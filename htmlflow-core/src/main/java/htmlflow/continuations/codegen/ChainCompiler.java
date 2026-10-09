/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow.continuations.codegen;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationSyncStatic;
import htmlflow.continuations.HtmlContinuationSyncValue;
import htmlflow.continuations.HtmlContinuationSyncValue.Kind;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Generates one {@link Renderer} class for each chain of static blocks and value slots. Every
 * accessor gets its own field and its own call site.
 *
 * <p>The class has two methods with the same straight-line body. render appends to a
 * StringBuilder, and its numbers go in without boxing. write appends to any other Appendable, which
 * only takes chars, so each number goes through String.valueOf first. Nothing is buffered.
 *
 * <p>Neither method branches, so they need no StackMapTable, and the stack and local counts are
 * constant. We write the class file directly, so this needs no bytecode library.
 *
 * <p>When something goes wrong, {@link #compile} returns null and the caller falls back to the
 * linked chain. A mistake here costs performance, not correctness.
 *
 * @author Bernardo Pereira
 */
public final class ChainCompiler {

    private static final Lookup LOOKUP = MethodHandles.lookup();

    /** Hidden classes land in the lookup's package, so the name must too. */
    private static final String GEN =
        "htmlflow/continuations/codegen/ChainRendererGen";

    private static final String RENDERER = internal(Renderer.class);
    private static final String VALUE_SLOT = internal(
        HtmlContinuationSyncValue.class
    );
    private static final String APPENDABLE = internal(Appendable.class);
    private static final String SB = internal(StringBuilder.class);
    private static final String OBJECT = internal(Object.class);
    private static final String STRING = internal(String.class);
    private static final String STRING_DESC = String.class.descriptorString();
    private static final String CHAR_SEQUENCE_DESC =
        CharSequence.class.descriptorString();
    private static final String OBJECT_DESC = Object.class.descriptorString();
    private static final String TAKES_OBJECT = method(OBJECT_DESC, "");

    /** Called by name from the generated code. See {@link #checkRuntimeHelpers}. */
    private static final String TEXT = "text";
    private static final String ATTR_NULLABLE = "attrNullable";

    private static final int HUGE_METHOD_LIMIT = 8000;

    /* The local variables of the generated methods. */
    private static final int THIS = 0;
    private static final int ACCESSORS_ARG = 1;
    private static final int OUT_LOCAL = 1;
    private static final int MODEL_LOCAL = 2;

    /* These are constant because the generated code has no branches. */
    private static final int INIT_MAX_STACK = 4;
    private static final int INIT_MAX_LOCALS = 2;
    private static final int RENDER_MAX_STACK = 6;
    private static final int RENDER_MAX_LOCALS = 3;

    private ChainCompiler() {}

    /**
     * @return A Renderer for the chain starting in first, or null when the chain holds other
     *     nodes than static blocks and value slots, when it is too large, or when the definition
     *     of the class failed.
     */
    public static Renderer compile(HtmlContinuation first) {
        Slots slots = Slots.of(first);
        if (slots == null) return null;
        try {
            checkRuntimeHelpers();
            byte[] classFile = classFile(slots);
            if (classFile == null) return null;
            Class<?> gen = LOOKUP
                .defineHiddenClass(classFile, true)
                .lookupClass();
            MethodHandle constructor = LOOKUP.findConstructor(
                gen,
                MethodType.methodType(void.class, Object[].class)
            );
            return (Renderer) constructor.invoke(slots.accessors);
        } catch (Throwable failed) {
            return null; // the caller uses the linked chain instead
        }
    }

    /**
     * Looks up the helpers the generated code calls by name. The JVM links invokestatic lazily,
     * so a wrong name would pass verification and only throw NoSuchMethodError on first render.
     */
    private static void checkRuntimeHelpers() throws NoSuchMethodException {
        HtmlContinuationSyncValue.class.getDeclaredMethod(TEXT, Object.class);
        HtmlContinuationSyncValue.class.getDeclaredMethod(
                ATTR_NULLABLE,
                Appendable.class,
                String.class,
                Object.class
            );
    }

    private static byte[] classFile(Slots slots) throws IOException {
        Kind[] kinds = slots.kinds;
        ClassFile gen = new ClassFile(GEN, OBJECT, RENDERER);
        for (int i = 0; i < kinds.length; i++) {
            gen.field("a" + i, desc(accessorFor(kinds[i]).type));
        }
        ClassFile.ConstantPool pool = gen.pool();
        gen.method(
            "<init>",
            method(Object[].class.descriptorString(), "V"),
            INIT_MAX_STACK,
            INIT_MAX_LOCALS,
            constructorCode(pool, slots)
        );
        ClassFile.Bytecode render = renderCode(pool, slots, Target.BUILDER);
        ClassFile.Bytecode write = renderCode(pool, slots, Target.APPENDABLE);
        if (Math.max(render.size(), write.size()) >= HUGE_METHOD_LIMIT) {
            return null;
        }
        gen.method(
            "render",
            method(desc(SB) + OBJECT_DESC, "V"),
            RENDER_MAX_STACK,
            RENDER_MAX_LOCALS,
            render
        );
        gen.method(
            "write",
            method(desc(APPENDABLE) + OBJECT_DESC, "V"),
            RENDER_MAX_STACK,
            RENDER_MAX_LOCALS,
            write
        );
        return gen.toBytes();
    }

    /** Generates the constructor, which casts each array element into its own field. */
    private static ClassFile.Bytecode constructorCode(
        ClassFile.ConstantPool pool,
        Slots slots
    ) throws IOException {
        Kind[] kinds = slots.kinds;
        ClassFile.Bytecode code = new ClassFile.Bytecode();
        code.aload(THIS);
        code.invokeSpecial(pool.method(OBJECT, "<init>", "()V"));
        for (int i = 0; i < kinds.length; i++) {
            String type = accessorFor(kinds[i]).type;
            code.aload(THIS);
            code.aload(ACCESSORS_ARG);
            code.pushInt(i);
            code.arrayLoad();
            code.checkCast(pool.clazz(type));
            code.putField(pool.field(GEN, "a" + i, desc(type)));
        }
        code.returnVoid();
        return code;
    }

    /** The output that a generated method appends to. */
    private enum Target {
        BUILDER,
        APPENDABLE,
    }

    private static ClassFile.Bytecode renderCode(
        ClassFile.ConstantPool pool,
        Slots slots,
        Target target
    ) throws IOException {
        String[] statics = slots.statics;
        Kind[] kinds = slots.kinds;
        String[] attrNames = slots.attrNames;
        ClassFile.Bytecode code = new ClassFile.Bytecode();
        for (int i = 0; i < kinds.length; i++) {
            emitStatic(code, pool, target, statics[i]);
            emitSlot(code, pool, target, i, kinds[i], attrNames[i]);
        }
        emitStatic(code, pool, target, statics[kinds.length]);
        code.returnVoid();
        return code;
    }

    /** Generates the append of a constant String. */
    private static void emitStatic(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        Target target,
        String text
    ) throws IOException {
        if (text.isEmpty()) return;
        code.aload(OUT_LOCAL);
        code.ldc(pool.string(text));
        appendAndPop(code, pool, target, STRING_DESC);
    }

    private static void emitSlot(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        Target target,
        int i,
        Kind kind,
        String attrName
    ) throws IOException {
        if (kind == Kind.ATTR_NULLABLE) {
            emitNullableAttribute(code, pool, i, attrName);
            return;
        }
        code.aload(OUT_LOCAL);
        if (kind == Kind.TEXT) {
            callAccessor(code, pool, i, Accessor.OBJECT);
            code.invokeStatic(
                pool.method(VALUE_SLOT, TEXT, TAKES_OBJECT + STRING_DESC)
            );
            appendAndPop(code, pool, target, STRING_DESC);
        } else {
            // A StringBuilder takes append(int), append(double) and so on, with no boxing. An
            // Appendable only takes chars, so the value goes through String.valueOf first. That is
            // also what append(Object) does, so null prints as "null" on both.
            Accessor accessor = accessorFor(kind);
            callAccessor(code, pool, i, accessor);
            String onStack = accessor.returns;
            if (target == Target.APPENDABLE) {
                code.invokeStatic(
                    pool.method(STRING, "valueOf", method(onStack, STRING_DESC))
                );
                onStack = STRING_DESC;
            }
            appendAndPop(code, pool, target, onStack);
        }
    }

    /** The interface, method and return descriptor for each accessor, kept together so they match. */
    private enum Accessor {
        INT(ToIntFunction.class, "applyAsInt", "I"),
        LONG(ToLongFunction.class, "applyAsLong", "J"),
        DOUBLE(ToDoubleFunction.class, "applyAsDouble", "D"),
        BOOLEAN(Predicate.class, "test", "Z"),
        OBJECT(Function.class, "apply", OBJECT_DESC);

        private final String type;
        private final String method;
        private final String returns;

        Accessor(Class<?> type, String method, String returns) {
            this.type = internal(type);
            this.method = method;
            this.returns = returns;
        }
    }

    private static Accessor accessorFor(Kind kind) {
        switch (kind) {
            case INT:
                return Accessor.INT;
            case LONG:
                return Accessor.LONG;
            case DOUBLE:
                return Accessor.DOUBLE;
            case BOOLEAN:
                return Accessor.BOOLEAN;
            default:
                return Accessor.OBJECT;
        }
    }

    /** Generates the call to attrNullable, which skips a null attribute. */
    private static void emitNullableAttribute(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        int i,
        String attrName
    ) throws IOException {
        code.aload(OUT_LOCAL);
        code.ldc(pool.string(attrName));
        callAccessor(code, pool, i, Accessor.OBJECT);
        code.invokeStatic(
            pool.method(
                VALUE_SLOT,
                ATTR_NULLABLE,
                method(desc(APPENDABLE) + STRING_DESC + OBJECT_DESC, "V")
            )
        );
    }

    /** Generates the accessor call. Each slot gets its own call site. */
    private static void callAccessor(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        int i,
        Accessor accessor
    ) throws IOException {
        code.aload(THIS);
        code.getField(pool.field(GEN, "a" + i, desc(accessor.type)));
        code.aload(MODEL_LOCAL);
        code.invokeInterface(
            pool.interfaceMethod(
                accessor.type,
                accessor.method,
                TAKES_OBJECT + accessor.returns
            )
        );
    }

    /** @param argDesc The type on the stack. An Appendable target takes only a String. */
    private static void appendAndPop(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        Target target,
        String argDesc
    ) throws IOException {
        if (target == Target.BUILDER) {
            code.invokeVirtual(
                pool.method(SB, "append", method(argDesc, desc(SB)))
            );
        } else {
            code.invokeInterface(
                pool.interfaceMethod(
                    APPENDABLE,
                    "append",
                    method(CHAR_SEQUENCE_DESC, desc(APPENDABLE))
                )
            );
        }
        code.pop();
    }

    private static String desc(String internalName) {
        return 'L' + internalName + ';';
    }

    /** Converts a name such as java.lang.String into java/lang/String. */
    private static String internal(Class<?> type) {
        return type.getName().replace('.', '/');
    }

    private static String method(String params, String returns) {
        return '(' + params + ')' + returns;
    }

    /** Emits the HTML of a chain of static blocks and value slots. */
    public interface Renderer {
        void render(StringBuilder sb, Object model);

        void write(Appendable out, Object model) throws IOException;
    }

    /** The chain as parallel arrays. Runs of static nodes merge into one append. */
    private static final class Slots {

        /** The text before slot i. The last entry holds the text after the last slot. */
        final String[] statics;

        final Kind[] kinds;
        final Object[] accessors;
        final String[] attrNames;

        private Slots(
            String[] statics,
            Kind[] kinds,
            Object[] accessors,
            String[] attrNames
        ) {
            this.statics = statics;
            this.kinds = kinds;
            this.accessors = accessors;
            this.attrNames = attrNames;
        }

        /** @return null when the chain holds other nodes than static blocks and value slots. */
        static Slots of(HtmlContinuation first) {
            int slots = 0;
            for (HtmlContinuation c = first; c != null; c = c.getNext()) {
                if (c instanceof HtmlContinuationSyncValue) {
                    slots++;
                } else if (!(c instanceof HtmlContinuationSyncStatic)) {
                    return null; // a dynamic block, an await or a loop
                }
            }
            if (slots == 0) return null;

            String[] statics = new String[slots + 1];
            Kind[] kinds = new Kind[slots];
            Object[] accessors = new Object[slots];
            String[] attrNames = new String[slots];

            StringBuilder pending = new StringBuilder();
            int i = 0;
            for (HtmlContinuation c = first; c != null; c = c.getNext()) {
                if (c instanceof HtmlContinuationSyncStatic) {
                    pending.append(
                        ((HtmlContinuationSyncStatic) c).staticHtmlBlock
                    );
                    continue;
                }
                HtmlContinuationSyncValue v = (HtmlContinuationSyncValue) c;
                statics[i] = pending.toString();
                pending.setLength(0);
                kinds[i] = v.kind();
                accessors[i] = v.accessor();
                attrNames[i] = v.attributeName();
                i++;
            }
            statics[slots] = pending.toString();
            return new Slots(statics, kinds, accessors, attrNames);
        }
    }
}

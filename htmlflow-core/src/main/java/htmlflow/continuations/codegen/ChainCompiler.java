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
import htmlflow.visitor.HtmlVisitor;
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
 * <p>The generated render method never branches, so it needs no StackMapTable, and the stack and
 * local counts are constant. We write the class file directly, so this needs no bytecode library.
 *
 * <p>When something goes wrong, {@link #compile} returns null and the caller falls back to the
 * linked chain. A mistake here costs performance, not correctness.
 *
 * @author Bernardo Pereira
 */
public final class ChainCompiler {

    private static final Lookup LOOKUP = MethodHandles.lookup();

    /** A hidden class is defined in the lookup's own package, so this name lives there too. */
    private static final String GEN =
        "htmlflow/continuations/codegen/ChainRendererGen";

    private static final String RENDERER = internal(Renderer.class);
    private static final String VALUE_SLOT = internal(
        HtmlContinuationSyncValue.class
    );
    private static final String VISITOR = internal(HtmlVisitor.class);
    private static final String SB = internal(StringBuilder.class);
    private static final String OBJECT = internal(Object.class);
    private static final String STRING = internal(String.class);
    private static final String STRING_DESC = String.class.descriptorString();
    private static final String OBJECT_DESC = Object.class.descriptorString();
    private static final String TAKES_OBJECT = method(OBJECT_DESC, "");

    /** The methods that the generated code invokes by name. See {@link #checkRuntimeHelpers}. */
    private static final String TEXT = "text";
    private static final String ATTR_NULLABLE = "attrNullable";

    /**
     * The HugeMethodLimit of HotSpot. Past this size C2 never compiles the render method, and
     * nothing reports it, so we measure the actual size instead of counting slots.
     */
    private static final int HUGE_METHOD_LIMIT = 8000;

    /* The local variables of the generated methods. */
    private static final int THIS = 0;
    private static final int ACCESSORS_ARG = 1;
    private static final int VISITOR_LOCAL = 1;
    private static final int SB_LOCAL = 2;
    private static final int MODEL_LOCAL = 3;

    /* These are constant because the generated code has no branches. */
    private static final int INIT_MAX_STACK = 4;
    private static final int INIT_MAX_LOCALS = 2;
    private static final int RENDER_MAX_STACK = 6;
    private static final int RENDER_MAX_LOCALS = 4;

    private ChainCompiler() {}

    /**
     * @return A Renderer for the chain starting in first, or null when the chain holds other
     *     nodes than static blocks and value slots, when it is too large, or when the definition
     *     of the class failed.
     */
    public static Renderer compile(HtmlContinuation first) {
        Slots flat = Slots.of(first);
        if (flat == null) return null;
        try {
            checkRuntimeHelpers();
            byte[] classFile = classFile(flat);
            if (classFile == null) return null;
            Class<?> gen = LOOKUP
                .defineHiddenClass(classFile, true)
                .lookupClass();
            MethodHandle constructor = LOOKUP.findConstructor(
                gen,
                MethodType.methodType(void.class, Object[].class)
            );
            return (Renderer) constructor.invoke(flat.accessors);
        } catch (Throwable failed) {
            return null; // the caller uses the linked chain instead
        }
    }

    /**
     * Resolves the methods that the generated code invokes by name. The JVM resolves an
     * invokestatic lazily, so without this check a wrong name would verify cleanly and only
     * throw NoSuchMethodError on the first render.
     */
    private static void checkRuntimeHelpers() throws NoSuchMethodException {
        HtmlContinuationSyncValue.class.getDeclaredMethod(TEXT, Object.class);
        HtmlContinuationSyncValue.class.getDeclaredMethod(
                ATTR_NULLABLE,
                HtmlVisitor.class,
                String.class,
                Object.class
            );
    }

    private static byte[] classFile(Slots flat) throws IOException {
        Kind[] kinds = flat.kinds;
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
            constructorCode(pool, flat)
        );
        ClassFile.Bytecode renderBody = renderCode(pool, flat);
        if (renderBody.size() >= HUGE_METHOD_LIMIT) return null;
        gen.method(
            "render",
            method(desc(VISITOR) + desc(SB) + OBJECT_DESC, "V"),
            RENDER_MAX_STACK,
            RENDER_MAX_LOCALS,
            renderBody
        );
        return gen.toBytes();
    }

    /** Generates the constructor, which casts each element of the array into its own field. */
    private static ClassFile.Bytecode constructorCode(
        ClassFile.ConstantPool pool,
        Slots flat
    ) throws IOException {
        Kind[] kinds = flat.kinds;
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

    private static ClassFile.Bytecode renderCode(
        ClassFile.ConstantPool pool,
        Slots flat
    ) throws IOException {
        String[] statics = flat.statics;
        Kind[] kinds = flat.kinds;
        String[] attrNames = flat.attrNames;
        ClassFile.Bytecode code = new ClassFile.Bytecode();
        for (int i = 0; i < kinds.length; i++) {
            emitStatic(code, pool, statics[i]);
            emitSlot(code, pool, i, kinds[i], attrNames[i]);
        }
        emitStatic(code, pool, statics[kinds.length]);
        code.returnVoid();
        return code;
    }

    /** Generates the append of one constant String. */
    private static void emitStatic(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        String text
    ) throws IOException {
        if (text.isEmpty()) return;
        code.aload(SB_LOCAL);
        code.ldc(pool.string(text));
        appendAndPop(code, pool, STRING_DESC);
    }

    private static void emitSlot(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        int i,
        Kind kind,
        String attrName
    ) throws IOException {
        if (kind == Kind.ATTR_NULLABLE) {
            emitNullableAttribute(code, pool, i, attrName);
            return;
        }
        Accessor accessor = accessorFor(kind);
        code.aload(SB_LOCAL);
        if (kind == Kind.TEXT) {
            appendConverted(code, pool, i, VALUE_SLOT, TEXT);
        } else if (accessor == Accessor.OBJECT) {
            // The append(Object) of StringBuilder already calls String.valueOf, also for null.
            callAccessor(code, pool, i, accessor);
            appendAndPop(code, pool, OBJECT_DESC);
        } else {
            appendPrimitive(code, pool, i, accessor);
        }
    }

    /**
     * The interface, the method and the return descriptor that we use to invoke the accessor of
     * each Kind. We keep them together so that they cannot diverge.
     */
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

    /** Generates the append of a primitive, without boxing and without intermediate String. */
    private static void appendPrimitive(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        int i,
        Accessor accessor
    ) throws IOException {
        callAccessor(code, pool, i, accessor);
        appendAndPop(code, pool, accessor.returns);
    }

    /** Generates the append of a value that we convert to a String first. */
    private static void appendConverted(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        int i,
        String converterOwner,
        String converter
    ) throws IOException {
        callAccessor(code, pool, i, Accessor.OBJECT);
        code.invokeStatic(
            pool.method(converterOwner, converter, TAKES_OBJECT + STRING_DESC)
        );
        appendAndPop(code, pool, STRING_DESC);
    }

    /** Generates the call to attrNullable, which writes the attribute when it is not null. */
    private static void emitNullableAttribute(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        int i,
        String attrName
    ) throws IOException {
        code.aload(VISITOR_LOCAL);
        code.ldc(pool.string(attrName));
        callAccessor(code, pool, i, Accessor.OBJECT);
        code.invokeStatic(
            pool.method(
                VALUE_SLOT,
                ATTR_NULLABLE,
                method(desc(VISITOR) + STRING_DESC + OBJECT_DESC, "V")
            )
        );
    }

    /** Generates the invocation of the accessor, which has one call site for each slot. */
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

    private static void appendAndPop(
        ClassFile.Bytecode code,
        ClassFile.ConstantPool pool,
        String argDesc
    ) throws IOException {
        code.invokeVirtual(
            pool.method(SB, "append", method(argDesc, desc(SB)))
        );
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
        void render(HtmlVisitor visitor, StringBuilder sb, Object model);
    }

    /**
     * The chain in parallel arrays, which is the shape that the generator reads. We join a
     * sequence of static nodes so that it costs a single append.
     */
    private static final class Slots {

        /** The constant text before the slot i. The last position holds the remaining text. */
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

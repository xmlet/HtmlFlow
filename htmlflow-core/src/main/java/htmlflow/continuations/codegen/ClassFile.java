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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class file that we build in the order that the JVM specification defines, i.e. the constant
 * pool, then the fields and then the methods. Nothing here depends on HtmlFlow.
 *
 * @author Bernardo Pereira
 */
final class ClassFile {

    private static final int MAGIC = 0xCAFEBABE;
    private static final int MAJOR_JAVA_17 = 61;

    static final int ACC_PUBLIC = 0x0001;
    private static final int ACC_PRIVATE = 0x0002;
    static final int ACC_FINAL = 0x0010;
    static final int ACC_SUPER = 0x0020;

    private final ConstantPool pool = new ConstantPool();
    private final List<int[]> fields = new ArrayList<>();
    private final List<Method> methods = new ArrayList<>();
    private final int thisClass;
    private final int superClass;
    private final int iface;
    private final int codeAttr;

    ClassFile(String name, String superName, String interfaceName)
        throws IOException {
        this.thisClass = pool.clazz(name);
        this.superClass = pool.clazz(superName);
        this.iface = pool.clazz(interfaceName);
        this.codeAttr = pool.utf8("Code");
    }

    ConstantPool pool() {
        return pool;
    }

    void field(String name, String desc) throws IOException {
        fields.add(new int[] { pool.utf8(name), pool.utf8(desc) });
    }

    void method(
        String name,
        String desc,
        int maxStack,
        int maxLocals,
        Bytecode code
    ) throws IOException {
        methods.add(
            new Method(
                pool.utf8(name),
                pool.utf8(desc),
                maxStack,
                maxLocals,
                code
            )
        );
    }

    byte[] toBytes() throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(raw);
        out.writeInt(MAGIC);
        out.writeShort(0); // minor version
        out.writeShort(MAJOR_JAVA_17);
        pool.writeTo(out);
        out.writeShort(ACC_PUBLIC | ACC_FINAL | ACC_SUPER);
        out.writeShort(thisClass);
        out.writeShort(superClass);
        out.writeShort(1); // one interface
        out.writeShort(iface);

        out.writeShort(fields.size());
        for (int[] f : fields) {
            out.writeShort(ACC_PRIVATE | ACC_FINAL);
            out.writeShort(f[0]);
            out.writeShort(f[1]);
            out.writeShort(0); // field attributes
        }

        out.writeShort(methods.size());
        for (Method m : methods) m.writeTo(out, codeAttr);

        out.writeShort(0); // class attributes
        return raw.toByteArray();
    }

    /**
     * A method_info and its Code attribute. It needs no StackMapTable because the generated code
     * has no branches.
     */
    private static final class Method {

        private final int name;
        private final int desc;
        private final int maxStack;
        private final int maxLocals;
        private final Bytecode code;

        Method(int name, int desc, int maxStack, int maxLocals, Bytecode code) {
            this.name = name;
            this.desc = desc;
            this.maxStack = maxStack;
            this.maxLocals = maxLocals;
            this.code = code;
        }

        void writeTo(DataOutputStream out, int codeAttr) throws IOException {
            out.writeShort(ACC_PUBLIC);
            out.writeShort(name);
            out.writeShort(desc);
            out.writeShort(1); // one attribute, Code
            out.writeShort(codeAttr);
            int length = code.size();
            out.writeInt(12 + length); // maxs, length, code, two empty tables
            out.writeShort(maxStack);
            out.writeShort(maxLocals);
            out.writeInt(length);
            code.writeTo(out);
            out.writeShort(0); // exception table
            out.writeShort(0); // no StackMapTable because nothing branches
        }
    }

    /** The constant pool. We reuse an entry whenever the same value appears again. */
    static final class ConstantPool {

        private final Map<String, Integer> seen = new HashMap<>();
        private final ByteArrayOutputStream raw = new ByteArrayOutputStream();
        private final DataOutputStream out = new DataOutputStream(raw);
        private int next = 1; // index zero is not a valid entry

        int utf8(String value) throws IOException {
            return intern(
                "u" + value,
                () -> {
                    out.writeByte(1);
                    out.writeUTF(value);
                }
            );
        }

        int string(String value) throws IOException {
            return intern(
                "s" + value,
                () -> {
                    int text = utf8(value);
                    out.writeByte(8);
                    out.writeShort(text);
                }
            );
        }

        int clazz(String internalName) throws IOException {
            return intern(
                "c" + internalName,
                () -> {
                    int name = utf8(internalName);
                    out.writeByte(7);
                    out.writeShort(name);
                }
            );
        }

        int field(String owner, String name, String desc) throws IOException {
            return ref(9, owner, name, desc);
        }

        int method(String owner, String name, String desc) throws IOException {
            return ref(10, owner, name, desc);
        }

        int interfaceMethod(String owner, String name, String desc)
            throws IOException {
            return ref(11, owner, name, desc);
        }

        void writeTo(DataOutputStream target) throws IOException {
            out.flush();
            target.writeShort(next); // the count is one past the last index
            raw.writeTo(target);
        }

        private int ref(int tag, String owner, String name, String desc)
            throws IOException {
            return intern(
                tag + owner + '.' + name + desc,
                () -> {
                    int type = clazz(owner);
                    int signature = nameAndType(name, desc);
                    out.writeByte(tag);
                    out.writeShort(type);
                    out.writeShort(signature);
                }
            );
        }

        /**
         * Returns the index that this key already has, or writes the entry and takes the next
         * index. We write the entry first so that a nested entry takes a lower index, as the
         * class file format requires.
         */
        private int intern(String key, Entry entry) throws IOException {
            Integer known = seen.get(key);
            if (known != null) return known;
            entry.write();
            seen.put(key, next);
            return next++;
        }

        private interface Entry {
            void write() throws IOException;
        }

        private int nameAndType(String name, String desc) throws IOException {
            return intern(
                "n" + name + desc,
                () -> {
                    int n = utf8(name);
                    int d = utf8(desc);
                    out.writeByte(12);
                    out.writeShort(n);
                    out.writeShort(d);
                }
            );
        }
    }

    /** The bytes of the body of one method. */
    static final class Bytecode {

        private static final int ALOAD_0 = 0x2a;
        private static final int ICONST_0 = 0x03;
        private static final int BIPUSH = 0x10;
        private static final int SIPUSH = 0x11;
        private static final int LDC = 0x12;
        private static final int LDC_W = 0x13;
        private static final int AALOAD = 0x32;
        private static final int POP = 0x57;
        private static final int RETURN = 0xb1;
        private static final int GETFIELD = 0xb4;
        private static final int PUTFIELD = 0xb5;
        private static final int INVOKEVIRTUAL = 0xb6;
        private static final int INVOKESPECIAL = 0xb7;
        private static final int INVOKESTATIC = 0xb8;
        private static final int INVOKEINTERFACE = 0xb9;
        private static final int CHECKCAST = 0xc0;

        private final ByteArrayOutputStream raw = new ByteArrayOutputStream();

        /** Loads a local from 0 to 3, which are all the locals that the generated code uses. */
        void aload(int local) {
            raw.write(ALOAD_0 + local);
        }

        void arrayLoad() {
            raw.write(AALOAD);
        }

        void pop() {
            raw.write(POP);
        }

        void returnVoid() {
            raw.write(RETURN);
        }

        void getField(int field) {
            operand(GETFIELD, field);
        }

        void putField(int field) {
            operand(PUTFIELD, field);
        }

        void checkCast(int type) {
            operand(CHECKCAST, type);
        }

        void invokeSpecial(int method) {
            operand(INVOKESPECIAL, method);
        }

        void invokeVirtual(int method) {
            operand(INVOKEVIRTUAL, method);
        }

        void invokeStatic(int method) {
            operand(INVOKESTATIC, method);
        }

        /**
         * The invokeinterface is the only invoke with a count operand, which is the number of
         * argument slots plus the receiver.
         */
        void invokeInterface(int method) {
            operand(INVOKEINTERFACE, method);
            raw.write(2);
            raw.write(0);
        }

        void ldc(int constant) {
            if (constant <= 0xFF) {
                raw.write(LDC);
                raw.write(constant);
            } else {
                operand(LDC_W, constant);
            }
        }

        void pushInt(int value) {
            if (value <= 5) {
                raw.write(ICONST_0 + value);
            } else if (value <= Byte.MAX_VALUE) {
                raw.write(BIPUSH);
                raw.write(value);
            } else {
                operand(SIPUSH, value);
            }
        }

        int size() {
            return raw.size();
        }

        void writeTo(DataOutputStream target) throws IOException {
            raw.writeTo(target);
        }

        private void operand(int opcode, int index) {
            raw.write(opcode);
            raw.write(index >>> 8);
            raw.write(index);
        }
    }
}

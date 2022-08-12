/*
 * MIT License
 *
 * Copyright (c) 2014-16, Miguel Gamboa (gamboa.pt)
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

package htmlflow.test;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 *  Unit Test to coverage the case where ClassLoader does not find
 *  resource for HTML template.
 *  To that end we use a custom ClassLoader, the ClassLoaderGw, that
 *  will fail to load resources.
 *
 * @author Miguel Gamboa
 *         created on 20-09-2017
 */
public class TestResourceNotFound {

    @Test(expected = ExceptionInInitializerError.class)
    public void testHtmlViewHeaderNotFound()
        throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        ClassLoaderGw gw = new ClassLoaderGw();
        Class<?> klassView = gw.loadClass("htmlflow.HtmlPage");
        Field f = klassView.getDeclaredField("HEADER");
        f.setAccessible(true);
        Object header = f.get(null);
    }
}

class ClassLoaderGw extends ClassLoader {

    public ClassLoaderGw() {
        super(null);
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        try{
            InputStream classData = ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class");
            if(classData == null) {
                throw new ClassNotFoundException("class " + name + " is not findable");
            }
            byte[] array = bytes(classData);
            return defineClass(name, array, 0, array.length);
        } catch(IOException ex) {
            throw new ClassNotFoundException(name);
        }
    }

    private static byte[] bytes(InputStream is) throws IOException{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}


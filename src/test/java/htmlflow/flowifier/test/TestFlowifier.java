/*
 * MIT License
 *
 * Copyright (c) 2014-19 HtmlFlow.org
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
package htmlflow.flowifier.test;

import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.flowifier.Flowifier;
import htmlflow.test.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class TestFlowifier {

    /**
     * A file object used to represent source coming from a string.
     */
    private static final class JavaSourceFromString extends SimpleJavaFileObject {
        /**
         * The source code of this "file".
         */
        private final String code;

        /**
         * Constructs a new JavaSourceFromString.
         * 
         * @param name
         *            the name of the compilation unit represented by this file
         *            object
         * @param code
         *            the source code for the compilation unit represented by
         *            this file object
         */
        private JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    @Test
    public void testFlowifierTuerSourceforgeHomepage() throws Exception {
        testFlowifier("http://tuer.sourceforge.net/en/");
    }

    @Test
    public void testFlowifierWithJavasyncOrg() throws Exception {
        testFlowifier("https://javasync.org");
    }

    @Test
    public void testFlowifierWithEclemmaOrg() throws Exception {
        testFlowifier("https://www.eclemma.org/");
    }

    @Test
    public void testSample05ForFlowifier() throws IOException {
        String src = "<!DOCTYPE html>" +
            "<html>" +
            "<head><title>HtmlFlow</title></head>" +
            "<body>" +
            "<div class=\"container\">" +
            "<h1>My first page with HtmlFlow</h1>" +
            "<img src=\"https://avatars1.githubusercontent.com/u/35267172\">" +
            "<p>Typesafe is awesome! :-)</p>" +
            "</div>" +
            "</body>" +
            "</html>";
        String actual = Flowifier.fromHtml(src);
        Iterator<String> iter = Arrays.asList(actual.split("\n")).iterator();
        Assert.assertEquals(true, iter.hasNext());
        Utils
                .loadLines("htmlflowSample05ForFlowifier.java")
                .forEach(expected -> {
                    String line = iter.next();
                    assertEquals(expected, line);
                });
        Assert.assertEquals(false, iter.hasNext());
    }

    private void testFlowifier(final String url) throws Exception {
        final Flowifier flowifier = new Flowifier();
        final String htmlFlowJavaClassSourceCodeWithHtmlViewGetter = flowifier.toFlow(url);
        // shows the generated Java source code
        Logger.getLogger("htmlflow.flowifier.test").info(htmlFlowJavaClassSourceCodeWithHtmlViewGetter);
        // compiles this Java class
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        final String className = "Flowified";
        final JavaSourceFromString compilationUnit = new JavaSourceFromString(className,
                htmlFlowJavaClassSourceCodeWithHtmlViewGetter);
        final CompilationTask compilerTask = compiler.getTask(null, fileManager, null,
                Arrays.asList("-classpath", System.getProperty("java.class.path")), null,
                Collections.singletonList(compilationUnit));
        final boolean compilationSuccessful = compilerTask.call();
        Assert.assertTrue(compilationSuccessful);
        if (compilationSuccessful) {
            // gets the default location of the class files
            final JavaFileObject classFileObject = fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT,
                    className, Kind.CLASS, null);
            // gets the content of the generated class file
            final URI classFileUri = classFileObject.toUri();
            final Path classFilePath = Paths.get(classFileUri);
            final byte[] classFileContent = Files.readAllBytes(classFilePath);
            // deletes the class file to keep the workspace clean
            classFileObject.delete();
            // gets the class loader with the classpath containing the necessary
            // third party dependencies
            final ClassLoader classLoader = getClass().getClassLoader();
            // creates a custom class loader that knows the third party
            // dependencies by using the previous class loader as parent
            // such a class loader is necessary because it's impossible to
            // modify an existing class loader without getting some warnings
            final ClassLoader customClassLoader = new ClassLoader(classLoader) {
                @Override
                protected Class<?> findClass(final String name) throws ClassNotFoundException {
                    final Class<?> result;
                    if (name.equals(className)) {
                        result = defineClass(name, classFileContent, 0, classFileContent.length);
                    } else {
                        result = super.findClass(name);
                    }
                    return result;
                }
            };
            // loads the generated class into the custom class loader
            final Class<?> generatedClass = customClassLoader.loadClass(className);
            // gets the single declared method
            final Method getHtmlViewMethod = generatedClass.getMethod("get", StringBuilder.class);
            // Produces the HTML output from the HtmlDoc
            StringBuilder out = new StringBuilder();
            getHtmlViewMethod.invoke(null, out);
            final String generatedHtmlSourceCode = out.toString();
            Logger.getLogger("htmlflow.flowifier.test").info(generatedHtmlSourceCode);
            // gets the original HTML document
            final Document doc = Jsoup.connect(url).get();
            final String originalHtmlSourceCode = doc.root().outerHtml();
            Logger.getLogger("htmlflow.flowifier.test").info(originalHtmlSourceCode);
            // compares the original HTML to the generated HTML
            final Iterator<String> expected = Arrays
                .stream(originalHtmlSourceCode.split("<"))
                .skip(2)
                .flatMap(l -> Arrays.stream(l.split(";")))
                .iterator();
            final Iterator<String> actual = Arrays
                .stream(generatedHtmlSourceCode.split("<"))
                .skip(2)
                .flatMap(l -> Arrays.stream(l.split(";")))
                .iterator();
            while (expected.hasNext() || actual.hasNext()) {
                final String expectedPart = expected.hasNext() ? expected.next().replaceAll("\\s", "").replaceAll("\\h", "").replaceAll("\\v", "").toLowerCase() : "";
                final String actualPart = actual.hasNext() ? actual.next().replaceAll("\\s", "").replaceAll("\\h", "").replaceAll("\\v", "").toLowerCase() : "";
                Assert.assertEquals(expectedPart, actualPart);
            }
        }
    }
}

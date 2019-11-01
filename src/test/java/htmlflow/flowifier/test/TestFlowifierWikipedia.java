package htmlflow.flowifier.test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Test;
import junit.framework.Assert;

import htmlflow.flowifier.Flowifier;

public class TestFlowifierWikipedia {

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
         * @param name the name of the compilation unit represented by this file object
         * @param code the source code for the compilation unit represented by this file object
         */
        private JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                  Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
	
    @Test
    public void testFlowifierTuerSourceforgeHomepage() throws IOException {
		testFlowifier("http://tuer.sourceforge.net/en/");
	}
    
	@Test
    public void testFlowifierWikipediaHomepage() throws IOException {
		testFlowifier("https://en.wikipedia.org");
	}
	
	private void testFlowifier(final String url) throws IOException {
		final Flowifier flowifier = new Flowifier();
		final String htmlFlowJavaClassSourceCodeWithHtmlViewGetter = flowifier.toFlow(url);
		// shows the generated Java source code
		Logger.getLogger("htmlflow.flowifier.test").info(htmlFlowJavaClassSourceCodeWithHtmlViewGetter);
		// compiles this Java class
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final CompilationTask compilerTask = compiler.getTask(null, null, null, 
				Arrays.asList("-classpath",System.getProperty("java.class.path")), null, 
				Collections.singletonList(new JavaSourceFromString("Flowified", htmlFlowJavaClassSourceCodeWithHtmlViewGetter)));
		Assert.assertTrue(compilerTask.call());
	}
}

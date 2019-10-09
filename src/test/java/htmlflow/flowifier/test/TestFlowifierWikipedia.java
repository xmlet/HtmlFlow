package htmlflow.flowifier.test;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;
import junit.framework.Assert;

import htmlflow.flowifier.Flowifier;

public class TestFlowifierWikipedia {

	@Test
    public void testFlowifierWikipediaHomepage() throws IOException {
		final Flowifier flowifier = new Flowifier();
		final String htmlFlowJavaClassSourceCodeWithHtmlViewGetter = flowifier.toFlow("https://en.wikipedia.org");
		Logger.getLogger("htmlflow.flowifier.test").info(htmlFlowJavaClassSourceCodeWithHtmlViewGetter);
		Assert.assertTrue(htmlFlowJavaClassSourceCodeWithHtmlViewGetter != null && htmlFlowJavaClassSourceCodeWithHtmlViewGetter.contains(".text(\"Wikipedia, the free encyclopedia\")"));
	}
}

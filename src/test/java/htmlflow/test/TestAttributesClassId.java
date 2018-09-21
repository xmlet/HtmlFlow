/*
 * MIT License
 *
 * Copyright (c) 2015-16, Mikael KROK
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
/**
 * 
 */
package htmlflow.test;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import org.junit.Test;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Html;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static htmlflow.test.Utils.htmlWrite;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mikael KROK
 *
 */
public class TestAttributesClassId {

  private static final Logger LOGGER = Logger.getLogger("htmlflow.test");
  private static final String DIV_NAME = Div.class.getSimpleName().toLowerCase();

  @Test
  public void testGetElementName() {
    Div<Body<Html<HtmlView>>> div = StaticHtml.view().html().body().div();
    assertEquals(DIV_NAME, div.getName());
  }

  @Test
  public void testIdAndClassAttribute() {

    Task t1 = new Task("Unit Test", "Test of element name", Priority.High, Status.Progress);
    ByteArrayOutputStream mem = new ByteArrayOutputStream();
    DynamicHtml
      .view(new PrintStream(mem), HtmlLists::taskView)
      .write(t1);
    List<String> actual = htmlWrite(mem).collect(toList());

    String result = actual.stream().collect(joining("\n"));
    // System.out.println(result);
    assertTrue(result.contains("<div"));
    assertTrue(result.contains("</div>"));
    assertTrue(result.contains(HtmlLists.divClass));
    assertTrue(result.contains(HtmlLists.divId));
    // !!!!!! Missing feature in HtmlApiFaster !!!!
    // assertTrue(result.contains("toto=\"tutu\""));
    assertTrue("should contains <script type=\"text/javascript\" src=\"test.css\">",
              result.contains("<script type=\"text/javascript\" src=\"test.css\">"));

    Iterator<String> iter = actual.iterator();
    Utils
            .loadLines("testIdAndClassAttribute.html")
            .forEach(expected -> assertEquals(expected, iter.next()));
  }
}

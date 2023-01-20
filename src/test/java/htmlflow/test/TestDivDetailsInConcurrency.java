/*
 * MIT License
 *
 * Copyright (c) 2014-23, Miguel Gamboa (gamboa.pt)
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

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import htmlflow.test.model.Priority;
import htmlflow.test.model.TaskDelayed;
import htmlflow.test.views.HtmlLists;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static htmlflow.test.Utils.htmlRender;
import static htmlflow.test.Utils.loadLines;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 * Created on 18-01-2023.
 */
public class TestDivDetailsInConcurrency {

    private static final List<TaskDelayed> models = asList(
            new TaskDelayed(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new TaskDelayed(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
            new TaskDelayed(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High),
            new TaskDelayed(9, "Web Summit 2018", "Sir Tim Berners-Lee", Priority.High));

    private static final Map<TaskDelayed, List<String>> expectedViews = models
            .stream()
            .collect(Collectors.toMap(
                    identity(),
                    task -> loadLines(format("task%d.html", task.getId())).collect(toList())
            ));

    private static final HtmlView unsafeThreadView = HtmlFlow.view(HtmlLists::taskDetailsTemplate);

    @Test
    public void testDivDetailsBindingWithRenderInParallelThreadSafe(){
        testDivDetailsBindingWithRenderInParallel(unsafeThreadView.threadSafe());
    }

    /**
     * We cannot be more specific about the expected error in unsafe multithreading scenarios
     * because the wrong behavior is unexpected.
     */
    @Test(expected = Throwable.class)
    public void testDivDetailsBindingWithRenderInParallelNonThreadSafe(){
        testDivDetailsBindingWithRenderInParallel(unsafeThreadView);
    }


    public void testDivDetailsBindingWithRenderInParallel(HtmlView view) {

        models
            .stream()
            .parallel()
            .map(task -> ModelAndView.of(task, htmlRender(view, task)))
            .forEach(taskHtml -> {
                // taskHtml.html.forEach(System.out::println);

                Iterator<String> actual = taskHtml.html.iterator();
                expectedViews
                        .get(taskHtml.obj)
                        .forEach(line -> assertEquals(line, actual.next()));

            });
    }

    private static class ModelAndView {
        final TaskDelayed obj;
        final Stream<String> html;
        public ModelAndView(TaskDelayed obj, Stream<String> html) {
            this.obj = obj;
            this.html = html;
        }
        static ModelAndView of(TaskDelayed t, Stream<String> html) {
            return new ModelAndView(t, html);
        }
    }
}

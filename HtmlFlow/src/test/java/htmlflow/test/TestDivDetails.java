package htmlflow.test;

import htmlflow.HtmlView;
import org.junit.Test;

import java.io.*;

/**
 * Created by mcarvalho on 18-01-2016.
 */
public class TestDivDetails {
    @Test
    public void test_div_details_without_binding() throws IOException {
        HtmlView<?> taskView = new HtmlView<>();
        taskView
                .head()
                .title("Task Details")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
                .body().classAttr("container")
                .heading(1, "Task Details")
                .hr()
                .div()
                .text("Title: ").text("ISEL MPD project")
                .br()
                .text("Description: ").text("A Java library for serializing objects in HTML.")
                .br()
                .text("Priority: ").text("HIGH");
        //
        // Produces an HTML file document
        //
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        try(PrintStream out = new PrintStream(mem)){
            taskView.setPrintStream(out).write();
            System.out.println(mem.toString());
        }
    }
}

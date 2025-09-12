package htmlflow.test;

import htmlflow.HtmlFlow;
import org.junit.Test;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

import static org.junit.Assert.assertEquals;

public class TestNewScriptTypes {
    static final String EXPECTED_SCRIPT_MODULE = "" +
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<script type=\"module\"></script>" +
            "</head>" +
            "</html>";

    @Test
    public void view_script_type_module(){
        StringBuilder buffer = new StringBuilder();
        HtmlFlow
                .doc(buffer)
                .setIndented(false)
                .html()
                    .head()
                        .script().attrType(EnumTypeScriptType.MODULE).__()
                    .__() //head
                .__(); // html
        assertEquals(EXPECTED_SCRIPT_MODULE, buffer.toString());
    }

}

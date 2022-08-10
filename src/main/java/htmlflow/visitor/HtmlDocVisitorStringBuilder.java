package htmlflow.visitor;

import java.io.PrintStream;

public class HtmlDocVisitorStringBuilder extends HtmlDocVisitor {
    /**
     * The main StringBuilder. Read by the finished() to return the
     * resulting string with the Html content.
     */
    protected final StringBuilder sb = new StringBuilder();

    public HtmlDocVisitorStringBuilder(boolean isIndented) {
        super(isIndented);
    }

    public HtmlDocVisitorStringBuilder(boolean isIndented, int depth) {
        super(isIndented);
        this.depth = depth;
    }

    @Override
    public final String finished() {
        return sb.toString();
    }

    @Override
    protected final void beginTag(String elementName) {
        Tags.appendOpenTag(sb, elementName); // "<elementName"
    }

    @Override
    protected final void endTag(String elementName) {
        Tags.appendCloseTag(sb, elementName); // </elementName>
    }

    @Override
    protected final void addAttribute(String attributeName, String attributeValue) {
        Tags.appendAttribute(sb, attributeName, attributeValue);
    }

    @Override
    protected final void addComment(String comment) {
        Tags.appendComment(sb, comment);
    }

    @Override
    public final void write(String text) {
        sb.append(text);
    }
    @Override
    protected final void write(char c) {
        sb.append(c);
    }

    @Override
    protected final int size() {
        return sb.length();
    }

    @Override
    public final HtmlDocVisitor clone(PrintStream out, boolean isIndented) {
        if(out != null)
            throw new IllegalArgumentException("This HtmlVisitor emits to StringBuilder and does not support PrintStream!");
        return new HtmlDocVisitorStringBuilder(isIndented);
    }
}

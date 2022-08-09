package htmlflow.visitor;

import java.io.PrintStream;

public class HtmlVisitorStringBuilder extends HtmlVisitor {
    /**
     * The main StringBuilder. Read by the finished() to return the
     * resulting string with the Html content.
     */
    protected final StringBuilder sb = new StringBuilder();

    public HtmlVisitorStringBuilder(boolean isIndented) {
        super(isIndented);
    }

    public HtmlVisitorStringBuilder(boolean isIndented, int depth) {
        super(isIndented);
        this.depth = depth;
    }

    @Override
    public final HtmlVisitor newbie() {
        return new HtmlVisitorStringBuilder(isIndented, depth);
    }

    @Override
    public final String finished() {
        String data = sb.toString();
        return data;
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

    /**
     * Since this Visitor immediately emits on each element visit then it is always writing.
     * @return
     */
    @Override
    public final boolean isWriting() {
        return true;
    }

    @Override
    protected final int size() {
        return sb.length();
    }

    @Override
    public final HtmlVisitor clone(PrintStream out, boolean isIndented) {
        if(out != null)
            throw new IllegalArgumentException("This HtmlVisitor emits to StringBuilder and does not support PrintStream!");
        return new HtmlVisitorStringBuilder(isIndented);
    }
}

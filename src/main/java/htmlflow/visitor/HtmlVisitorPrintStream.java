package htmlflow.visitor;

import java.io.PrintStream;

public class HtmlVisitorPrintStream extends HtmlVisitor {
    /**
     * The final PrintStream destination of the HTML content
     * produced by this visitor.
     */
    private final PrintStream out;

    public HtmlVisitorPrintStream(PrintStream out, boolean isIndented) {
        this(out, isIndented, 0);
    }

    public HtmlVisitorPrintStream(PrintStream out, boolean isIndented, int depth) {
        super(isIndented);
        this.out = out;
        this.depth = depth;
    }

    @Override
    public final HtmlVisitor newbie() {
        return new HtmlVisitorPrintStream(out, isIndented, depth);
    }

    @Override
    public final String finished() {
        throw new UnsupportedOperationException("Do not call finished() on HtmlVisitorPrintStream because" +
            "HTML fragments have been already emitted on each element call.");
    }

    @Override
    protected final void beginTag(String elementName) {
        Tags.printOpenTag(out, elementName); // "<elementName"
    }

    @Override
    protected final void endTag(String elementName) {
        Tags.printCloseTag(out, elementName); // </elementName>
    }

    @Override
    protected final void addAttribute(String attributeName, String attributeValue) {
        Tags.printAttribute(out, attributeName, attributeValue);
    }

    @Override
    protected final void addComment(String comment) {
        Tags.printComment(out, comment);
    }

    @Override
    public final void write(String text) {
        out.print(text);
    }
    @Override
    protected final void write(char c) {
        out.print(c);
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
        throw new UnsupportedOperationException("HtmlVisitorPrintStream does not keep buffer, " +
            "and neither controls the emitted chars. " +
            "HTML fragments have been already emitted on each element call.");
    }

    @Override
    public final HtmlVisitor clone(PrintStream out, boolean isIndented) {
        if(out == null)
            throw new IllegalArgumentException("Out PrintStream cannot be null!");
        return new HtmlVisitorPrintStream(out, isIndented);
    }
}

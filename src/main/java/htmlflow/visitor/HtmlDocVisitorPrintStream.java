package htmlflow.visitor;

import java.io.PrintStream;

public class HtmlDocVisitorPrintStream extends HtmlDocVisitor implements TagsToPrintStream {
    /**
     * The final PrintStream destination of the HTML content
     * produced by this visitor.
     */
    private final PrintStream out;

    public HtmlDocVisitorPrintStream(PrintStream out, boolean isIndented) {
        this(out, isIndented, 0);
    }

    public HtmlDocVisitorPrintStream(PrintStream out, boolean isIndented, int depth) {
        super(isIndented);
        this.out = out;
        this.depth = depth;
    }

    @Override
    public final String finish() {
        throw new UnsupportedOperationException("Do not call finish() on HtmlVisitorPrintStream because" +
            "HTML fragments have been already emitted on each element call.");
    }

    @Override
    public final void write(String text) {
        out.print(text);
    }
    @Override
    protected final void write(char c) {
        out.print(c);
    }

    @Override
    public final HtmlDocVisitor clone(PrintStream out, boolean isIndented) {
        if(out == null)
            throw new IllegalArgumentException("Out PrintStream cannot be null!");
        return new HtmlDocVisitorPrintStream(out, isIndented);
    }

    @Override
    public PrintStream out() {
        return out;
    }
}

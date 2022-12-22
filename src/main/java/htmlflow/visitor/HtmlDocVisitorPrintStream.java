package htmlflow.visitor;

import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;

public class HtmlDocVisitorPrintStream extends HtmlDocVisitor implements TagsToAppendable {
    /**
     * The final PrintStream destination of the HTML content
     * produced by this visitor.
     */
    private final Appendable out;

    public HtmlDocVisitorPrintStream(Appendable out, boolean isIndented) {
        this(out, isIndented, 0);
    }

    public HtmlDocVisitorPrintStream(Appendable out, boolean isIndented, int depth) {
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
        try {
            out.append(text);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    protected final void write(char c) {
        try {
            out.append(c);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    public final HtmlDocVisitor clone(Appendable out, boolean isIndented) {
        if(out == null)
            throw new IllegalArgumentException("Out Appendable cannot be null!");
        return new HtmlDocVisitorPrintStream(out, isIndented);
    }

    @Override
    public Appendable out() {
        return out;
    }
}

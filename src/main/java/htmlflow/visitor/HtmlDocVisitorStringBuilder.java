package htmlflow.visitor;


public class HtmlDocVisitorStringBuilder extends HtmlDocVisitor implements TagsToAppendable {
    /**
     * The main StringBuilder. Read by the finish() to return the
     * resulting string with the Html content.
     */
    protected final StringBuilder sb = new StringBuilder();

    public HtmlDocVisitorStringBuilder(boolean isIndented) {
        super(isIndented);
    }

    @Override
    public final String finish() {
        return sb.toString();
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
    public final HtmlDocVisitor clone(Appendable out, boolean isIndented) {
        if(out != null)
            throw new IllegalArgumentException("This HtmlVisitor emits to StringBuilder and does not support PrintStream!");
        return new HtmlDocVisitorStringBuilder(isIndented);
    }

    @Override
    public Appendable out() {
        return this.sb;
    }
}

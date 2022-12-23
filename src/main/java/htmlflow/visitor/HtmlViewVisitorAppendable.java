package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;

/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAppendable extends HtmlViewVisitorContinuations implements TagsToAppendable {

    private final Appendable out;

    public HtmlViewVisitorAppendable(Appendable out, boolean isIndented, HtmlContinuation first) {
        super(isIndented, first);
        this.out = out;
    }


    @Override
    protected String readAndReset() {
        if (out instanceof StringBuilder) {
            String data = out.toString();
            ((StringBuilder) out).setLength(0);
            return data;
        }

        return null;
    }

    @Override
    public void write(String text) {
        try {
            this.out.append(text);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    protected void write(char c) {
        try {
            this.out.append(c);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    public HtmlVisitor clone(boolean isIndented) {
        return new HtmlViewVisitorAppendable(this.out, isIndented, first);
    }

    @Override
    public Appendable out() {
        return this.out;
    }
}

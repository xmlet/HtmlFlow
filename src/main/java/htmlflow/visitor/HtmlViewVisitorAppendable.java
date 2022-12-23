package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;

/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAppendable extends HtmlViewVisitorContinuations implements TagsToAppendable {

    private Appendable out;

    public HtmlViewVisitorAppendable(boolean isIndented, HtmlContinuation first) {
        super(isIndented, first);
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
        return new HtmlViewVisitorAppendable(isIndented, first);
    }

    @Override
    public Appendable out() {
        return this.out;
    }

    @Override
    public void setAppendable(Appendable appendable) {
        this.out = appendable;
    }
}

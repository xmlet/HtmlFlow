package htmlflow.visitor;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync<T> extends HtmlViewVisitorContinuations<T> implements TagsToPrintStream {
    
    private final PrintStream out;
    private final HtmlContinuation<T> last;
    private final CompletableFuture<Void> cf;
    
    public HtmlViewVisitorAsync(PrintStream out, boolean isIndented, HtmlContinuation<T> first,
                                HtmlContinuation<T> last, CompletableFuture<Void> cf) {
        super(isIndented, first);
        this.out = out;
        this.last = last;
        this.cf = cf;
    }
    
    @Override
    protected String readAndReset() {
        return null;
    }
    
    @Override
    public void write(String text) {
        out.print(text);
    }
    
    @Override
    protected void write(char c) {
        out.print(c);
    }
    
    @Override
    public HtmlVisitor clone(PrintStream out, boolean isIndented) {
        return new HtmlViewVisitorAsync<>(out, isIndented, first, last, cf);
    }
    
    @Override
    public PrintStream out() {
        return this.out;
    }
    
    public CompletableFuture<Void> finishedAsync(T model) {
        this.first.execute(model);
        return cf;
    }
}

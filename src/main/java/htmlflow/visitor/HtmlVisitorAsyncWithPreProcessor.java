package htmlflow.visitor;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static htmlflow.visitor.PreprocessingVisitorAsync.HtmlContinuationSetter.setNext;

/**
 * @author Pedro Fialho
 **/
public class HtmlVisitorAsyncWithPreProcessor<T> extends HtmlViewVisitorContinuations<T> implements TagsToPrintStream {
    
    private final PrintStream out;
    private final HtmlContinuation<T> last;
    
    public HtmlVisitorAsyncWithPreProcessor(PrintStream out, boolean isIndented, HtmlContinuation<T> first,
                                            HtmlContinuation<T> last) {
        super(isIndented, first);
        this.out = out;
        this.last = last;
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
        return new HtmlVisitorAsyncWithPreProcessor<>(out, isIndented, first, last);
    }
    
    @Override
    public PrintStream out() {
        return this.out;
    }
    
    public CompletableFuture<Void> finishedAsync(T model) {
        final CompletableFuture<Void> cf = new CompletableFuture<>();
        setNext(last, new HtmlContinuation<T>(-1, isClosed, this, null) {
            @Override
            public void execute(T model) {
                this.emitHtml(model);
            }
    
            @Override
            protected void emitHtml(T model) {
                //TODO SEE IF IT'S STILL NEEDED
                //resetFirst((HtmlContinuation<?>) first);
                cf.complete(null);
            }
    
            @Override
            protected HtmlContinuation<T> copy(HtmlVisitor visitor) {
                return null;
            }
        });
        this.first.execute(model);
        return cf;
    }
}

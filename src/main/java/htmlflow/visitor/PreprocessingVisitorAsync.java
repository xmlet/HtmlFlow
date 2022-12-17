package htmlflow.visitor;

import htmlflow.HtmlView;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import static htmlflow.visitor.PreprocessingVisitorAsync.HtmlContinuationSetter.setNext;


/**
 * @author Pedro Fialho
 **/
public class PreprocessingVisitorAsync extends HtmlViewVisitor implements TagsToStringBuilder {
    
    private final StringBuilder sb = new StringBuilder();
    
    private int staticBlockIndex = 0;
    /**
     * The first node to be processed.
     */
    private HtmlContinuation first;
    /**
     * The last HtmlContinuation
     */
    private HtmlContinuation last;

    public PreprocessingVisitorAsync(boolean isIndented) {
        super(isIndented);
    }
    
    public HtmlContinuation getFirst() {
        return first;
    }

    @Override
    public String finish(Object model, HtmlView... partials) {
        this.finishAsync();
        return null;
    }
    
    public void finishAsync() {
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<Object> staticCont = new HtmlContinuationStatic<>(staticHtml.trim(), this, null);

        if (first == null) {
            last = first = staticCont; // assign both first and last
        } else {
            last = setNext(last, staticCont); // append new staticCont and return it to be the new last continuation.
        }
    }
    
    @Override
    public void write(String text) {
        sb.append(text);
    }
    
    @Override
    protected void write(char c) {
        sb.append(c);
    }
    
    @Override
    public HtmlVisitor clone(PrintStream out, boolean isIndented) {
        return null;
    }
    
    @Override
    public <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> consumer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <M, E extends Element> void visitAwait(E element, AwaitConsumer<E, M> asyncHtmlBlock) {
        /**
         * Creates an HtmlContinuation for the async block.
         */
    
        HtmlContinuationCloseAndIndent<M> closeAndIndent =
                new HtmlContinuationCloseAndIndent<>(this);
        
        HtmlContinuation<M> asyncCont = new HtmlContinuationAsync<>(depth, isClosed, element,
                asyncHtmlBlock, this, closeAndIndent);
    
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the asyncCont.
         */
        String staticHtml = sb.substring(staticBlockIndex);
        String staticHtmlTrimmed = staticHtml.trim();
        HtmlContinuationStatic<M> staticCont = new HtmlContinuationStatic<>(staticHtmlTrimmed, this, asyncCont);
        if(first == null) first = staticCont; // on first visit initializes the first pointer
        
        else {
            setNext(last, staticCont);       // else append the staticCont to existing chain
        }
        last = asyncCont.next;                   // advance last to point to the new asyncCont
        /**
         * We have to run asyncCont to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        newlineAndIndent();
        staticBlockIndex = sb.length(); // increment the staticBlockIndex to the end of internal string buffer.
    }
    
    @Override
    public StringBuilder sb() {
        return this.sb;
    }


    @SuppressWarnings({"squid:S3011", "squid:S112"})
    public static class HtmlContinuationSetter {
        private HtmlContinuationSetter() {
        }
        
        static final Field fieldNext;
        static {
            try {
                fieldNext = HtmlContinuation.class.getDeclaredField("next");
                fieldNext.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        
        public static <Z> HtmlContinuation<Z> setNext(HtmlContinuation<Z> cont, HtmlContinuation<Z> next) {
            try {
                fieldNext.set(cont, next);
                return next;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

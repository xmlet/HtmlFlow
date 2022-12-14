package htmlflow.visitor;

import htmlflow.HtmlView;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.OnCompletion;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;

import static htmlflow.visitor.PreprocessingVisitorAsync.HtmlContinuationSetter.setNext;


/**
 * @author Pedro Fialho
 **/
public class PreprocessingVisitorAsync<T> extends HtmlViewVisitor<T> implements TagsToStringBuilder {
    
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
    /**
     * Used create a mocked instance of the model to be passed to dynamic HTML blocks.
     */
    private final Class<?> modelClass;
    /**
     * Generic type arguments of the Model.
     */
    private final Type[] genericTypeArgs;
    
    public PreprocessingVisitorAsync(boolean isIndented, Class<?> modelClass, Type... genericTypeArgs) {
        super(isIndented);
        this.modelClass = modelClass;
        this.genericTypeArgs = genericTypeArgs;
    }
    
    public HtmlContinuation getFirst() {
        return first;
    }

    @Override
    public String finish(T model, HtmlView... partials) {
        this.finishAsync();
        return null;
    }
    
    public void finishAsync() {
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<T> staticCont = new HtmlContinuationStatic<>(staticHtml.trim(), this, null);

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
    public <E extends Element> void visitAwait(E element, BiConsumer<E, OnCompletion> asyncHtmlBlock) {
        /**
         * Creates an HtmlContinuation for the async block.
         */
    
        HtmlContinuationCloseAndIndent<T> closeAndIndent =
                new HtmlContinuationCloseAndIndent<>(this);
        
        HtmlContinuation<T> asyncCont = new HtmlContinuationAsync<>(depth, isClosed, element,
                asyncHtmlBlock, this, closeAndIndent);
    
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the asyncCont.
         */
        String staticHtml = sb.substring(staticBlockIndex);
        String staticHtmlTrimmed = staticHtml.trim();
        HtmlContinuation<T> staticCont = new HtmlContinuationStatic<>(staticHtmlTrimmed, this, asyncCont);
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

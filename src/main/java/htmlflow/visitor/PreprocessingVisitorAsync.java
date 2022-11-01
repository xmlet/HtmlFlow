package htmlflow.visitor;

import htmlflow.HtmlView;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.OnPublisherCompletion;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

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
    private HtmlContinuation<T> first;
    /**
     * The last HtmlContinuation
     */
    private HtmlContinuation<T> last;
    /**
     * Used create a mocked instance of the model to be passed to dynamic HTML blocks.
     */
    private final Class<?> modelClass;
    /**
     * Generic type arguments of the Model.
     */
    private final Type[] genericTypeArgs;
    
    public static final PodamFactory podamFactory;
    
    static {
        podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Publisher.class, new PublisherFactory(podamFactory));
    }
    
    public PreprocessingVisitorAsync(boolean isIndented, Class<?> modelClass, Type... genericTypeArgs) {
        super(isIndented);
        this.modelClass = modelClass;
        this.genericTypeArgs = genericTypeArgs;
    }
    
    public HtmlContinuation<T> getFirst() {
        return first;
    }
    
    
    public HtmlContinuation<T> getLast() {
        return last;
    }
    
    @Override
    public String finish(T model, HtmlView... partials) {
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<T> staticCont = new HtmlContinuationStatic<>(staticHtml, this, null);
        last = first == null
                ? first = staticCont         // assign both first and last
                : setNext(last, staticCont); // append new staticCont and return it to be the new last continuation.
        /**
         * We are just collecting static HTML blocks and the resulting HTML should be ignored.
         * Intentionally return null to force a NullPointerException if someone intend to use this result.
         */
        return null;
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
    public <E extends Element, U> OnPublisherCompletion visitAsync(E element, BiConsumer<E, Publisher<U>> asyncHtmlBlock,
                                                                   Publisher<U> source) {
        /**
         * Creates an HtmlContinuation for the async block.
         */
        
//        Runnable asyncAction = () -> asyncHtmlBlock.accept(element.get(), source);
    
        HtmlContinuation<T> asyncCont =
                new HtmlContinuationAsync<>(depth, isClosed, element, asyncHtmlBlock, this, source,null);
        
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the asyncCont.
         */
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<T> staticCont = new HtmlContinuationStatic<>(staticHtml, this, asyncCont);
        if(first == null) first = staticCont; // on first visit initializes the first pointer
        else setNext(last, staticCont);       // else append the staticCont to existing chain
        last = asyncCont;                   // advance last to point to the new asyncCont
        /**
         * We have to run dynamicContinuation to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        T model = (T) podamFactory.manufacturePojoWithFullData(modelClass, genericTypeArgs);
        staticBlockIndex = sb.length(); // increment the staticBlockIndex to the end of internal string buffer.
        return () -> asyncCont.execute((T) source);
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
        static final Field fieldFirst;
        static {
            try {
                fieldNext = HtmlContinuation.class.getDeclaredField("next");
                fieldFirst = PreprocessingVisitorAsync.class.getDeclaredField("first");
                fieldNext.setAccessible(true);
                fieldFirst.setAccessible(true);
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
    
        public static <Z> void resetFirst(HtmlContinuation<Z> first) {
            try {
                fieldFirst.set(first, null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

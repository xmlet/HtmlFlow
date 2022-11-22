package htmlflow.visitor;

import htmlflow.HtmlView;
import htmlflow.async.PublisherOnCompleteHandlerProxy;
import htmlflow.async.PublisherOnCompleteHandlerProxy.PublisherOnCompleteHandler;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static htmlflow.async.PublisherOnCompleteHandlerProxy.proxyPublisher;
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
    
    public static final PodamFactory podamFactory;
    
    static {
        podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Publisher.class, new PublisherFactory(podamFactory));
    }
    
    private final CompletableFuture<Void> cf = new CompletableFuture<>();
    
    public PreprocessingVisitorAsync(boolean isIndented, Class<?> modelClass, Type... genericTypeArgs) {
        super(isIndented);
        this.modelClass = modelClass;
        this.genericTypeArgs = genericTypeArgs;
    }
    
    public HtmlContinuation getFirst() {
        return first;
    }
    
    
    public HtmlContinuation getLast() {
        return last;
    }
    
    public CompletableFuture<Void> getCf() {
        return cf;
    }
    
    @Override
    public String finish(T model, HtmlView... partials) {
        this.finishAsync();
        return null;
    }
    
    public void finishAsync() {
        HtmlContinuation<T> cfContinuation  = new HtmlContinuation<T>(-1, isClosed, this, null) {
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
                return this;
            }
        };
        
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<T> staticCont = new HtmlContinuationStatic<>(staticHtml, this, cfContinuation);
        last = first == null
                ? first = staticCont         // assign both first and last
                : setNext(last, staticCont); // append new staticCont and return it to be the new last continuation.
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
    public <E extends Element, M, U> void visitAwait(E element, Class<U> clazz, BiConsumer<E, Publisher<U>> asyncHtmlBlock,
                                                  Function<M,Publisher<U>> modelToObs) {
        /**
         * Creates an HtmlContinuation for the async block.
         */
        
        T model = (T) podamFactory.manufacturePojoWithFullData(modelClass, genericTypeArgs);
        
        HtmlContinuation<T> asyncCont = new HtmlContinuationAsync<>(depth, isClosed, element,
                asyncHtmlBlock, this, (Function<T, Publisher<U>>) modelToObs,null);
    
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the asyncCont.
         */
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<T> staticCont = new HtmlContinuationStatic<>(staticHtml, this, asyncCont);
        if(first == null) first = staticCont; // on first visit initializes the first pointer
        
        else {
            setNext(last, staticCont);       // else append the staticCont to existing chain
        }
        last = asyncCont;                   // advance last to point to the new asyncCont
        /**
         * We have to run asyncCont to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        asyncCont.execute(model);
        
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
    }
}

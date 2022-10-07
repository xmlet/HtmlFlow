package htmlflow.visitor;

import htmlflow.async.nodes.AsyncNode;
import htmlflow.async.nodes.ContinuationNode;
import htmlflow.async.nodes.ThenNode;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.OnPublisherCompletion;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This is the implementation of the HtmlVisitor that can handle template with async models.
 */
public class HtmlVisitorAsync extends HtmlVisitor implements TagsToPrintStream {
    
    private static final String CANNOT_USE_STATIC_BLOCKS_CACHE_WITH_HTML_VISITOR_ASYNC = "Cannot use static blocks cache with HtmlVisitorAsync";
    private static final String CANNOT_CREATE_AN_HTML_VIEW_VISITOR_INSTANCE_FROM_HTML_VISITOR_ASYNC = "Cannot create an HtmlViewVisitor instance from " +
            "HtmlVisitorAsync";
    /**
     * The PrintStream destination of the HTML content produced by the visitor
     */
    private final PrintStream out;
    
    /**
     * The first node to be processed.
     */
    private ContinuationNode first = null;
    
    /**
     * The last ContinuationNode.
     */
    private ContinuationNode lastNode = null;
    
    
    public HtmlVisitorAsync(PrintStream out, boolean isIndented) {
        super(isIndented);
        this.out = out;
    }
    
    public HtmlVisitorAsync(PrintStream out, boolean isIndented, int depth) {
        this(out, isIndented);
        this.depth = depth;
    }
    
    @Override
    public HtmlViewVisitor newbie() {
        throw new UnsupportedOperationException(CANNOT_CREATE_AN_HTML_VIEW_VISITOR_INSTANCE_FROM_HTML_VISITOR_ASYNC);
    }
    
    @Override
    public boolean isWriting() {
        return true;
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
    protected int size() {
        throw new UnsupportedOperationException(CANNOT_USE_STATIC_BLOCKS_CACHE_WITH_HTML_VISITOR_ASYNC);
    }
    
    @Override
    public String finished() {
        throw new UnsupportedOperationException(CANNOT_USE_STATIC_BLOCKS_CACHE_WITH_HTML_VISITOR_ASYNC);
    }
    
    @Override
    public HtmlVisitor clone(PrintStream out, boolean isIndented) {
        return new HtmlVisitorAsync(out, isIndented);
    }
    
    @Override
    public PrintStream out() {
        return out;
    }

    /**
     * VisitAsync is responsible to handle the logic for when the user calls {@code async} for a certain Element.
     * <p/>
     * At the start we always wrap the call to the consumer, which is the logic for creating the Html tags from the Publisher type, inside a
     * runnable, which will start running once we know that we can start emitting the Html.
     * <p/>
     * This Runnable is then used to create a Node. A Node represents an action that was submitted by the user
     * The nodes are linked through the reference of a next node.
     * <p/>
     * Then we have two flows for processing async tasks:
     * <ul>
     * <li> When the async call is the first one. </li>
     * <li> When the async call is the N one. </li>
     * </ul>
     * <p/>
     * Case 1 is very straightforward, we just set the {@link #first} node to the newly created node and start the async action.
     * <p/>
     * For case 2, the first thing we do is to associate the new node (N node) to the N-1 async action, by using the method
     * {@link ContinuationNode#setNext(ContinuationNode)} to make the connection between both actions.
     * <p/>
     * In the end we return an implementation of {@link OnPublisherCompletion} which is going to be used as a Handler, which will allows us to be
     * notified when the {@code obs} will emit the {@code onComplete signal}.
     *
     * @param supplier A {@link Supplier} containing the current {@link Element} being used for the async task
     * @param consumer The async action that was submitted
     * @param source {@link Publisher} containing the reactive and async data which we are using to create the Html Element
     * @param <E> A generic type representing the current Html Element
     * @param <T> A generic type representing the object inside the Publisher
     * @return {@link OnPublisherCompletion} A Handler for when the {@code source} Publisher has terminated we get notified.
     *
     * @see Runnable
     * @see AsyncNode
     */
    @Override
    public <E extends Element, T> OnPublisherCompletion visitAsync(Supplier<E> supplier, BiConsumer<E, Publisher<T>> consumer, Publisher<T> source) {
        
        Runnable asyncAction = () -> consumer.accept(supplier.get(), source);
        
        final AsyncNode node = new AsyncNode(asyncAction);
        
        if (first == null) { lastNode = first = node; }

        lastNode = lastNode.setNext(node);

        return node::executeNextNode;
    }

    /**
     * VisitThen is responsible to handle the calls for the {@code .then()} in a certain Html Element.
     * <p/>
     * The call to {@code .then()} is always a sync one, which means we need to be very careful when to run it, as we need to make sure it only
     * happens after the {@code async()} ends.
     * <p/>
     * Since this is always called after a call to the {@code .async()} method, the first thing we need to do is to associate this action to the
     * async action. Just like with the {@link #visitAsync} method we link this node as a next action to the last one.
     * <p/>
     * Just like in the {@link #visitAsync} method we set up a listener for when the last submitted node has terminated we can run this sync action.
     * Unlike the {@linkplain #visitAsync} which we return a Handler that will be automatically called by us to inform us that the
     * {@link Publisher} has finished the execution, by taking advantage of the {@link Subscriber#onComplete()} signal, we need to set up manually
     * the continuation logic.
     * We do that by giving a {@link Runnable} to the last assigned node which will run this conitnuation logic for us.
     *
     * @param elem The resulting Html element from the an {@code .then()} call.
     * @param <E> The generic type identifying the next Html Element.
     */
    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        final ThenNode<E> thenNode = new ThenNode<>(elem);
        lastNode = lastNode.setNext(thenNode);
    }
    
    /**
     * It is linking a new CF to the last ContinuationNode.
     * Rendering twice this Visitor (i.e. call twice finishedAsync) will replace the former CF
     * with a new one, and the former will be claimed by GC.
     * @return A {@link CompletableFuture} which completes upon the last async action call the {@code onCompletion} listener.
     * @see ContinuationNode
     */
    public CompletableFuture<Void> finishedAsync() {
        final CompletableFuture<Void> cf = new CompletableFuture<>();
        this.getLastNode().setNext(new ContinuationNode() {
            @Override
            public void execute() {
                first = null;
                cf.complete(null);
            }
        });
        return cf;
    }
    
    
    public ContinuationNode getLastNode() {
        return lastNode;
    }
    
    public ContinuationNode getFirst() {
        return first;
    }
}

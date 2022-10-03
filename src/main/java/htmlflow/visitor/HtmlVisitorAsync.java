package htmlflow.visitor;

import htmlflow.async.nodes.AsyncNode;
import htmlflow.async.nodes.ContinuationNode;
import htmlflow.async.nodes.ThenNode;
import org.reactivestreams.Publisher;
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
    
    /**
     *
     * @return A {@link CompletableFuture} which completes upon the last async action achieves {@link AsyncNode#isDone()} state.
     * @see AsyncNode
     */
    public CompletableFuture<Void> finishedAsync() {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        this.getLastNode().setCfForCompletion(cf);
        return cf;
    }
    
    @Override
    public HtmlVisitor clone(PrintStream out, boolean isIndented) {
        return new HtmlVisitorAsync(out, isIndented);
    }
    
    public ContinuationNode getLastNode() {
        return lastNode;
    }
    
    /**
     * The current node represent the async action that is being processed at a certain point in time.
     */
    private ContinuationNode curr = null;
    
    /**
     * The last AsyncNode.
     */
    private ContinuationNode lastNode = null;
    
    public ContinuationNode getCurr() {
        return curr;
    }

    /**
     * VisitAsync is responsible to handle the logic for when the user calls {@code async} for a certain Element.
     * <p/>
     * At the start we always wrap the call to the consumer, which is the logic for creating the Html tags from the Publisher type, inside a
     * runnable, which will start running once we know that we can start emitting the Html.
     * <p/>
     * This Runnable is then used to create a Node. A Node represents an async action that was submitted by the user. The node always start at the
     * {@link AsyncNode#isWaiting()} state.
     * These nodes are always added to the LinkedList of nodes.
     * <p/>
     * Then we have two flows for processing async tasks:
     * <ul>
     * <li> When the async call is the first one. </li>
     * <li> When the async call is the N one. </li>
     * </ul>
     * <p/>
     * Case 1 is very straightforward, we just set the {@link #curr} node to the newly created node and start the async action by putting it in
     * {@link AsyncNode#isRunning()} state.
     * <p/>
     * For case 2, the first thing we do is to associate the new node (N node) to the N-1 async action.
     * <p/>
     * After that we perform a preemptive check in order to see if the N-1 task is already done.
     * If that's the case we can advance the curr node for the N node.
     * If that's not the case, we subscribe to the N-1 Publisher and once it emits the completed signal we can then advance the curr node to the N
     * task.
     *
     * @param supplier A {@link Supplier} containing the current {@link Element} being used for the async task
     * @param consumer The async action that was submitted
     * @param obs {@link Publisher} containing the reactive and async data which we are using to create the Html Element
     * @param <E> A generic type representing the current Html Element
     * @param <T> A generic type representing the object inside the Publisher
     *
     * @see Runnable
     * @see AsyncNode
     */
    @Override
    public <E extends Element, T> OnPublisherCompletion visitAsync(Supplier<E> supplier, BiConsumer<E, Publisher<T>> consumer, Publisher<T> obs) {
        
        Runnable asyncAction = () -> consumer.accept(supplier.get(), obs);
        
        final AsyncNode<T> node = new AsyncNode<>(asyncAction, obs);
        
        if (curr == null) {
            curr = node;
            node.execute();
        } else {
            final ContinuationNode last = lastNode;
            last.setNext(node);
            
            this.ifCurrDoneExecuteNext(node);
        }
    
        lastNode = node;
        return () -> {
            curr.setDone();
            final ContinuationNode next = curr.getNext();
            if (next != null) {
                curr = next;
                curr.execute();
            }
        };
    }
    
    /**
     * VisitThen is responsible to handle the calls for the {@code .then()} in a certain Html Element.
     * <p/>
     * The call to {@code .then()} is always a sync one, which means we need to be very careful when to run it, as we need to make sure it only
     * happens after the {@code async()} ends.
     * <p/>
     * Since this is always called after a call to the {@code .async()} method, the first thing we need to do is to associate this action to the
     * async action.
     * We do that by creating a {@link AsyncNode.ChildNode} and setting the {@link AsyncNode#childNode} reference for newly created one.
     * <p/>
     * So we create an association of parent -> child between an {@code .async()} call and a {@code .then()}.
     * After that we proceed to read the parent state, where,
     * <ul>
     *     <li>
     *         If the state is {@link AsyncNode#isDone()} we can call the {@link Supplier} which will trigger the execution of the
     *         {@link java.util.function.Function} inside the {@code .then()}
     *     </li>
     *     <li>
     *         If the state is {@link AsyncNode#isWaiting()} we don't do anything as the respective parent has not yet started emitting.
     *     </li>
     *     <li>
     *         If the state {@link AsyncNode#isRunning()} we subscribe to the {@link Publisher} of the parent and after it completes we set the
     *         parent state as done and proceed to call the {@link Supplier}
     *     </li>
     * </ul>
     *
     * @param elem The resulting Html element from the an {@code .then()} call.
     * @param <E> The generic type identifying the next Html Element.
     */
    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        final ThenNode<E> thenNode = new ThenNode<>(elem, () -> {
            final ContinuationNode next = curr.getNext();
            if (next != null) {
                next.execute();
                curr = next;
            }
        });
        
        final ContinuationNode last = this.lastNode;
        last.setNext(thenNode);
        
        this.lastNode = thenNode;
    
        ifCurrDoneExecuteNext(thenNode);
    }
    
    private void ifCurrDoneExecuteNext(ContinuationNode nextNode) {
        if (curr.isDone()) {
            curr = curr.getNext();
            nextNode.execute();
        }
    }
    
    @Override
    public PrintStream out() {
        return out;
    }
}

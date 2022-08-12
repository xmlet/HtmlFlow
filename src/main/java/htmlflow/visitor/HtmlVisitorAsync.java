package htmlflow.visitor;

import htmlflow.async.AsyncNode;
import htmlflow.async.subscribers.ObservableSubscriber;
import htmlflow.async.subscribers.PreviousAsyncObservableSubscriber;
import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Element;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Async version of an HtmlVisitorCache.
 * This visitor only handles async models.
 */
public class HtmlVisitorAsync extends HtmlViewVisitor {
    
    /**
     * The PrintStream destination of the HTML content produced by the visitor
     */
    private final PrintStream out;
    
    public HtmlVisitorAsync(PrintStream out) {
        this(out, true);
    }
    
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
        return new HtmlVisitorAsync(out, isIndented, depth);
    }
    
    @Override
    protected void beginTag(String elementName) {
        Tags.printOpenTag(out, elementName);
    }
    
    @Override
    protected void endTag(String elementName) {
        Tags.printCloseTag(out, elementName);
    }
    
    @Override
    protected void addAttribute(String attributeName, String attributeValue) {
        Tags.printAttribute(out, attributeName, attributeValue);
    }
    
    @Override
    protected void addComment(String comment) {
        Tags.printComment(out, comment);
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
    protected String substring(int staticBlockIndex) {
        
        return "";
    }
    
    @Override
    protected int size() {
        return 0;
    }
    
    @Override
    protected String readAndReset() {
        
        return null;
    }
    
    /**
     *
     * @return A {@link CompletableFuture} which completes upon the last async action achieves {@link AsyncNode#isDone()} state.
     * @see htmlflow.async.AsyncNode
     */
    public CompletableFuture<Void> finishedAsync() {
        CompletableFuture<Void> currCf = curr.cf;
        for(AsyncNode node = curr.next; node != null; node = node.next) {
            final AsyncNode myNode = node;
            currCf = currCf.thenCompose(prev -> myNode.cf);
        }
        return currCf;
    }
    
    @Override
    public HtmlViewVisitor clone(PrintStream out, boolean isIndented) {
        return new HtmlVisitorAsync(out, isIndented);
    }
    
    public AsyncNode getLastAsyncNode() {
        return lastAsyncNode.clone();
    }
    
    /**
     * The current node represent the async action that is being processed at a certain point in time.
     */
    private AsyncNode curr = null;
    
    /**
     * The last AsyncNode.
     */
    private AsyncNode lastAsyncNode = null;
    
    public AsyncNode getCurr() {
        return curr.clone();
    }
    
    /**
     * VisitAsync is responsible to handle the logic for when the user calls {@code async} for a certain Element.
     * <p/>
     * At the start we always wrap the call to the consumer, which is the logic for creating the Html tags from the Observable type, inside a
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
     * If that's not the case, we subscribe to the N-1 observable and once it emits the completed signal we can then advance the curr node to the N
     * task.
     *
     * @param supplier A {@link Supplier} containing the current {@link Element} being used for the async task
     * @param consumer The async action that was submitted
     * @param obs {@link Observable} containing the reactive and async data which we are using to create the Html Element
     * @param <E> A generic type representing the current Html Element
     * @param <T> A generic type representing the object inside the Observable
     *
     * @see Runnable
     * @see AsyncNode
     */
    @Override
    public <E extends Element, T> void visitAsync(Supplier<E> supplier, BiConsumer<E, Observable<T>> consumer, Observable<T> obs) {
        
        Runnable asyncAction = () -> consumer.accept(supplier.get(), obs);
        
        final AsyncNode<T> node = new AsyncNode<>(null, null, asyncAction, obs);
        
        if (curr == null) {
            curr = node;
            setCurrStateAsRunning();
        } else {
            final AsyncNode last = lastAsyncNode;
            last.next = node;
            if (last.isDone()) {
                advanceToNextAsyncAction();
            } else {
                last.observable.subscribe(new PreviousAsyncObservableSubscriber<>(this::advanceToNextAsyncAction));
            }
        }
        lastAsyncNode = node;
    }
    
    private void advanceToNextAsyncAction() {
        curr = curr.next;
        setCurrStateAsRunning();
    }
    
    /**
     * Puts the current node into RUNNING mode, if this node already contains a child node we trigger the action that was set when the child node
     * was created.
     */
    private void setCurrStateAsRunning() {
        curr.setRunning();
        curr.asyncAction.run();
        if (curr.childNode != null) {
            curr.childNode.onAsyncAction.trigger(curr);
        }
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
     *         If the state {@link AsyncNode#isRunning()} we subscribe to the {@link Observable} of the parent and after it completes we set the
     *         parent state as done and proceed to call the {@link Supplier}
     *     </li>
     * </ul>
     *
     * @param elem The resulting Html element from the an {@code .then()} call.
     * @param <E> The generic type identifying the next Html Element.
     * @see htmlflow.async.AsyncNode.OnAsyncAction
     */
    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        
        AsyncNode last = lastAsyncNode;
    
        if (last.childNode == null) {
            last.childNode = new AsyncNode.ChildNode<>(elem, curr -> readParentState(curr, last, elem));
        }
        
        this.readParentState(last, last, elem);
    }
    
    private <E extends Element> void readParentState(AsyncNode curr, AsyncNode last, Supplier<E> elem) {
        if(curr.isDone()) {
            elem.get();
        }
        else if(curr.isRunning()) {
            last.observable.subscribe(new ObservableSubscriber<>(this::setCurrStateAsDone, elem, last));
        }
        else if(curr.isWaiting()) {
            // no action needed
        }
        else
            throw new IndexOutOfBoundsException();
    }
    
    /**
     * Executes the call to the Supplier and sets the parent state as {@link AsyncNode#isDone()}.
     *
     * @param elem The Supplier containing the execution of the {@code .then()}
     * @param parentNode The parent node
     * @param <E> Generic type representing the next type of Html Element
     */
    private <E extends Element> void setCurrStateAsDone(Supplier<E> elem, AsyncNode parentNode) {
        elem.get();
        parentNode.setDone();
    }
}

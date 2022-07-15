package htmlflow;

import htmlflow.async.AsyncNode;
import htmlflow.async.subscribers.ObservableSubscriber;
import htmlflow.async.subscribers.PreviousAsyncObservableSubscriber;
import htmlflow.util.ObservablePrintStream;
import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Element;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static htmlflow.async.AsyncNode.State.DONE;
import static htmlflow.async.AsyncNode.State.RUNNING;

public class HtmlVisitorAsync extends HtmlVisitorCache {
    
    private final PrintStream out;
    
    private ObservablePrintStream current;
    
    public HtmlVisitorAsync(PrintStream out, boolean isDynamic) {
        this(out, isDynamic, true);
    }
    
    public HtmlVisitorAsync(PrintStream out, boolean isDynamic, boolean isIndented) {
        super(isDynamic, isIndented);
        this.out = out;
        this.current = new ObservablePrintStream(out);
    }
    
    public HtmlVisitorAsync(PrintStream out, boolean isDynamic, boolean isIndented, int depth) {
        this(out, isDynamic, isIndented);
        this.depth = depth;
    }
    
    
    @Override
    protected HtmlVisitorCache newbie() {
        return new HtmlVisitorAsync(out, isDynamic, isIndented, depth);
    }
    
    @Override
    protected void beginTag(String elementName) {
        Tags.printOpenTag(current, elementName);
    }
    
    @Override
    protected void endTag(String elementName) {
        Tags.printCloseTag(current, elementName);
    }
    
    @Override
    protected void addAttribute(String attributeName, String attributeValue) {
        Tags.printAttribute(current, attributeName, attributeValue);
    }
    
    @Override
    protected void addComment(String comment) {
        Tags.printComment(current, comment);
    }
    
    @Override
    protected void write(String text) {
        current.print(text);
    }
    
    @Override
    protected void write(char c) {
        current.print(c);
    }
    
    @Override
    protected String substring(int staticBlockIndex) {
        return this.current.subString(staticBlockIndex);
    }
    
    @Override
    protected int size() {
        return this.current.length();
    }
    
    @Override
    protected String readAndReset() {
        this.current = new ObservablePrintStream(out);
        
        return null;
    }
    
    @Override
    protected HtmlVisitorCache clone(boolean isIndented) {
        return new HtmlVisitorAsync(out, isDynamic, isIndented);
    }
    
    public Observable<String> getHtmlEmitter() {
        return this.current.getHtmlEmitter();
    }
    
    private final LinkedList<AsyncNode> actions = new LinkedList<>();
    private AsyncNode curr = null;
    
    public LinkedList<AsyncNode> getActions() {
        return actions;
    }
    
    protected AsyncNode getCurr() {
        return curr.clone();
    }
    
    @Override
    public <E extends Element, T> void visitAsync(Supplier<E> supplier, BiConsumer<E, Observable<T>> consumer, Observable<T> obs) {
        
        Runnable asyncAction = () -> consumer.accept(supplier.get(), obs);
        
        final AsyncNode<T> node = new AsyncNode<>(null, null, asyncAction, obs);
        
        if (curr == null) {
            curr = node;
            setCurrStateAsRunning();
        } else {
            final AsyncNode last = actions.getLast();
            last.next = node;
            if (last.isDone()) {
                advanceToNextAsyncAction();
            } else {
                last.observable.subscribe(new PreviousAsyncObservableSubscriber<>(this::advanceToNextAsyncAction));
            }
        }
        actions.addLast(node);
    }
    
    private void advanceToNextAsyncAction() {
        while (!curr.isDone());
        curr = curr.next;
        setCurrStateAsRunning();
    }
    
    private void setCurrStateAsRunning() {
        curr.state = RUNNING;
        curr.asyncAction.run();
        if (curr.childNode != null) {
            curr.childNode.onAsyncAction.trigger(curr.state);
        }
    }
    
    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        
        AsyncNode last = actions.getLast();
        final AsyncNode.ChildNode<E> childNode = new AsyncNode.ChildNode<>(elem, state -> readParentState(state, last, elem));
        
        if (last.childNode == null) {
            last.childNode = childNode;
        }
        
        if (actions.size() == 1) {
            last.observable.subscribe(new ObservableSubscriber<>(this::setCurrStateAsDone, elem, last));
        } else {
            readParentState(last.state, last, elem);
        }
    }
    
    private <E extends Element> void readParentState(AsyncNode.State state, AsyncNode last, Supplier<E> elem) {
        switch (state){
            case DONE:
                break;
            case WAITING:
                this.setCurrStateAsRunning();
                break;
            case RUNNING:
                last.observable.subscribe(new ObservableSubscriber<>(this::setCurrStateAsDone, elem, last));
                break;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
    
    private <E extends Element> void setCurrStateAsDone(Supplier<E> elem, AsyncNode node) {
        // so if we have .async(x, (thead,x) -> {...}, x -> x.__())
        // calling .get() on the supplier we will trigger the .then() function to be applied to x -> x.__()
        // we can only call the .get() when the observable is done
        elem.get();
        node.state = DONE;
    }
}

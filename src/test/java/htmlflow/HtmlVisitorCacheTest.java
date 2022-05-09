package htmlflow;

import htmlflow.HtmlVisitorStringBuilder;
import htmlflow.async.AsyncNode;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.internal.operators.observable.ObservableDelay;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HtmlVisitorCacheTest {
    
    @Nested
    @DisplayName("When user uses async methods")
    class VisitAsyncMethodsTest {
    
        private Table<Body<Html<Element>>> baseElem;
    
        private HtmlVisitorStringBuilder visitor;
    
        @BeforeEach
        void init() {
            visitor = new HtmlVisitorStringBuilder(false);
            baseElem = new Html<>(visitor)
                    .body()
                    .table();
        }
        
        @Test
        void given_supplier_action_and_observable_when_first_call_to_visitAsync_then_add_node_and_set_running() {
    
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            Consumer<Table<Body<Html<Element>>>> action = (table) -> table.thead().text("text");
    
            final Observable<String> observable = Observable.fromArray("1", "2", "3");
    
            visitor.visitAsync(elem, action, observable);
    
            final LinkedList<AsyncNode> actions = visitor.getActions();
            
            assertEquals(1, actions.size());
            assertTrue(visitor.getCurr().isRunning());
        }
    
        @Test
        void given_supplier_action_and_observable_when_n_call_to_visitAsync_then_add_node_and_wait() {
            
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
            
            Consumer<Table<Body<Html<Element>>>> action = (table) -> table.thead().text("text");
    
            final Observable<String> observable = Observable.create(emitter -> {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
        
                emitter.onComplete();
            });
            
            final Observable<String> delayer =
                    new ObservableDelay<>(observable, 2, TimeUnit.SECONDS, Schedulers.io(), false);
    
    
            // first call to trigger the logic
            visitor.visitAsync(elem, action, delayer);
            
            visitor.visitAsync(elem, action, observable);
        
            final LinkedList<AsyncNode> actions = visitor.getActions();
        
            assertEquals(2, actions.size());
            assertTrue(visitor.getCurr().isRunning());
            assertTrue(actions.getLast().isWaiting());
            assertNull(actions.getLast().next);
            
        }
    
        @Test
        void given_supplier_action_and_observable_when_previous_is_finished_runs_next_async() {
    
            AtomicBoolean isSubscribed = new AtomicBoolean(false);
            
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
            
            Consumer<Table<Body<Html<Element>>>> action = (table) -> table.thead().text("text");
        
            final Observable<String> observable = Observable.<String>create(emitter -> {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
            
                emitter.onComplete();
            }).doOnSubscribe(__ -> isSubscribed.set(true));
        
        
            // first call to trigger the logic
            visitor.visitAsync(elem, action, observable);
            
            //creates new element
    
            final Observable<String> newObservable = Observable.create(emitter -> {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
        
                emitter.onComplete();
            });
            
            final Div<Body<Html<Element>>> newElem = baseElem.thead().__()
                    .__().div().text("new field");
    
            Consumer<Div<Body<Html<Element>>>> divAction = (div) -> div.text("new action");
    
            String nextAsyncActionExpected = "\n<html>\n" +
                    "\t<body>\n" +
                    "\t\t<table>\n" +
                    "\t\t\t<thead>\n" +
                    "\t\t\t\ttext\n" +
                    "\t\t\t\t<thead>\n" +
                    "\t\t\t\t</thead>\n" +
                    "\t\t\t</table>\n" +
                    "\t\t\t<div>\n" +
                    "\t\t\t\tnew field\n" +
                    "\t\t\t\tnew action";
            
            visitor.visitAsync(() -> newElem, divAction, newObservable);
            //forces termination of previous
            observable.blockingSubscribe();
            
            final LinkedList<AsyncNode> actions = visitor.getActions();
        
            assertEquals(2, actions.size());
            assertTrue(actions.getLast().isRunning());
            assertTrue(isSubscribed.get());
    
            final String result = visitor.readAndReset();
            assertEquals(nextAsyncActionExpected, result);
        }
        
        @Test
        void given_new_elem_and_first_then_call_when_call_visitThen_set_child_node_and_subscribe() {
            AtomicBoolean isSubscribed = new AtomicBoolean(false);
    
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            Consumer<Table<Body<Html<Element>>>> action = (table) -> table.thead().text("text");
    
            final Observable<String> observable = Observable.<String>create(emitter -> {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
        
                emitter.onComplete();
            }).doOnSubscribe(__ -> isSubscribed.set(true));
    
    
            // first call to trigger the logic
            visitor.visitAsync(elem, action, observable);
    
            visitor.visitThen(() -> baseElem.__().div());
    
            final AsyncNode last = visitor.getActions().getLast();
    
            assertTrue(isSubscribed.get());
            assertNotNull(last.childNode);
        }
    
        @Test
        void given_new_elem_and_second_then_call_when_call_visitThen_set_child_node_and_read_state_when_ready() {
    
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
        
            Consumer<Table<Body<Html<Element>>>> action = (table) -> table.thead().text("text");
            Consumer<Div<Body<Html<Element>>>> secondAction = (div) -> div.text("text");
        
            final Observable<String> observable = Observable.create(emitter -> {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
            
                emitter.onComplete();
            });
    
            // we delay to make sure the code has time to execute the logic were the first action is not ready
            final Observable<String> delayer =
                    new ObservableDelay<>(observable, 1, TimeUnit.SECONDS, Schedulers.io(), false);
    
            final Observable<String> secondObservable = Observable.create(emitter -> {
                emitter.onNext("4");
                emitter.onNext("5");
                emitter.onNext("6");
        
                emitter.onComplete();
            });
    
            final ObservableDelay<String> secondDelayer =
                    new ObservableDelay<>(secondObservable, 1, TimeUnit.SECONDS, Schedulers.io(), false);
    
    
            // first call to trigger the logic
            visitor.visitAsync(elem, action, delayer);
    
            final Div<Body<Html<Element>>> div = baseElem.__().div();
            visitor.visitThen(() -> div);
            
            //real logic
            visitor.visitAsync(() -> div, secondAction, secondDelayer);
    
            visitor.visitThen(() -> div.text("text").__());
            
            String expectedFinalResult = "\n" +
                    "<html>\n" +
                    "\t<body>\n" +
                    "\t\t<table>\n" +
                    "\t\t\t<thead>\n" +
                    "\t\t\t\ttext\n" +
                    "\t\t\t</table>\n" +
                    "\t\t\t<div>\n" +
                    "\t\t\t\ttext\n" +
                    "\t\t\t\ttext\n" +
                    "\t\t\t</div>";
        
            //force to wait for the delay
            delayer.blockingSubscribe();
            
            final AsyncNode last = visitor.getActions().getLast();
        
            assertNotNull(last.childNode);
            assertTrue(last.isRunning());
            
            secondDelayer.blockingSubscribe();
            assertEquals(expectedFinalResult, visitor.readAndReset());
        }
    }
}

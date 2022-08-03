package htmlflow.test;

import htmlflow.HtmlVisitorAsync;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class HtmlVisitorAsyncTest {
    
    @Nested
    @DisplayName("When user uses async methods")
    class VisitAsyncMethodsTest {
    
        private Table<Body<Html<Element>>> baseElem;
    
        @Mock
        private PrintStream out;
        
        private HtmlVisitorAsync visitor;

        @BeforeEach
        void init() {
            visitor = new HtmlVisitorAsync(out,false);
            doNothing().when(out).print(anyString());
            doNothing().when(out).print(anyChar());
            baseElem = new Html<>(visitor)
                    .body()
                    .table();
        }
        
        @Test
        void given_supplier_action_and_observable_when_first_call_to_visitAsync_then_add_node_and_set_running() {
    
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            BiConsumer<Table<Body<Html<Element>>>, Observable<String>> action = (table, obs) -> table.thead().text("text");
    
            final Observable<String> observable = Observable.fromArray("1", "2", "3");
    
            visitor.visitAsync(elem, action, observable);
    
            assertNull(visitor.getCurr().next);
            assertTrue(visitor.getCurr().isRunning());
        }
    
        @Test
        void given_supplier_action_and_observable_when_n_call_to_visitAsync_then_add_node_and_wait() {
            
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            BiConsumer<Table<Body<Html<Element>>>, Observable<String>> action = (table, obs) -> table.thead().text("text");
    
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
        
            assertNull(visitor.getCurr().next.next); // size == 2
            assertTrue(visitor.getCurr().isRunning());
            assertTrue(visitor.getLastAsyncNode().isWaiting());
            assertNull(visitor.getLastAsyncNode().next);
            
        }
    
        @Test
        void given_supplier_action_and_observable_when_previous_is_finished_runs_next_async() {
    
            AtomicBoolean isSubscribed = new AtomicBoolean(false);
            
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            BiConsumer<Table<Body<Html<Element>>>, Observable<String>> action = (table, obs) -> table.thead().text("text");
        
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
    
            BiConsumer<Div<Body<Html<Element>>>, Observable<String>> divAction = (div, obs) -> div.text("new action");
            
            visitor.visitAsync(() -> newElem, divAction, newObservable);
            //forces termination of previous
            observable.blockingSubscribe();
            
            assertNull(visitor.getCurr().next); // Current == Last ?
            assertTrue(visitor.getLastAsyncNode().isRunning());
            assertTrue(isSubscribed.get());
        }
        
        @Test
        void given_new_elem_and_first_then_call_when_call_visitThen_set_child_node_and_subscribe() {
            AtomicBoolean isSubscribed = new AtomicBoolean(false);
    
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            BiConsumer<Table<Body<Html<Element>>>, Observable<String>> action = (table, obs) -> table.thead().text("text");
    
            final Observable<String> observable = Observable.<String>create(emitter -> {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
        
                emitter.onComplete();
            }).doOnSubscribe(__ -> isSubscribed.set(true));
    
    
            // first call to trigger the logic
            visitor.visitAsync(elem, action, observable);
    
            visitor.visitThen(() -> baseElem.__().div());
    
            final AsyncNode last = visitor.getLastAsyncNode();
    
            assertTrue(isSubscribed.get());
            assertNotNull(last.childNode);
        }
    
        @Test
        void given_new_elem_and_second_then_call_when_call_visitThen_set_child_node_and_read_state_when_ready() {
    
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
    
            BiConsumer<Table<Body<Html<Element>>>, Observable<String>> action = (table, obs) -> table.thead().text("text");
            BiConsumer<Div<Body<Html<Element>>>, Observable<String>> secondAction = (div, obs) -> div.text("text");
        
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
        
            //force to wait for the delay
            delayer.blockingSubscribe();
            
            final AsyncNode last = visitor.getLastAsyncNode();
        
            assertNotNull(last.childNode);
            assertTrue(last.isRunning());
            
            secondDelayer.blockingSubscribe();
        }
    }
}

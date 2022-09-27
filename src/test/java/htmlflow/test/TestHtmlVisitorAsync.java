package htmlflow.test;

import htmlflow.async.AsyncNode;
import htmlflow.visitor.HtmlVisitorAsync;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;
import org.xmlet.htmlapifaster.async.OnPublisherCompletion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.io.PrintStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class TestHtmlVisitorAsync {
    
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
            
            BiConsumer<Table<Body<Html<Element>>>, Publisher<String>> action = (table, obs) -> table.thead().text("text");
            
            final Flux<String> observable = Flux.fromStream(of("1", "2", "3"));
            
            visitor.visitAsync(elem, action, observable);
            observable.subscribe();
            
            assertNull(visitor.getCurr().next);
            assertTrue(visitor.getCurr().isRunning());
        }
        
        @Test
        void given_supplier_action_and_observable_when_n_call_to_visitAsync_then_add_node_and_wait() {
            
            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;
            
            BiConsumer<Table<Body<Html<Element>>>, Publisher<String>> action = (table, obs) -> table.thead().text("text");

            Sinks.Many<String> sink = Sinks.many().replay().all();
            sink.emitNext("1", FAIL_FAST);
            sink.emitNext("2", FAIL_FAST);
            sink.emitNext("3", FAIL_FAST);
            sink.emitComplete(FAIL_FAST);

            final Publisher<String> delayer = sink
                .asFlux()
                .delayElements(Duration.of(2, ChronoUnit.SECONDS), Schedulers.boundedElastic());

            
            // first call to trigger the logic
            visitor.visitAsync(elem, action, delayer);
            
            visitor.visitAsync(elem, action, sink.asFlux());
            
            assertNull(visitor.getCurr().next.next); // size == 2
            assertTrue(visitor.getCurr().isRunning());
            assertTrue(visitor.getLastAsyncNode().isWaiting());
            assertNull(visitor.getLastAsyncNode().next);
            
        }

        @Test
        void given_supplier_action_and_observable_when_previous_is_finished_runs_next_async() {

            AtomicBoolean isSubscribed = new AtomicBoolean(false);

            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;

            BiConsumer<Table<Body<Html<Element>>>, Publisher<String>> action = (table, obs) -> table.thead().text("text");

            Sinks.Many<String> sink = Sinks.many().replay().all();
            sink.emitNext("1", FAIL_FAST);
            sink.emitNext("2", FAIL_FAST);
            sink.emitNext("3", FAIL_FAST);
            sink.emitComplete(FAIL_FAST);

            Flux<String> observable = sink.asFlux().doOnSubscribe(__ -> isSubscribed.set(true));

            // first call to trigger the logic
            final OnPublisherCompletion publisherCompletion = visitor.visitAsync(elem, action, observable);
            observable.subscribe();

            //creates new element
            Sinks.Many<String> newSink = Sinks.many().replay().all();
            newSink.emitNext("1", FAIL_FAST);
            newSink.emitNext("2", FAIL_FAST);
            newSink.emitNext("3", FAIL_FAST);
            newSink.emitComplete(FAIL_FAST);

            final Div<Body<Html<Element>>> newElem = baseElem.thead().__()
                    .__().div().text("new field");

            BiConsumer<Div<Body<Html<Element>>>, Publisher<String>> divAction = (div, obs) -> div.text("new action");

            visitor.visitAsync(() -> newElem, divAction, newSink.asFlux());
            
            //forces termination of previous
            observable.blockLast();
            publisherCompletion.onComplete();
    
            assertNull(visitor.getCurr().next);
            assertTrue(visitor.getLastAsyncNode().isRunning());
            assertTrue(isSubscribed.get());
        }

        @Test
        void given_new_elem_and_first_then_call_when_call_visitThen_set_child_node_and_subscribe() {
            AtomicBoolean isSubscribed = new AtomicBoolean(false);

            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;

            BiConsumer<Table<Body<Html<Element>>>, Publisher<String>> action = (table, obs) -> table.thead().text("text");

            Sinks.Many<String> sink = Sinks.many().replay().all();
            sink.emitNext("1", FAIL_FAST);
            sink.emitNext("2", FAIL_FAST);
            sink.emitNext("3", FAIL_FAST);
            sink.emitComplete(FAIL_FAST);

            Flux<String> flux = sink.asFlux().doOnSubscribe(__ -> isSubscribed.set(true));



            // first call to trigger the logic
            visitor.visitAsync(elem, action, flux);
            flux.subscribe();

            visitor.visitThen(() -> baseElem.__().div());

            final AsyncNode last = visitor.getLastAsyncNode();

            assertTrue(isSubscribed.get());
            assertNotNull(last.childNode);
        }

        @Test
        void given_new_elem_and_second_then_call_when_call_visitThen_set_child_node_and_read_state_when_ready() {

            Supplier<Table<Body<Html<Element>>>> elem = () -> baseElem;

            BiConsumer<Table<Body<Html<Element>>>, Publisher<String>> action = (table, obs) -> table.thead().text("text");
            BiConsumer<Div<Body<Html<Element>>>, Publisher<String>> secondAction = (div, obs) -> div.text("text");

            final Sinks.Many<String> sink = Sinks.many().replay().all();
            sink.emitNext("1", FAIL_FAST);
            sink.emitNext("2", FAIL_FAST);
            sink.emitNext("3", FAIL_FAST);
            sink.emitComplete(FAIL_FAST);

            // we delay to make sure the code has time to execute the logic were the first action is not ready
            final Flux<String> delayer = sink
                .asFlux()
                .delayElements(Duration.of(1, ChronoUnit.SECONDS), Schedulers.boundedElastic());

            final Sinks.Many<String> secondSink = Sinks.many().replay().all();
            secondSink.emitNext("4", FAIL_FAST);
            secondSink.emitNext("5", FAIL_FAST);
            secondSink.emitNext("6", FAIL_FAST);
            secondSink.emitComplete(FAIL_FAST);


            final Flux<String> secondDelayer = secondSink
                .asFlux()
                .delayElements(Duration.of(1, ChronoUnit.SECONDS), Schedulers.boundedElastic());


            // first call to trigger the logic
            final OnPublisherCompletion publisherCompletion = visitor.visitAsync(elem, action, delayer);
            delayer.subscribe();
    
            final Div<Body<Html<Element>>> div = baseElem.__().div();
            visitor.visitThen(() -> div);

            //real logic
            visitor.visitAsync(() -> div, secondAction, secondDelayer);

            visitor.visitThen(() -> div.text("text").__());

            //force to wait for the delay
            delayer.blockLast();
            publisherCompletion.onComplete();

            final AsyncNode last = visitor.getLastAsyncNode();

            assertNotNull(last.childNode);
            assertTrue(last.isRunning());

            secondDelayer.blockLast();
        }
    }
}

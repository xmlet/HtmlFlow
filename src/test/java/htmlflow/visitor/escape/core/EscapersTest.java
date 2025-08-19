package htmlflow.visitor.escape.core;

import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EscapersTest {
    @Test
    void when_calling_the_addScapeMethod_with_null_replacement_then_throw_exception() {
        assertThrows(NullPointerException.class, () -> Escapers.builder().addScape('a', null));
    }

   @Test
   void addScape_should_return_the_same_builder_instance() {
       Escapers.Builder builder = Escapers.builder();
       Escapers.Builder returnedBuilder = builder.addScape('a', "replacement");
       Assertions.assertEquals(builder, returnedBuilder);
   }
}
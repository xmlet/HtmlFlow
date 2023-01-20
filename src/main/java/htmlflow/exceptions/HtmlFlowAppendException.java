package htmlflow.exceptions;

import java.util.function.UnaryOperator;

/**
 * @author Pedro Fialho
 *
 * Class to be used where is an error while writing to the output
 **/
public class HtmlFlowAppendException extends RuntimeException {

    private static final UnaryOperator<String> EXCEPTION_MESSAGE = msg -> "There has been an exception in writing the Html" +
            ", underlying exception is "+msg;

    public HtmlFlowAppendException(Throwable th) {
        super(EXCEPTION_MESSAGE.apply(th.getMessage()), th);
    }

    public HtmlFlowAppendException(String message) {
        super(EXCEPTION_MESSAGE.apply(message));
    }

}

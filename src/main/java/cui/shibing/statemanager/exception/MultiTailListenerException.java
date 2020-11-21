package cui.shibing.statemanager.exception;

public class MultiTailListenerException extends Exception {

    public MultiTailListenerException() {

    }

    public MultiTailListenerException(String message) {
        super(message);
    }

    public MultiTailListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiTailListenerException(Throwable cause) {
        super(cause);
    }

    public MultiTailListenerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

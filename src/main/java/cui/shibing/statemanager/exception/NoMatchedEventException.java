package cui.shibing.statemanager.exception;

/**
 * 当前状态无法响应给定的事件时抛出
 */
public class NoMatchedEventException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoMatchedEventException() {
    }

    public NoMatchedEventException(String message) {
        super(message);
    }

    public NoMatchedEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchedEventException(Throwable cause) {
        super(cause);
    }

    public NoMatchedEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

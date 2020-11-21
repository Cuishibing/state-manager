package cui.shibing.statemanager.exception;

/**
 * 状态转换异常
 */
public class StateTransitionException extends Exception {

    private static final long serialVersionUID = 1L;

    public StateTransitionException() {
    }

    public StateTransitionException(String message) {
        super(message);
    }

    public StateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateTransitionException(Throwable cause) {
        super(cause);
    }

    public StateTransitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

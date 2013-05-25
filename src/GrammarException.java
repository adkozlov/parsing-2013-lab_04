public class GrammarException extends Throwable {

    public GrammarException() {
    }

    public GrammarException(Throwable cause) {
        super(cause);
    }

    public GrammarException(String message) {
        super(message);
    }

    public GrammarException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrammarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

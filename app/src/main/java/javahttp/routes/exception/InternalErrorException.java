package javahttp.routes.exception;

public class InternalErrorException extends Exception {
    public InternalErrorException() {
        super("Internal Error");
    }

    public InternalErrorException(String message) {
        super(message);
    }
}

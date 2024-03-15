package javahttp.routes.exception;

public class InvalidRequestException extends Exception {
    public InvalidRequestException() {
        super("Invalid Request");
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}

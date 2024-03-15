package javahttp.routes.exception;

public class UserExistsException extends Exception {
    public UserExistsException() {
        super("User Already Exists");
    }

    public UserExistsException(String message) {
        super(message);
    }
}

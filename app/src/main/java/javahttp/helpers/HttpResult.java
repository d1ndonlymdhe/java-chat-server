package javahttp.helpers;

public class HttpResult<T> {
    boolean success;
    T result;

    HttpResult(boolean success, T result) {
        this.success = success;
        this.result = result;
    }
}

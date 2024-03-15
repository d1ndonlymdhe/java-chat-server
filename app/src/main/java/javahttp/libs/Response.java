package javahttp.libs;

public class Response<T> {
    Boolean success;
    T data;

    public Response(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}

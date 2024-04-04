package org.mdhe.chatserver.utilsx;

import java.io.Serializable;

public class Response<T> implements Serializable {
    public Boolean success;
    public T data;

    public Response(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}

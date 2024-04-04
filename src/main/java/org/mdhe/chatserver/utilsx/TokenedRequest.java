package org.mdhe.chatserver.utilsx;

public class TokenedRequest<T> {
    public String token;
    public T data;

    public TokenedRequest(String token, T data) {
        this.token = token;
        this.data = data;
    }
}

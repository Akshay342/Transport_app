package com.fujitsu.transport.transportapp.base;

public class MutableResponse <T> {
    private T body;
    private String error;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

package com.example.database_system.pojo.response;

import org.springframework.http.HttpStatus;

public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResponseMessage(Integer code,String message,T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseMessage(Integer code,String message,T data,String token) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.token = token;
    }


    public static <T> ResponseMessage<T> loginSuccess(T data, String message, String token) {
        return new ResponseMessage<T>(HttpStatus.OK.value(), message, data, token);
    }

    public static <T> ResponseMessage<T> error(T data, String message, Integer code) {
        return new ResponseMessage<>(code, message, data);
    }

    //接口请求成功
    public static <T> ResponseMessage<T> success(T data, String message) {
        return new ResponseMessage<T>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseMessage<T> fail(T data, String message) {
        return new ResponseMessage<T>(HttpStatus.EXPECTATION_FAILED.value(), message, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
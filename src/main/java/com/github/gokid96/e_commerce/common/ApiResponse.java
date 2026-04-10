package com.github.gokid96.e_commerce.common;

import lombok.Getter;

@Getter
public class ApiResponse <T> {
    private int code;
    private String message;
    private T data;

    private ApiResponse(int code,String message,T data) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "OK", data);
    }
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(200, "OK", null);
    }
    public static <T> ApiResponse<T> fail(int code,String message) {
        return new ApiResponse<>(code,message,null);
    }
}

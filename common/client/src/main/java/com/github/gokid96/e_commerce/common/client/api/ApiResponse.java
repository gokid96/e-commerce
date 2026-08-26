package com.github.gokid96.e_commerce.common.client.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
}

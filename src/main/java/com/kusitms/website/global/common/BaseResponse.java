package com.kusitms.website.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.kusitms.website.global.common.BaseExceptionStatus.SUCCESS;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "data"})
public class BaseResponse<T> {
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private T data;

    public BaseResponse(T data) {
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.data = data;
    }

    public BaseResponse() {
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
    }

    public static BaseResponse<BaseException> error(BaseExceptionStatus status) {
        return new BaseResponse<>(status.getCode(), status.getMessage(), null);
    }

    public static BaseResponse<BaseException> error(BaseExceptionStatus status, String message) {
        return new BaseResponse<>(status.getCode(), message, null);
    }
}

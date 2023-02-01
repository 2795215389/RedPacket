package com.js.api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    public BaseResponse(StatusCode statusCode){
        this.code=statusCode.getCode();
        this.msg=statusCode.getMsg();
    }

}

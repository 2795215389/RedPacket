package com.js.api.response;

public enum StatusCode {
    Success(200,"成功！"),
    Failed(500,"失败！"),
    InvalidParm(401,"非法的参数！"),
    InvalidGrantType(402,"非法的授权类型！")
    ;
    private Integer code;
    private String msg;

    StatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

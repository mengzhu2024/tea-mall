package com.graduation.common;

public class Result<T> {

    private Integer code;

    private T data;

    private String msg;

    public Result(T data) {
        this.code = 0;
        this.data = data;
        this.msg = "操作成功";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Result<?> success() {
        return new Result<>(null);
    }

    public static Result<?> failed(String msg) {
        Result<?> result = new Result<>(null);
        result.setMsg(msg);
        result.setCode(1);
        return result;
    }
}

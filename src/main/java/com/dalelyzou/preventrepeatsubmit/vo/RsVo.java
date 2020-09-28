package com.dalelyzou.preventrepeatsubmit.vo;

import lombok.Data;

@Data
public class RsVo<T> {
    private boolean succeeded;
    private int code;
    private String msg;
    private T data;

    public RsVo(String msg) {
        this.succeeded = true;
        this.code = 200;
        this.msg = msg;
    }

    public RsVo(boolean succeeded, int code, String msg, T data) {
        this.succeeded = succeeded;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static RsVo success(Object data){
        return new RsVo(true, 200, "success", data);
    }

    public static RsVo failButNormal(String msg){
        return new RsVo(true,205, msg,null);
    }

    public static RsVo failButNormal(String msg, Object data){
        return new RsVo(true,205, msg, data);
    }

    public static RsVo failButNormal2(String msg){
        return new RsVo(true,435, msg,null);
    }

    public static RsVo failButNormal3(Object data){
        return new RsVo(true,212, "success",data);
    }

    public static RsVo fail(String msg){
        return new RsVo(true,500,msg,null);
    }

    public static RsVo success(String msg, Object data){
        return new RsVo(true, 200, msg, data);
    }

    public boolean successful(){
        return this.code == 200;
    }

    public T getData() {
        return data;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

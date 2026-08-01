package com.erywim.common;

import lombok.Data;

@Data
public class R {
    private String code;
    private String msg;
    private Object data;
    public static R ok(Object data) {
        R r = new R();
        r.setCode("200");
        r.setData(data);
        return r;
    }
    public static R ok(String msg, Object data){
        R r = new R();
        r.setCode("200");
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
    public static R ok(String  msg){
        R r = new R();
        r.setCode("200");
        r.setMsg(msg);
        return r;
    }

    public static R error(String msg) {
        R r = new R();
        r.setCode("500");
        r.setMsg(msg);
        return r;
    }
}

package com.neuedu.yyzx.utils;

import lombok.Data;

@Data
public class ResultVo<T> {
    private boolean flag;
    private String message;
    private T data;

    private ResultVo() {}

    public static <T> ResultVo<T> ok(T data) {
        ResultVo<T> vo = new ResultVo<>();
        vo.flag = true;
        vo.message = "操作成功";
        vo.data = data;
        return vo;
    }

    public static <T> ResultVo<T> ok() {
        ResultVo<T> vo = new ResultVo<>();
        vo.flag = true;
        vo.message = "操作成功";
        return vo;
    }

    public static <T> ResultVo<T> ok(String message, T data) {
        ResultVo<T> vo = new ResultVo<>();
        vo.flag = true;
        vo.message = message;
        vo.data = data;
        return vo;
    }

    public static <T> ResultVo<T> fail(String message) {
        ResultVo<T> vo = new ResultVo<>();
        vo.flag = false;
        vo.message = message;
        return vo;
    }
}

package com.xqlh.handscoordination.entity;

/**
 * Created by Administrator on 2017/9/6.
 */

public class EntityRegisterCheck {

    /**
     * code : 1
     * msg : OK
     * Result : true
     */

    private int code;
    private String msg;
    private boolean Result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean Result) {
        this.Result = Result;
    }
}

package com.module.questionnaire.bean.response;

public class BaseResponse {

    /**
     * success : true
     * msg : success
     * data : 您的订单
     * code : 200
     */

    private boolean success;
    private String msg;
    private String data;
    private int code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

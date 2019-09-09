package com.module.questionnaire.bean.response;

public class AppConfigResponse {

    /**
     * success : true
     * msg : success
     * data : {"api_url":"http://192.168.10.165/","app_url":"http://saas1.dev.qqdkj.net:9001/","app_btn":false,"agree_url":"http://192.168.10.165/api/v1/agree"}
     * code : 200
     */

    private boolean success;
    private String msg;
    private DataBean data;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class DataBean {
        /**
         * api_url : http://192.168.10.165/
         * app_url : http://saas1.dev.qqdkj.net:9001/
         * app_btn : false
         * agree_url : http://192.168.10.165/api/v1/agree
         */

        //BaseUrl
        private String api_url;
        private String app_url;
        private boolean app_btn;
        //协议Url
        private String agree_url;

        public String getApi_url() {
            return api_url;
        }

        public void setApi_url(String api_url) {
            this.api_url = api_url;
        }

        public String getApp_url() {
            return app_url;
        }

        public void setApp_url(String app_url) {
            this.app_url = app_url;
        }

        public boolean isApp_btn() {
            return app_btn;
        }

        public void setApp_btn(boolean app_btn) {
            this.app_btn = app_btn;
        }

        public String getAgree_url() {
            return agree_url;
        }

        public void setAgree_url(String agree_url) {
            this.agree_url = agree_url;
        }
    }
}

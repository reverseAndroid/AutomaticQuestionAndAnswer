package com.module.questionnaire.bean.response;

public class AppConfigResponse {

    /**
     * success : true
     * msg : success
     * data : {"appid":"com.loans.lion","appname":"LionLoan","app_name":"LionLoan","name":"狮子贷","url":"https://lion.s1.anxinabc.com/","tabs":null}
     */

    private boolean success;
    private String msg;
    private DataBean data;

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

    public static class DataBean {
        /**
         * appid : com.loans.lion
         * appname : LionLoan
         * app_name : LionLoan
         * name : 狮子贷
         * url : https://lion.s1.anxinabc.com/
         * tabs : null
         */

        private String appid;
        private String appname;
        private String app_name;
        private String name;
        private String url;
        private Object tabs;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Object getTabs() {
            return tabs;
        }

        public void setTabs(Object tabs) {
            this.tabs = tabs;
        }
    }
}

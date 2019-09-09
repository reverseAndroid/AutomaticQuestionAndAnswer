package com.module.questionnaire.bean.response;

import java.util.List;

public class MeListResponse {

    /**
     * success : true
     * msg : success
     * data : [[{"id":0,"title":"答题记录","desc":"http://store.qqdkj.net/test/mine_dtjt_icon@2x.png","rout":""},{"id":1,"title":"浏览历史","desc":"http://store.qqdkj.net/test/mine_llls_icon@2x.png","rout":""},{"id":2,"title":"我的收藏","desc":"http://store.qqdkj.net/test/mine_wdsc_icon@2x.png","rout":""}],[{"id":3,"title":"反馈建议","desc":"http://store.qqdkj.net/test/mine_fkjy_icon@2x.png","rout":""},{"id":4,"title":"邀请好友","desc":"http://store.qqdkj.net/test/mine_yqhy_icon@2x.png","rout":""},{"id":5,"title":"设置","desc":"http://store.qqdkj.net/test/mine_sz_icon@2x.png","rout":""}]]
     * code : 200
     */

    private boolean success;
    private String msg;
    private int code;
    private List<List<DataBean>> data;

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<List<DataBean>> getData() {
        return data;
    }

    public void setData(List<List<DataBean>> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 0
         * title : 答题记录
         * desc : http://store.qqdkj.net/test/mine_dtjt_icon@2x.png
         * rout :
         */

        private int id;
        private String title;
        private String desc;
        private String rout;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getRout() {
            return rout;
        }

        public void setRout(String rout) {
            this.rout = rout;
        }
    }
}

package com.module.questionnaire.bean.response;

public class BootPlanResponse {

    /**
     * success : true
     * msg : success
     * data : {"id":3,"type":2,"name":"还款方案","content":"[[1,2],[3,4,5,6,7,8,9,10,11],[12]]"}
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
         * id : 3
         * type : 2
         * name : 还款方案
         * content : [[1,2],[3,4,5,6,7,8,9,10,11],[12]]
         */

        private int id;
        private int type;
        private String name;
        private String content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

package com.module.questionnaire.bean.response;

import java.util.List;

public class QuestionResponse {

    /**
     * success : true
     * msg : success
     * data : [{"id":1,"label":"你想要借款的金额","type":1,"decision":0,"comments":null}
     * code : 200
     */

    private boolean success;
    private String msg;
    private int code;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * label : 你想要借款的金额
         * type : 1
         * decision : 0
         * comments : null
         */

        private int id;
        private String label;
        private int type;
        private int decision;
        private Object comments;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getDecision() {
            return decision;
        }

        public void setDecision(int decision) {
            this.decision = decision;
        }

        public Object getComments() {
            return comments;
        }

        public void setComments(Object comments) {
            this.comments = comments;
        }
    }
}

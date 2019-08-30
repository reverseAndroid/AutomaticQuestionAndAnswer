package com.module.questionnaire.bean.response;

import java.util.List;
import java.util.Map;

public class AnswerResponse {

    /**
     * success : true
     * msg : success
     * data : {"1":[{"id":1,"question_id":1,"label":"$30","list_order":1},{"id":2,"question_id":1,"label":"$50","list_order":2},{"id":3,"question_id":1,"label":"$70","list_order":3},{"id":4,"question_id":1,"label":"$100","list_order":4}],"2":[{"id":5,"question_id":2,"label":"7","list_order":1},{"id":6,"question_id":2,"label":"14","list_order":2}],"4":[{"id":7,"question_id":4,"label":"0","list_order":1},{"id":8,"question_id":4,"label":"1","list_order":2}]}
     * code : 200
     */

    private boolean success;
    private String msg;
    private Map<String, List<DataBean>> data;
    //    private Object data;
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

    public Map<String, List<DataBean>> getData() {
        return data;
    }

    public void setData(Map<String, List<DataBean>> data) {
        this.data = data;
    }

    //    public Object getData() {
//        return data;
//    }
//
//    public void setData(Object data) {
//        this.data = data;
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public class DataBean {

        private int id;
        private int question_id;
        private String label;
        private int list_order;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(int question_id) {
            this.question_id = question_id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getList_order() {
            return list_order;
        }

        public void setList_order(int list_order) {
            this.list_order = list_order;
        }
    }
}

package com.module.questionnaire.bean.response;

import java.util.List;

public class DecisionMakingResponse {

    /**
     * success : true
     * msg : success
     * data : [{"id":1,"answer_ids":[31],"close_questions":[18],"open_questions":[15,16,17,18,20,21,22],"status":0,"delete_at":null,"create_at":"1567247070"},{"id":2,"answer_ids":[46],"close_questions":[47],"open_questions":[45,46],"status":0,"delete_at":null,"create_at":"1567247070"},{"id":3,"answer_ids":[47],"close_questions":[45,46],"open_questions":[47],"status":0,"delete_at":null,"create_at":"1567247070"}]
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
         * answer_ids : [31]
         * close_questions : [18]
         * open_questions : [15,16,17,18,20,21,22]
         * status : 0
         * delete_at : null
         * create_at : 1567247070
         */

        private int id;
        private int status;
        private Object delete_at;
        private String create_at;
        private List<Integer> answer_ids;
        private List<Integer> close_questions;
        private List<Integer> open_questions;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getDelete_at() {
            return delete_at;
        }

        public void setDelete_at(Object delete_at) {
            this.delete_at = delete_at;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        public List<Integer> getAnswer_ids() {
            return answer_ids;
        }

        public void setAnswer_ids(List<Integer> answer_ids) {
            this.answer_ids = answer_ids;
        }

        public List<Integer> getClose_questions() {
            return close_questions;
        }

        public void setClose_questions(List<Integer> close_questions) {
            this.close_questions = close_questions;
        }

        public List<Integer> getOpen_questions() {
            return open_questions;
        }

        public void setOpen_questions(List<Integer> open_questions) {
            this.open_questions = open_questions;
        }
    }
}

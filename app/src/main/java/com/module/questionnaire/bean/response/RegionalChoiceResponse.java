package com.module.questionnaire.bean.response;

import com.module.questionnaire.bean.RegionalChoiceBean;

import java.util.List;

public class RegionalChoiceResponse {

    /**
     * success : true
     * msg : success
     * data : [{"id":"110000","other_name":"ខេត្តបន្ទាយមានជ័យ","pid":"0"},{"id":"110100","other_name":"ស្រុកមង្គលបុរី","pid":"110000"},{"id":"110200","other_name":"ស្រុកភ្នំស្រុក","pid":"110000"},{"id":"110300","other_name":"ស្រុកព្រះនេត្រព្រះ","pid":"110000"},{"id":"110400","other_name":"ស្រុកអូរជ្រៅ","pid":"110000"},{"id":"110500","other_name":"ក្រុងសិរីសោភ័ណ","pid":"110000"},{"id":"110600","other_name":"ស្រុកថ្មពួក","pid":"110000"},{"id":"110700","other_name":"ស្រុកស្វាយចេក","pid":"110000"},{"id":"110800","other_name":"ស្រុកម៉ាឡៃ","pid":"110000"},{"id":"120000","other_name":"ខេត្តបាត់ដំបង","pid":"0"},{"id":"120100","other_name":"ស្រុកបាណន់","pid":"120000"},{"id":"120200","other_name":"ស្រុកថ្មគោល","pid":"120000"},{"id":"120300","other_name":"ក្រុងបាត់ដំបង","pid":"120000"},{"id":"120400","other_name":"ស្រុកបវេល","pid":"120000"},{"id":"120500","other_name":"ស្រុកឯកភ្នំ","pid":"120000"},{"id":"120600","other_name":"ស្រុកមោងឫស្សី","pid":"120000"}]
     * code : 200
     */

    private boolean success;
    private String msg;
    private int code;
    private List<RegionalChoiceBean> data;

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

    public List<RegionalChoiceBean> getData() {
        return data;
    }

    public void setData(List<RegionalChoiceBean> data) {
        this.data = data;
    }

//    public static class DataBean {
//        /**
//         * id : 110000
//         * other_name : ខេត្តបន្ទាយមានជ័យ
//         * pid : 0
//         */
//
//        private String id;
//        private String other_name;
//        private String pid;
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getOther_name() {
//            return other_name;
//        }
//
//        public void setOther_name(String other_name) {
//            this.other_name = other_name;
//        }
//
//        public String getPid() {
//            return pid;
//        }
//
//        public void setPid(String pid) {
//            this.pid = pid;
//        }
//    }
}

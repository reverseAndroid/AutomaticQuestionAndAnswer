package com.module.questionnaire.bean;

import java.util.List;

public class QuestionAnswerBean {

    private Integer id;
    private String type;
    private String label;
    //是否是连续问题
    private boolean isContinuous;
    //是否关闭这项问题
    private boolean isCloseQuestion;
    private List<Item> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isContinuous() {
        return isContinuous;
    }

    public void setContinuous(boolean continuous) {
        isContinuous = continuous;
    }

    public boolean isCloseQuestion() {
        return isCloseQuestion;
    }

    public void setCloseQuestion(boolean closeQuestion) {
        isCloseQuestion = closeQuestion;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {

        private int id;
        private String value;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

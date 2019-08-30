package com.module.questionnaire.bean.response;

public class LoginResponse {

    /**
     * success : true
     * msg : success
     * data : {"access_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvbGlvbi5zMS5hbnhpbmFiYy5jb20iLCJpYXQiOjE1NjcxMzc1MzEsIm5iZiI6MTU2NzEzNzUzMSwiZXhwIjoxNTY4NDMzNTMxLCJzdWIiOiJjM2xaWVhCUFFVaFdkRGxTVUV0T00wMWtNMnN4UVQwOSJ9.pyA-rudU1vmw7BPyDo5zHMdm9cBJCnDnysYGzh-hHO0","user":{"id":1,"mobile":"18993195341","first_name":"hanlongfei","last_name":null,"amount":8888,"avatar":null,"first_login":false,"is_check":false,"is_pass":false,"is_contacts":true}}
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
         * access_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvbGlvbi5zMS5hbnhpbmFiYy5jb20iLCJpYXQiOjE1NjcxMzc1MzEsIm5iZiI6MTU2NzEzNzUzMSwiZXhwIjoxNTY4NDMzNTMxLCJzdWIiOiJjM2xaWVhCUFFVaFdkRGxTVUV0T00wMWtNMnN4UVQwOSJ9.pyA-rudU1vmw7BPyDo5zHMdm9cBJCnDnysYGzh-hHO0
         * user : {"id":1,"mobile":"18993195341","first_name":"hanlongfei","last_name":null,"amount":8888,"avatar":null,"first_login":false,"is_check":false,"is_pass":false,"is_contacts":true}
         */

        private String access_token;
        private UserBean user;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * id : 1
             * mobile : 18993195341
             * first_name : hanlongfei
             * last_name : null
             * amount : 8888
             * avatar : null
             * first_login : false
             * is_check : false
             * is_pass : false
             * is_contacts : true
             */

            private int id;
            private String mobile;
            private String first_name;
            private Object last_name;
            private int amount;
            private Object avatar;
            private boolean first_login;
            private boolean is_check;
            private boolean is_pass;
            private boolean is_contacts;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public Object getLast_name() {
                return last_name;
            }

            public void setLast_name(Object last_name) {
                this.last_name = last_name;
            }

            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }

            public Object getAvatar() {
                return avatar;
            }

            public void setAvatar(Object avatar) {
                this.avatar = avatar;
            }

            public boolean isFirst_login() {
                return first_login;
            }

            public void setFirst_login(boolean first_login) {
                this.first_login = first_login;
            }

            public boolean isIs_check() {
                return is_check;
            }

            public void setIs_check(boolean is_check) {
                this.is_check = is_check;
            }

            public boolean isIs_pass() {
                return is_pass;
            }

            public void setIs_pass(boolean is_pass) {
                this.is_pass = is_pass;
            }

            public boolean isIs_contacts() {
                return is_contacts;
            }

            public void setIs_contacts(boolean is_contacts) {
                this.is_contacts = is_contacts;
            }
        }
    }
}

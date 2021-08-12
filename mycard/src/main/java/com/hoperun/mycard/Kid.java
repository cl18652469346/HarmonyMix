package com.hoperun.mycard;

import java.util.List;

public class Kid {

    /**
     * code : 200
     * msg : success
     * newslist : [{"quest":"一根生锈的绣花针，在7月7日7时7分7秒，浩月当空之时，扔到云南饵海中将会发生什么反应？","result":"掉到海底"}]
     */

    private int code;
    private String msg;
    private List<NewslistBean> newslist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<NewslistBean> getNewslist() {
        return newslist;
    }

    public void setNewslist(List<NewslistBean> newslist) {
        this.newslist = newslist;
    }

    public static class NewslistBean {
        /**
         * quest : 一根生锈的绣花针，在7月7日7时7分7秒，浩月当空之时，扔到云南饵海中将会发生什么反应？
         * result : 掉到海底
         */

        private String quest;
        private String result;

        public String getQuest() {
            return quest;
        }

        public void setQuest(String quest) {
            this.quest = quest;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}

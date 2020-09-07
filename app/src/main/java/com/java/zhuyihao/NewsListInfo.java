package com.java.zhuyihao;

import androidx.annotation.NonNull;

import java.util.List;

public class NewsListInfo {
    private boolean status;
    private List<NewsInfo> data;
    private  Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<NewsInfo> getData() {
        return data;
    }

    public void setData(List<NewsInfo> data) {
        this.data = data;
    }

    public static class Pagination{
        private int page;
        private int size;
        private int total;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class NewsInfo{
        private String _id;
        private String title;
        private String content;
        private String date;
        private String type;
        private String lang;
        private List<AuthorInfo> authors;

        @NonNull
        @Override
        public String toString() {
            return _id+' '+title+' '+content+' '+date;
        }

        public List<AuthorInfo> getAuthors() {
            return authors;
        }

        public void setAuthors(List<AuthorInfo> authors) {
            this.authors = authors;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public static class AuthorInfo{
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        };

    };
}

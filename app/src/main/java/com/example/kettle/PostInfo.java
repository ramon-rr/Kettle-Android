package com.example.kettle;

import java.io.Serializable;

public class PostInfo implements Serializable {
    private String user;
    private String title;
    String body;

    public PostInfo(String user, String title, String body){
        this.user = user;
        this.title = title;
        this.body = body;
    }

    public String getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}

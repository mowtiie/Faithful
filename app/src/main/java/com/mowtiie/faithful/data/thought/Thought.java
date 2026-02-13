package com.mowtiie.faithful.data.thought;

import java.io.Serializable;

public class Thought implements Serializable {

    private int id;
    private String content;
    private long timestamp;

    private Thought() {
        // Just a empty constructor (can be useful for some frameworks)
    }

    private Thought(int id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
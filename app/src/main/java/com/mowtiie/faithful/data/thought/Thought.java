package com.mowtiie.faithful.data.thought;

import java.io.Serializable;

public class Thought implements Serializable {

    private String id;
    private String content;
    private long timestamp;

    public Thought() {
        // Just a empty constructor (can be useful for some frameworks)
    }

    public Thought(String id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
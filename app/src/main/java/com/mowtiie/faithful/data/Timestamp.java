package com.mowtiie.faithful.data;

public enum Timestamp {

    DYNAMIC("Dynamic"),
    FORMAL("Formal");

    public final String value;

    Timestamp(String value) {
        this.value = value;
    }

}
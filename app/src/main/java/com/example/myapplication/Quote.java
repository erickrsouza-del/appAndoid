package com.example.myapplication;

public class Quote {
    private final String text;
    private final String imageKeyword;

    public Quote(String text, String imageKeyword) {
        this.text = text;
        this.imageKeyword = imageKeyword;
    }

    public String getText() {
        return text;
    }

    public String getImageKeyword() {
        return imageKeyword;
    }
}

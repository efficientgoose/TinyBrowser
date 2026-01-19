package com.tinybrowser.dom;

public class TextNode extends Node {
    private String text;

    public TextNode(String text) {
        super();
        this.text = text != null ? text : "";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
    }
}

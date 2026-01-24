package com.tinybrowser.server;

public class ParseResponse {
    private String htmlContent;
    private Object styledTree;

    public ParseResponse() {
    }

    public ParseResponse(String htmlContent, Object styledTree) {
        this.htmlContent = htmlContent;
        this.styledTree = styledTree;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Object getStyledTree() {
        return styledTree;
    }

    public void setStyledTree(Object styledTree) {
        this.styledTree = styledTree;
    }
}

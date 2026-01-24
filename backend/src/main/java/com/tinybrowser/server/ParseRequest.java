package com.tinybrowser.server;

public class ParseRequest {
    private String filePath;

    public ParseRequest() {
    }

    public ParseRequest(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

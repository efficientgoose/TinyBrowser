package com.tinybrowser;

import com.tinybrowser.server.BrowserServer;

public class Main {
    public static void main(String[] args) {
        BrowserServer server = new BrowserServer(8080);
        System.out.println("TinyBrowser backend server running on http://localhost:8080");
        System.out.println("API endpoint: POST http://localhost:8080/api/parse");
        System.out.println("Press Ctrl+C to stop the server");
    }
}

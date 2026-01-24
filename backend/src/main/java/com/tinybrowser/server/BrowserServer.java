package com.tinybrowser.server;

import com.tinybrowser.css.CssParser;
import com.tinybrowser.css.StyleSheet;
import com.tinybrowser.dom.Document;
import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;
import com.tinybrowser.parser.HtmlParser;
import com.tinybrowser.style.StyleEngine;
import com.tinybrowser.style.StyledNode;
import com.tinybrowser.util.JsonSerializer;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BrowserServer {
    private final Javalin app;

    public BrowserServer(int port) {
        app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        }).start(port);
        setupRoutes();
    }

    private void setupRoutes() {
        app.post("/api/parse", this::handleParse);

        app.get("/", ctx -> {
            ctx.result("TinyBrowser Backend API - Ready\n\nEndpoints:\n  POST /api/parse - Parse HTML file");
        });
    }

    private void handleParse(Context ctx) {
        try {
            ParseRequest req = ctx.bodyAsClass(ParseRequest.class);
            String filePath = req.getFilePath();

            if (filePath == null || filePath.isBlank()) {
                ctx.status(400).json(Map.of("error", "filePath is required"));
                return;
            }

            String html = Files.readString(Path.of(filePath));

            HtmlParser parser = new HtmlParser();
            Document doc = parser.parseDocument(html);

            String css = extractCssFromDocument(doc.getRootElement());

            CssParser cssParser = new CssParser();
            StyleSheet styleSheet = cssParser.parse(css);

            StyleEngine styleEngine = new StyleEngine();
            StyledNode styledRoot = styleEngine.computeStyles(doc, styleSheet);

            JsonSerializer.StyledNodeJson styledTree = JsonSerializer.serializeStyledNode(styledRoot);

            ParseResponse response = new ParseResponse(html, styledTree);
            ctx.json(response);

        } catch (java.nio.file.NoSuchFileException e) {
            ctx.status(404).json(Map.of("error", "File not found: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Error processing file: " + e.getMessage()));
        }
    }

    private String extractCssFromDocument(Node node) {
        StringBuilder css = new StringBuilder();

        if (node instanceof Element) {
            Element element = (Element) node;
            if ("style".equals(element.getTagName())) {
                for (Node child : element.getChildren()) {
                    if (child instanceof TextNode) {
                        css.append(((TextNode) child).getText()).append("\n");
                    }
                }
            }
        }

        for (Node child : node.getChildren()) {
            css.append(extractCssFromDocument(child));
        }

        return css.toString();
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}

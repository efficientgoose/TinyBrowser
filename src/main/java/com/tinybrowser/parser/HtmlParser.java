package com.tinybrowser.parser;

import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;

public class HtmlParser {

    public HtmlParser() {
    }

    public Node parse(String html) {
        if (html == null || html.isBlank()) {
            return new Element("html");
        }

        Element root = new Element("html");
        TextNode textNode = new TextNode("Parsing not yet implemented. Content: " + html.substring(0, Math.min(50, html.length())));
        root.appendChild(textNode);

        return root;
    }
}

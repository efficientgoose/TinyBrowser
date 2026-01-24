package com.tinybrowser.util;

import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;
import com.tinybrowser.style.StyledNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonSerializer {

    public static StyledNodeJson serializeStyledNode(StyledNode styledNode) {
        if (styledNode == null) {
            return null;
        }

        Node node = styledNode.getNode();
        StyledNodeJson json = new StyledNodeJson();

        if (node instanceof Element) {
            Element element = (Element) node;
            json.type = "element";
            json.tagName = element.getTagName();
            json.attributes = new HashMap<>(element.getAttributes());
        } else if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            json.type = "text";
            json.text = textNode.getText();
        }

        json.styles = new HashMap<>(styledNode.getComputedStyles());

        json.children = new ArrayList<>();
        for (StyledNode child : styledNode.getChildren()) {
            json.children.add(serializeStyledNode(child));
        }

        return json;
    }

    public static class StyledNodeJson {
        public String type;
        public String tagName;
        public Map<String, String> attributes;
        public String text;
        public Map<String, String> styles;
        public List<StyledNodeJson> children;

        public StyledNodeJson() {
            this.attributes = new HashMap<>();
            this.styles = new HashMap<>();
            this.children = new ArrayList<>();
        }
    }
}

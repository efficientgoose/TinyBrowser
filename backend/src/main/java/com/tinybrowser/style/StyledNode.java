package com.tinybrowser.style;

import com.tinybrowser.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyledNode {
    private final Node node;
    private final Map<String, String> computedStyles;
    private final List<StyledNode> children;

    public StyledNode(Node node) {
        this.node = node;
        this.computedStyles = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public Node getNode() {
        return node;
    }

    public Map<String, String> getComputedStyles() {
        return computedStyles;
    }

    public void setStyle(String property, String value) {
        computedStyles.put(property, value);
    }

    public String getStyle(String property) {
        return computedStyles.get(property);
    }

    public String getStyleOrDefault(String property, String defaultValue) {
        return computedStyles.getOrDefault(property, defaultValue);
    }

    public List<StyledNode> getChildren() {
        return children;
    }

    public void appendChild(StyledNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StyledNode{");
        sb.append("node=").append(node.getClass().getSimpleName());
        if (!computedStyles.isEmpty()) {
            sb.append(", styles=").append(computedStyles);
        }
        sb.append("}");
        return sb.toString();
    }
}

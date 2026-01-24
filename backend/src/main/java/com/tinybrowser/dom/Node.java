package com.tinybrowser.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node {
    private final List<Node> children;
    private Node parent;

    public Node() {
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public void appendChild(Node child) {
        if (child == null) {
            throw new IllegalArgumentException("Child node cannot be null");
        }
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Node child) {
        if (child == null) {
            throw new IllegalArgumentException("Child node cannot be null");
        }
        if (children.remove(child)) {
            child.parent = null;
        }
    }

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Node getParent() {
        return parent;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public String getTextContent() {
        StringBuilder sb = new StringBuilder();
        collectTextContent(this, sb);
        return sb.toString();
    }

    private void collectTextContent(Node node, StringBuilder sb) {
        if (node instanceof TextNode) {
            sb.append(((TextNode) node).getText());
        }

        for (Node child : node.getChildren()) {
            collectTextContent(child, sb);
        }
    }

    public String toTreeString() {
        return toTreeString(0);
    }

    private String toTreeString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indentation = "  ".repeat(indent);

        if (this instanceof Element) {
            Element element = (Element) this;
            sb.append(indentation).append("<").append(element.getTagName());

            if (!element.getAttributes().isEmpty()) {
                for (var entry : element.getAttributes().entrySet()) {
                    sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                }
            }

            if (hasChildren()) {
                sb.append(">\n");
                for (Node child : getChildren()) {
                    sb.append(child.toTreeString(indent + 1));
                }
                sb.append(indentation).append("</").append(element.getTagName()).append(">\n");
            } else {
                sb.append(" />\n");
            }
        } else if (this instanceof TextNode) {
            TextNode textNode = (TextNode) this;
            String text = textNode.getText().trim();
            if (!text.isEmpty()) {
                sb.append(indentation).append("TEXT: \"").append(text).append("\"\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toTreeString();
    }
}

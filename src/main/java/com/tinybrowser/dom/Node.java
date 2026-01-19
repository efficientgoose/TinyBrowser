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
}

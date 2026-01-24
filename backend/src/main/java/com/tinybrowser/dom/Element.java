package com.tinybrowser.dom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Element extends Node {
    private final String tagName;
    private final Map<String, String> attributes;

    public Element(String tagName) {
        super();
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be null or blank");
        }
        this.tagName = tagName.toLowerCase();
        this.attributes = new HashMap<>();
    }

    public String getTagName() {
        return tagName;
    }

    public String getAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        return attributes.get(name.toLowerCase());
    }

    public void setAttribute(String name, String value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attribute name cannot be null or blank");
        }
        attributes.put(name.toLowerCase(), value != null ? value : "");
    }

    public boolean hasAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        return attributes.containsKey(name.toLowerCase());
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}

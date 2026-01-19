package com.tinybrowser.parser;

import java.util.HashMap;
import java.util.Map;

public class Token {
    private final TokenType type;
    private String tagName;
    private Map<String, String> attributes;
    private String content;

    public Token(TokenType type) {
        this.type = type;
        this.attributes = new HashMap<>();
    }

    public Token(TokenType type, String tagName) {
        this.type = type;
        this.tagName = tagName;
        this.attributes = new HashMap<>();
    }

    public TokenType getType() {
        return type;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token{type=").append(type);

        if (tagName != null) {
            sb.append(", tagName='").append(tagName).append("'");
        }

        if (!attributes.isEmpty()) {
            sb.append(", attributes=").append(attributes);
        }

        if (content != null) {
            String displayContent = content.length() > 50
                ? content.substring(0, 47) + "..."
                : content;
            sb.append(", content='").append(displayContent).append("'");
        }

        sb.append("}");
        return sb.toString();
    }
}

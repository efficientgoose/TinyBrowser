package com.tinybrowser.css;

public class CssToken {
    private final CssTokenType type;
    private String value;

    public CssToken(CssTokenType type) {
        this.type = type;
        this.value = "";
    }

    public CssToken(CssTokenType type, String value) {
        this.type = type;
        this.value = value != null ? value : "";
    }

    public CssTokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value.isEmpty()) {
            return "CssToken{type=" + type + "}";
        }
        return "CssToken{type=" + type + ", value='" + value + "'}";
    }
}

package com.tinybrowser.parser;

import java.util.HashMap;
import java.util.Map;

public class HtmlTokenizer {
    private final String input;
    private int position;
    private boolean skipWhitespaceText;

    public HtmlTokenizer(String input) {
        this(input, true);
    }

    public HtmlTokenizer(String input, boolean skipWhitespaceText) {
        this.input = input != null ? input : "";
        this.position = 0;
        this.skipWhitespaceText = skipWhitespaceText;
    }

    public Token nextToken() {
        if (position >= input.length()) {
            return new Token(TokenType.EOF);
        }

        char current = input.charAt(position);

        // Check for tag start
        if (current == '<') {
            return parseTag();
        }

        // Otherwise, it's text content
        return parseText();
    }

    private Token parseTag() {
        position++; // Skip '<'

        if (position >= input.length()) {
            return new Token(TokenType.EOF);
        }

        char next = input.charAt(position);

        // Check for comment: <!--
        if (next == '!' && position + 2 < input.length() &&
            input.charAt(position + 1) == '-' && input.charAt(position + 2) == '-') {
            return parseComment();
        }

        // Check for DOCTYPE: <!DOCTYPE
        if (next == '!' && position + 7 < input.length() &&
            input.substring(position, position + 8).equalsIgnoreCase("!DOCTYPE")) {
            return parseDoctype();
        }

        // Check for end tag: </
        if (next == '/') {
            return parseEndTag();
        }

        // Otherwise, it's a start tag or self-closing tag
        return parseStartTag();
    }

    private Token parseComment() {
        position += 3; // Skip '!--'

        StringBuilder content = new StringBuilder();
        while (position < input.length()) {
            if (position + 2 < input.length() &&
                input.charAt(position) == '-' &&
                input.charAt(position + 1) == '-' &&
                input.charAt(position + 2) == '>') {
                position += 3; // Skip '-->'
                break;
            }
            content.append(input.charAt(position));
            position++;
        }

        Token token = new Token(TokenType.COMMENT);
        token.setContent(content.toString());
        return token;
    }

    private Token parseDoctype() {
        StringBuilder content = new StringBuilder();
        while (position < input.length() && input.charAt(position) != '>') {
            content.append(input.charAt(position));
            position++;
        }
        if (position < input.length()) {
            position++; // Skip '>'
        }

        Token token = new Token(TokenType.DOCTYPE);
        token.setContent(content.toString().trim());
        return token;
    }

    private Token parseEndTag() {
        position++; // Skip '/'

        StringBuilder tagName = new StringBuilder();
        while (position < input.length()) {
            char ch = input.charAt(position);
            if (ch == '>') {
                position++; // Skip '>'
                break;
            }
            if (!Character.isWhitespace(ch)) {
                tagName.append(ch);
            }
            position++;
        }

        return new Token(TokenType.END_TAG, tagName.toString().toLowerCase());
    }

    private Token parseStartTag() {
        // Parse tag name
        StringBuilder tagName = new StringBuilder();
        while (position < input.length()) {
            char ch = input.charAt(position);
            if (Character.isWhitespace(ch) || ch == '>' || ch == '/') {
                break;
            }
            tagName.append(ch);
            position++;
        }

        String tag = tagName.toString().toLowerCase();

        // Skip whitespace
        skipWhitespace();

        // Parse attributes
        Map<String, String> attributes = parseAttributes();

        // Check for self-closing
        boolean selfClosing = false;
        if (position < input.length() && input.charAt(position) == '/') {
            selfClosing = true;
            position++; // Skip '/'
        }

        // Skip to '>'
        while (position < input.length() && input.charAt(position) != '>') {
            position++;
        }
        if (position < input.length()) {
            position++; // Skip '>'
        }

        Token token = new Token(selfClosing ? TokenType.SELF_CLOSING_TAG : TokenType.START_TAG, tag);
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            token.setAttribute(attr.getKey(), attr.getValue());
        }

        return token;
    }

    private Map<String, String> parseAttributes() {
        Map<String, String> attributes = new HashMap<>();

        while (position < input.length()) {
            char ch = input.charAt(position);

            // Stop at tag end or self-closing indicator
            if (ch == '>' || ch == '/') {
                break;
            }

            // Skip whitespace
            if (Character.isWhitespace(ch)) {
                position++;
                continue;
            }

            // Parse attribute name
            StringBuilder attrName = new StringBuilder();
            while (position < input.length()) {
                ch = input.charAt(position);
                if (ch == '=' || Character.isWhitespace(ch) || ch == '>' || ch == '/') {
                    break;
                }
                attrName.append(ch);
                position++;
            }

            String name = attrName.toString().toLowerCase();
            if (name.isEmpty()) {
                break;
            }

            // Skip whitespace
            skipWhitespace();

            // Check for '='
            String value = "";
            if (position < input.length() && input.charAt(position) == '=') {
                position++; // Skip '='
                skipWhitespace();

                // Parse attribute value
                value = parseAttributeValue();
            }

            attributes.put(name, value);
            skipWhitespace();
        }

        return attributes;
    }

    private String parseAttributeValue() {
        if (position >= input.length()) {
            return "";
        }

        char quote = input.charAt(position);

        // Quoted value
        if (quote == '"' || quote == '\'') {
            position++; // Skip opening quote
            StringBuilder value = new StringBuilder();

            while (position < input.length()) {
                char ch = input.charAt(position);
                if (ch == quote) {
                    position++; // Skip closing quote
                    break;
                }
                value.append(ch);
                position++;
            }
            return value.toString();
        }

        // Unquoted value
        StringBuilder value = new StringBuilder();
        while (position < input.length()) {
            char ch = input.charAt(position);
            if (Character.isWhitespace(ch) || ch == '>' || ch == '/') {
                break;
            }
            value.append(ch);
            position++;
        }
        return value.toString();
    }

    private Token parseText() {
        StringBuilder text = new StringBuilder();

        while (position < input.length()) {
            char ch = input.charAt(position);
            if (ch == '<') {
                break;
            }
            text.append(ch);
            position++;
        }

        String content = text.toString();

        // Skip whitespace-only text nodes if configured
        if (skipWhitespaceText && content.trim().isEmpty()) {
            return nextToken();
        }

        Token token = new Token(TokenType.TEXT);
        token.setContent(content);
        return token;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }

    public boolean hasMoreTokens() {
        return position < input.length();
    }
}

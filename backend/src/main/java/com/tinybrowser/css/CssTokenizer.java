package com.tinybrowser.css;

public class CssTokenizer {
    private final String input;
    private int position;

    public CssTokenizer(String input) {
        this.input = input != null ? input : "";
        this.position = 0;
    }

    public CssToken nextToken() {
        skipWhitespaceAndComments();

        if (position >= input.length()) {
            return new CssToken(CssTokenType.EOF);
        }

        char current = input.charAt(position);

        // Single-character tokens
        switch (current) {
            case '{':
                position++;
                return new CssToken(CssTokenType.LBRACE);
            case '}':
                position++;
                return new CssToken(CssTokenType.RBRACE);
            case ':':
                position++;
                return new CssToken(CssTokenType.COLON);
            case ';':
                position++;
                return new CssToken(CssTokenType.SEMICOLON);
            case ',':
                position++;
                return new CssToken(CssTokenType.COMMA);
        }

        // If we're here, it's either a selector, property, or value
        // Context determines which - the parser will handle that
        return parseIdentifier();
    }

    private CssToken parseIdentifier() {
        StringBuilder value = new StringBuilder();

        while (position < input.length()) {
            char ch = input.charAt(position);

            // Selector can include: a-z, A-Z, 0-9, -, _, ., #, *, >
            // Stop at whitespace or special CSS characters
            if (Character.isWhitespace(ch) || ch == '{' || ch == '}' ||
                ch == ':' || ch == ';' || ch == ',') {
                break;
            }

            value.append(ch);
            position++;
        }

        // Return a generic token - parser will determine if it's SELECTOR, PROPERTY, or VALUE
        // based on context
        return new CssToken(CssTokenType.SELECTOR, value.toString().trim());
    }

    private void skipWhitespaceAndComments() {
        while (position < input.length()) {
            char ch = input.charAt(position);

            if (Character.isWhitespace(ch)) {
                position++;
                continue;
            }

            // Check for CSS comments /* */
            if (ch == '/' && position + 1 < input.length() && input.charAt(position + 1) == '*') {
                position += 2; // Skip /*
                while (position < input.length()) {
                    if (position + 1 < input.length() &&
                        input.charAt(position) == '*' &&
                        input.charAt(position + 1) == '/') {
                        position += 2; // Skip */
                        break;
                    }
                    position++;
                }
                continue;
            }

            break;
        }
    }

    public int getPosition() {
        return position;
    }

    public boolean hasMoreTokens() {
        skipWhitespaceAndComments();
        return position < input.length();
    }
}

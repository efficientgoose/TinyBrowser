package com.tinybrowser.css;

public class CssParser {

    public StyleSheet parse(String css) {
        if (css == null || css.isBlank()) {
            return new StyleSheet();
        }

        CssTokenizer tokenizer = new CssTokenizer(css);
        StyleSheet styleSheet = new StyleSheet();

        while (tokenizer.hasMoreTokens()) {
            Rule rule = parseRule(tokenizer);
            if (rule != null && !rule.getSelectors().isEmpty()) {
                styleSheet.addRule(rule);
            }
        }

        return styleSheet;
    }

    private Rule parseRule(CssTokenizer tokenizer) {
        Rule rule = new Rule();

        // Parse selectors (can be comma-separated)
        while (true) {
            CssToken token = tokenizer.nextToken();

            if (token.getType() == CssTokenType.EOF) {
                return null;
            }

            if (token.getType() == CssTokenType.LBRACE) {
                // Start of declarations
                break;
            }

            if (token.getType() == CssTokenType.COMMA) {
                // Next selector in group
                continue;
            }

            // It's a selector
            if (!token.getValue().isEmpty()) {
                Selector selector = new Selector(token.getValue());
                rule.addSelector(selector);
            }
        }

        // Parse declarations
        while (true) {
            CssToken token = tokenizer.nextToken();

            if (token.getType() == CssTokenType.EOF || token.getType() == CssTokenType.RBRACE) {
                break;
            }

            if (token.getType() == CssTokenType.SEMICOLON) {
                continue;
            }

            // Property
            String property = token.getValue();

            // Expect colon
            token = tokenizer.nextToken();
            if (token.getType() != CssTokenType.COLON) {
                // Skip malformed declaration
                continue;
            }

            // Value (may contain multiple tokens until semicolon or })
            StringBuilder valueBuilder = new StringBuilder();
            while (true) {
                token = tokenizer.nextToken();

                if (token.getType() == CssTokenType.SEMICOLON ||
                    token.getType() == CssTokenType.RBRACE ||
                    token.getType() == CssTokenType.EOF) {

                    // Add the declaration
                    if (!property.isEmpty() && valueBuilder.length() > 0) {
                        Declaration declaration = new Declaration(property, valueBuilder.toString().trim());
                        rule.addDeclaration(declaration);
                    }

                    if (token.getType() == CssTokenType.RBRACE || token.getType() == CssTokenType.EOF) {
                        return rule;
                    }

                    break;
                }

                // Append to value
                if (valueBuilder.length() > 0) {
                    valueBuilder.append(" ");
                }
                valueBuilder.append(token.getValue());
            }
        }

        return rule;
    }
}

package com.tinybrowser.style;

import java.util.HashMap;
import java.util.Map;

public class CssDefaults {
    private static final Map<String, Map<String, String>> DEFAULT_STYLES = new HashMap<>();

    static {
        // Block-level elements
        addDefaults("html", "display", "block");
        addDefaults("body", "display", "block", "margin", "8px");
        addDefaults("div", "display", "block");
        addDefaults("p", "display", "block", "margin-top", "1em", "margin-bottom", "1em");
        addDefaults("h1", "display", "block", "font-size", "2em", "font-weight", "bold", "margin-top", "0.67em", "margin-bottom", "0.67em");
        addDefaults("h2", "display", "block", "font-size", "1.5em", "font-weight", "bold", "margin-top", "0.83em", "margin-bottom", "0.83em");
        addDefaults("h3", "display", "block", "font-size", "1.17em", "font-weight", "bold", "margin-top", "1em", "margin-bottom", "1em");
        addDefaults("h4", "display", "block", "font-size", "1em", "font-weight", "bold", "margin-top", "1.33em", "margin-bottom", "1.33em");
        addDefaults("h5", "display", "block", "font-size", "0.83em", "font-weight", "bold", "margin-top", "1.67em", "margin-bottom", "1.67em");
        addDefaults("h6", "display", "block", "font-size", "0.67em", "font-weight", "bold", "margin-top", "2.33em", "margin-bottom", "2.33em");
        addDefaults("ul", "display", "block", "margin-top", "1em", "margin-bottom", "1em", "padding-left", "40px", "list-style-type", "disc");
        addDefaults("ol", "display", "block", "margin-top", "1em", "margin-bottom", "1em", "padding-left", "40px", "list-style-type", "decimal");
        addDefaults("li", "display", "list-item");
        addDefaults("blockquote", "display", "block", "margin-top", "1em", "margin-bottom", "1em", "margin-left", "40px", "margin-right", "40px");
        addDefaults("pre", "display", "block", "font-family", "monospace", "white-space", "pre", "margin-top", "1em", "margin-bottom", "1em");
        addDefaults("header", "display", "block");
        addDefaults("footer", "display", "block");
        addDefaults("section", "display", "block");
        addDefaults("article", "display", "block");
        addDefaults("nav", "display", "block");
        addDefaults("aside", "display", "block");

        // Inline elements
        addDefaults("span", "display", "inline");
        addDefaults("a", "display", "inline", "color", "blue", "text-decoration", "underline");
        addDefaults("strong", "display", "inline", "font-weight", "bold");
        addDefaults("b", "display", "inline", "font-weight", "bold");
        addDefaults("em", "display", "inline", "font-style", "italic");
        addDefaults("i", "display", "inline", "font-style", "italic");
        addDefaults("code", "display", "inline", "font-family", "monospace");
        addDefaults("small", "display", "inline", "font-size", "0.83em");

        // Self-closing elements
        addDefaults("br", "display", "block");
        addDefaults("hr", "display", "block", "margin-top", "0.5em", "margin-bottom", "0.5em", "border-top", "1px solid black");
        addDefaults("img", "display", "inline");

        // Form elements
        addDefaults("input", "display", "inline-block");
        addDefaults("button", "display", "inline-block");
        addDefaults("textarea", "display", "inline-block");
        addDefaults("select", "display", "inline-block");

        // Table elements
        addDefaults("table", "display", "table", "border-collapse", "separate", "border-spacing", "2px");
        addDefaults("tr", "display", "table-row");
        addDefaults("td", "display", "table-cell", "padding", "1px");
        addDefaults("th", "display", "table-cell", "padding", "1px", "font-weight", "bold", "text-align", "center");
        addDefaults("thead", "display", "table-header-group");
        addDefaults("tbody", "display", "table-row-group");
        addDefaults("tfoot", "display", "table-footer-group");

        // Head elements (not displayed)
        addDefaults("head", "display", "none");
        addDefaults("title", "display", "none");
        addDefaults("meta", "display", "none");
        addDefaults("link", "display", "none");
        addDefaults("style", "display", "none");
        addDefaults("script", "display", "none");
    }

    private static void addDefaults(String tagName, String... properties) {
        Map<String, String> styles = new HashMap<>();
        for (int i = 0; i < properties.length; i += 2) {
            styles.put(properties[i], properties[i + 1]);
        }
        DEFAULT_STYLES.put(tagName, styles);
    }

    public static Map<String, String> getDefaultsFor(String tagName) {
        return DEFAULT_STYLES.getOrDefault(tagName, new HashMap<>());
    }

    public static Map<String, String> getGlobalDefaults() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("color", "black");
        defaults.put("background-color", "transparent");
        defaults.put("font-size", "16px");
        defaults.put("font-family", "serif");
        defaults.put("font-weight", "normal");
        defaults.put("font-style", "normal");
        defaults.put("text-decoration", "none");
        defaults.put("text-align", "left");
        defaults.put("display", "inline");
        return defaults;
    }

    public static boolean isInheritable(String property) {
        return switch (property) {
            case "color",
                 "font-family",
                 "font-size",
                 "font-weight",
                 "font-style",
                 "line-height",
                 "text-align",
                 "text-decoration",
                 "text-transform",
                 "letter-spacing",
                 "word-spacing",
                 "white-space",
                 "list-style-type" -> true;
            default -> false;
        };
    }
}

package com.tinybrowser;

import com.tinybrowser.css.CssParser;
import com.tinybrowser.css.StyleSheet;
import com.tinybrowser.dom.Document;
import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;
import com.tinybrowser.parser.HtmlParser;
import com.tinybrowser.style.StyleEngine;
import com.tinybrowser.style.StyledNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StyleEngineTest {

    @Test
    void testBasicMatching() {
        // Create DOM: <div>text</div>
        Element div = new Element("div");
        TextNode text = new TextNode("text");
        div.appendChild(text);

        // Create CSS: div { color: red; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("div { color: red; }");

        // Compute styles
        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        assertNotNull(styledDiv);
        assertEquals("red", styledDiv.getStyle("color"));
    }

    @Test
    void testSpecificityIdBeatsClass() {
        // Create element with both class and id
        Element div = new Element("div");
        div.setAttribute("class", "container");
        div.setAttribute("id", "main");

        // CSS: .container { color: blue; } #main { color: red; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse(".container { color: blue; } #main { color: red; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        // ID selector has higher specificity
        assertEquals("red", styledDiv.getStyle("color"));
    }

    @Test
    void testSpecificityClassBeatsTag() {
        Element div = new Element("div");
        div.setAttribute("class", "special");

        // CSS: div { color: blue; } .special { color: green; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("div { color: blue; } .special { color: green; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        // Class selector has higher specificity
        assertEquals("green", styledDiv.getStyle("color"));
    }

    @Test
    void testMultipleRulesMerge() {
        Element div = new Element("div");

        // CSS: div { color: red; } div { margin: 10px; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("div { color: red; } div { margin: 10px; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        // Both rules should apply
        assertEquals("red", styledDiv.getStyle("color"));
        assertEquals("10px", styledDiv.getStyle("margin"));
    }

    @Test
    void testInheritance() {
        // Create DOM: <div><p>text</p></div>
        Element div = new Element("div");
        Element p = new Element("p");
        TextNode text = new TextNode("text");
        div.appendChild(p);
        p.appendChild(text);

        // CSS: div { color: blue; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("div { color: blue; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        // P should inherit color from div
        assertEquals(1, styledDiv.getChildren().size());
        StyledNode styledP = styledDiv.getChildren().get(0);
        assertEquals("blue", styledP.getStyle("color"));

        // Text node should inherit color from p
        assertEquals(1, styledP.getChildren().size());
        StyledNode styledText = styledP.getChildren().get(0);
        assertEquals("blue", styledText.getStyle("color"));
    }

    @Test
    void testNonInheritableProperties() {
        // Create DOM: <div><p>text</p></div>
        Element div = new Element("div");
        Element p = new Element("p");
        div.appendChild(p);

        // CSS: div { margin: 10px; background-color: yellow; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("div { margin: 10px; background-color: yellow; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        assertEquals("10px", styledDiv.getStyle("margin"));
        assertEquals("yellow", styledDiv.getStyle("background-color"));

        // P should NOT inherit margin or background-color
        StyledNode styledP = styledDiv.getChildren().get(0);
        assertNotEquals("10px", styledP.getStyle("margin"));
        assertNotEquals("yellow", styledP.getStyle("background-color"));
    }

    @Test
    void testDefaultStyles() {
        Element h1 = new Element("h1");

        StyleEngine engine = new StyleEngine();
        StyledNode styledH1 = engine.computeStyles(h1, new StyleSheet());

        // H1 should have default styles
        assertEquals("block", styledH1.getStyle("display"));
        assertEquals("2em", styledH1.getStyle("font-size"));
        assertEquals("bold", styledH1.getStyle("font-weight"));
    }

    @Test
    void testCssOverridesDefaults() {
        Element h1 = new Element("h1");

        // CSS: h1 { font-size: 30px; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("h1 { font-size: 30px; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledH1 = engine.computeStyles(h1, sheet);

        // CSS should override default
        assertEquals("30px", styledH1.getStyle("font-size"));
        // But other defaults should remain
        assertEquals("bold", styledH1.getStyle("font-weight"));
    }

    @Test
    void testComplexDocument() {
        // Parse HTML
        String html = "<html><body><div class=\"container\"><p id=\"intro\">Hello</p></div></body></html>";
        HtmlParser htmlParser = new HtmlParser();
        Document doc = htmlParser.parseDocument(html);

        // Parse CSS
        String css = "body { color: #333; } .container { padding: 20px; } #intro { font-size: 18px; }";
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse(css);

        // Compute styles
        StyleEngine engine = new StyleEngine();
        StyledNode styledRoot = engine.computeStyles(doc, sheet);

        assertNotNull(styledRoot);

        // Find body element (could be first or second child depending on if head exists)
        StyledNode body = null;
        for (StyledNode child : styledRoot.getChildren()) {
            if (child.getNode() instanceof Element) {
                Element elem = (Element) child.getNode();
                if ("body".equals(elem.getTagName())) {
                    body = child;
                    break;
                }
            }
        }

        assertNotNull(body);
        assertEquals("#333", body.getStyle("color"));

        // Navigate to div.container
        StyledNode container = body.getChildren().get(0);
        assertEquals("20px", container.getStyle("padding"));
        assertEquals("#333", container.getStyle("color")); // inherited

        // Navigate to p#intro
        StyledNode intro = container.getChildren().get(0);
        assertEquals("18px", intro.getStyle("font-size"));
        assertEquals("#333", intro.getStyle("color")); // inherited
    }

    @Test
    void testMultipleClasses() {
        Element div = new Element("div");
        div.setAttribute("class", "box highlighted");

        // CSS: .box { padding: 10px; } .highlighted { background: yellow; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse(".box { padding: 10px; } .highlighted { background: yellow; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, sheet);

        // Both classes should match
        assertEquals("10px", styledDiv.getStyle("padding"));
        assertEquals("yellow", styledDiv.getStyle("background"));
    }

    @Test
    void testGroupedSelectors() {
        Element h1 = new Element("h1");
        Element h2 = new Element("h2");

        // CSS: h1, h2 { color: navy; }
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("h1, h2 { color: navy; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledH1 = engine.computeStyles(h1, sheet);
        StyledNode styledH2 = engine.computeStyles(h2, sheet);

        assertEquals("navy", styledH1.getStyle("color"));
        assertEquals("navy", styledH2.getStyle("color"));
    }

    @Test
    void testNoMatchingRules() {
        Element span = new Element("span");

        // CSS only for div
        CssParser cssParser = new CssParser();
        StyleSheet sheet = cssParser.parse("div { color: red; }");

        StyleEngine engine = new StyleEngine();
        StyledNode styledSpan = engine.computeStyles(span, sheet);

        // Should have defaults but not the div rule
        assertNotEquals("red", styledSpan.getStyle("color"));
        assertEquals("black", styledSpan.getStyle("color")); // global default
    }

    @Test
    void testEmptyStyleSheet() {
        Element div = new Element("div");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, new StyleSheet());

        // Should have defaults
        assertNotNull(styledDiv);
        assertEquals("block", styledDiv.getStyle("display"));
        assertEquals("black", styledDiv.getStyle("color"));
    }

    @Test
    void testGetStyleOrDefault() {
        Element div = new Element("div");

        StyleEngine engine = new StyleEngine();
        StyledNode styledDiv = engine.computeStyles(div, new StyleSheet());

        assertEquals("block", styledDiv.getStyleOrDefault("display", "inline"));
        assertEquals("default-value", styledDiv.getStyleOrDefault("non-existent-property", "default-value"));
    }
}

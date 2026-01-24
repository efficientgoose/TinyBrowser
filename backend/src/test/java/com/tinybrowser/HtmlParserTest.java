package com.tinybrowser;

import com.tinybrowser.dom.Document;
import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;
import com.tinybrowser.parser.HtmlParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlParserTest {

    @Test
    void testSingleElement() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<html></html>");

        assertNotNull(root);
        assertTrue(root instanceof Element);
        Element element = (Element) root;
        assertEquals("html", element.getTagName());
        assertFalse(element.hasChildren());
    }

    @Test
    void testNestedElements() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<html><body><p>Hi</p></body></html>");

        assertTrue(root instanceof Element);
        Element html = (Element) root;
        assertEquals("html", html.getTagName());

        assertEquals(1, html.getChildren().size());
        Node bodyNode = html.getChildren().get(0);
        assertTrue(bodyNode instanceof Element);
        Element body = (Element) bodyNode;
        assertEquals("body", body.getTagName());

        assertEquals(1, body.getChildren().size());
        Node pNode = body.getChildren().get(0);
        assertTrue(pNode instanceof Element);
        Element p = (Element) pNode;
        assertEquals("p", p.getTagName());

        assertEquals(1, p.getChildren().size());
        Node textNode = p.getChildren().get(0);
        assertTrue(textNode instanceof TextNode);
        assertEquals("Hi", ((TextNode) textNode).getText());
    }

    @Test
    void testMultipleChildren() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div><p>One</p><p>Two</p></div>");

        assertTrue(root instanceof Element);
        Element div = (Element) root;
        assertEquals("div", div.getTagName());

        assertEquals(2, div.getChildren().size());

        Node p1 = div.getChildren().get(0);
        assertTrue(p1 instanceof Element);
        assertEquals("p", ((Element) p1).getTagName());
        assertEquals("One", ((TextNode) p1.getChildren().get(0)).getText());

        Node p2 = div.getChildren().get(1);
        assertTrue(p2 instanceof Element);
        assertEquals("p", ((Element) p2).getTagName());
        assertEquals("Two", ((TextNode) p2.getChildren().get(0)).getText());
    }

    @Test
    void testMixedContent() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<p>Hello <strong>world</strong>!</p>");

        assertTrue(root instanceof Element);
        Element p = (Element) root;
        assertEquals("p", p.getTagName());

        assertEquals(3, p.getChildren().size());

        Node text1 = p.getChildren().get(0);
        assertTrue(text1 instanceof TextNode);
        assertEquals("Hello ", ((TextNode) text1).getText());

        Node strong = p.getChildren().get(1);
        assertTrue(strong instanceof Element);
        assertEquals("strong", ((Element) strong).getTagName());
        assertEquals("world", ((TextNode) strong.getChildren().get(0)).getText());

        Node text2 = p.getChildren().get(2);
        assertTrue(text2 instanceof TextNode);
        assertEquals("!", ((TextNode) text2).getText());
    }

    @Test
    void testAttributesPreserved() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div id=\"main\" class=\"container\"></div>");

        assertTrue(root instanceof Element);
        Element div = (Element) root;
        assertEquals("div", div.getTagName());
        assertEquals("main", div.getAttribute("id"));
        assertEquals("container", div.getAttribute("class"));
    }

    @Test
    void testSelfClosingTag() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div><br/><img src=\"test.png\"/></div>");

        assertTrue(root instanceof Element);
        Element div = (Element) root;
        assertEquals(2, div.getChildren().size());

        Node br = div.getChildren().get(0);
        assertTrue(br instanceof Element);
        assertEquals("br", ((Element) br).getTagName());
        assertFalse(br.hasChildren());

        Node img = div.getChildren().get(1);
        assertTrue(img instanceof Element);
        Element imgElement = (Element) img;
        assertEquals("img", imgElement.getTagName());
        assertEquals("test.png", imgElement.getAttribute("src"));
    }

    @Test
    void testGetTextContent() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div><p>Hello</p><p>World</p></div>");

        String textContent = root.getTextContent();
        assertEquals("HelloWorld", textContent);
    }

    @Test
    void testComplexDocument() {
        String html = "<html><head><title>Test Page</title></head><body><h1>Welcome</h1><p>Content here</p></body></html>";
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse(html);

        assertTrue(root instanceof Element);
        Element htmlElement = (Element) root;
        assertEquals("html", htmlElement.getTagName());

        assertEquals(2, htmlElement.getChildren().size());

        Element head = (Element) htmlElement.getChildren().get(0);
        assertEquals("head", head.getTagName());
        Element title = (Element) head.getChildren().get(0);
        assertEquals("title", title.getTagName());
        assertEquals("Test Page", ((TextNode) title.getChildren().get(0)).getText());

        Element body = (Element) htmlElement.getChildren().get(1);
        assertEquals("body", body.getTagName());
        assertEquals(2, body.getChildren().size());
    }

    @Test
    void testDocumentClass() {
        HtmlParser parser = new HtmlParser();
        Document doc = parser.parseDocument("<html><body><p id=\"para1\">Text</p><div class=\"box\"></div></body></html>");

        assertNotNull(doc);
        assertNotNull(doc.getRootElement());
        assertEquals("html", doc.getRootElement().getTagName());

        // Test getElementById
        Element para = doc.getElementById("para1");
        assertNotNull(para);
        assertEquals("p", para.getTagName());
        assertEquals("Text", para.getTextContent());

        // Test getElementById with non-existent id
        Element nonExistent = doc.getElementById("does-not-exist");
        assertNull(nonExistent);

        // Test getElementsByTagName
        List<Element> divs = doc.getElementsByTagName("div");
        assertEquals(1, divs.size());
        assertEquals("div", divs.get(0).getTagName());

        List<Element> paragraphs = doc.getElementsByTagName("p");
        assertEquals(1, paragraphs.size());
        assertEquals("para1", paragraphs.get(0).getAttribute("id"));
    }

    @Test
    void testDoctype() {
        HtmlParser parser = new HtmlParser();
        Document doc = parser.parseDocument("<!DOCTYPE html><html><body></body></html>");

        assertNotNull(doc.getDoctype());
        assertTrue(doc.getDoctype().contains("DOCTYPE"));
    }

    @Test
    void testEmptyInput() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("");

        assertNotNull(root);
        assertTrue(root instanceof Element);
        assertEquals("html", ((Element) root).getTagName());
    }

    @Test
    void testWhitespaceHandling() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div>  <p>Text</p>  </div>");

        assertTrue(root instanceof Element);
        Element div = (Element) root;

        // Whitespace-only text nodes are skipped by tokenizer
        assertEquals(1, div.getChildren().size());
        assertTrue(div.getChildren().get(0) instanceof Element);
    }

    @Test
    void testNestedSameTagNames() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div><div><div>Nested</div></div></div>");

        assertTrue(root instanceof Element);
        Element div1 = (Element) root;
        assertEquals("div", div1.getTagName());

        Element div2 = (Element) div1.getChildren().get(0);
        assertEquals("div", div2.getTagName());

        Element div3 = (Element) div2.getChildren().get(0);
        assertEquals("div", div3.getTagName());

        assertEquals("Nested", div3.getTextContent());
    }

    @Test
    void testMultipleRootLevelElements() {
        // In HTML, only one root element should exist, but let's test robustness
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div>First</div><div>Second</div>");

        // Parser should return the first element as root
        assertTrue(root instanceof Element);
        Element div = (Element) root;
        assertEquals("div", div.getTagName());
        assertEquals("First", div.getTextContent());
    }

    @Test
    void testToStringMethod() {
        HtmlParser parser = new HtmlParser();
        Node root = parser.parse("<div id=\"test\"><p>Hello</p></div>");

        String treeString = root.toString();
        assertNotNull(treeString);
        assertFalse(treeString.isEmpty());

        // The tree string should contain the tag names and text content
        assertTrue(treeString.toLowerCase().contains("div"), "Should contain 'div', got: " + treeString);
        assertTrue(treeString.toLowerCase().contains("p"), "Should contain 'p', got: " + treeString);
        assertTrue(treeString.contains("Hello"), "Should contain 'Hello', got: " + treeString);
    }
}

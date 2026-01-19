package com.tinybrowser;

import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;
import com.tinybrowser.parser.HtmlParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TinyBrowserTest {

    @Test
    void testElementCreation() {
        Element element = new Element("div");
        assertEquals("div", element.getTagName());
        assertFalse(element.hasChildren());
    }

    @Test
    void testElementAttributes() {
        Element element = new Element("div");
        element.setAttribute("class", "container");
        element.setAttribute("id", "main");

        assertTrue(element.hasAttribute("class"));
        assertEquals("container", element.getAttribute("class"));
        assertTrue(element.hasAttribute("id"));
        assertEquals("main", element.getAttribute("id"));
        assertFalse(element.hasAttribute("style"));
    }

    @Test
    void testTextNodeCreation() {
        TextNode textNode = new TextNode("Hello, World!");
        assertEquals("Hello, World!", textNode.getText());
    }

    @Test
    void testNodeHierarchy() {
        Element parent = new Element("div");
        Element child = new Element("p");
        TextNode text = new TextNode("Hello");

        parent.appendChild(child);
        child.appendChild(text);

        assertTrue(parent.hasChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals(child, parent.getChildren().get(0));
        assertEquals(1, child.getChildren().size());
        assertEquals(text, child.getChildren().get(0));
    }

    @Test
    void testRemoveChild() {
        Element parent = new Element("div");
        Element child = new Element("p");

        parent.appendChild(child);
        assertTrue(parent.hasChildren());

        parent.removeChild(child);
        assertFalse(parent.hasChildren());
        assertEquals(0, parent.getChildren().size());
    }

    @Test
    void testHtmlParserExists() {
        HtmlParser parser = new HtmlParser();
        assertNotNull(parser);

        Node node = parser.parse("<html><body>Test</body></html>");
        assertNotNull(node);
    }
}

package com.tinybrowser.parser;

import com.tinybrowser.dom.Document;
import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;

import java.util.Stack;

public class HtmlParser {
    private String doctype;

    public HtmlParser() {
        this.doctype = "";
    }

    public Node parse(String html) {
        Document document = parseDocument(html);
        return document.getRootElement();
    }

    public Document parseDocument(String html) {
        if (html == null || html.isBlank()) {
            Document doc = new Document(new Element("html"));
            return doc;
        }

        HtmlTokenizer tokenizer = new HtmlTokenizer(html);
        Stack<Element> elementStack = new Stack<>();
        Element root = null;
        Element currentParent = null;

        while (true) {
            Token token = tokenizer.nextToken();

            if (token.getType() == TokenType.EOF) {
                break;
            }

            switch (token.getType()) {
                case DOCTYPE:
                    doctype = token.getContent();
                    break;

                case START_TAG:
                    Element element = new Element(token.getTagName());
                    for (var entry : token.getAttributes().entrySet()) {
                        element.setAttribute(entry.getKey(), entry.getValue());
                    }

                    if (currentParent != null) {
                        currentParent.appendChild(element);
                    } else if (root == null) {
                        root = element;
                    }

                    elementStack.push(element);
                    currentParent = element;
                    break;

                case END_TAG:
                    if (!elementStack.isEmpty()) {
                        Element closingElement = elementStack.pop();

                        // Validate tag name matches (optional - for robustness)
                        if (!closingElement.getTagName().equals(token.getTagName())) {
                            // Tag mismatch - for now we'll just ignore it
                            // In a real browser, this would trigger error recovery
                        }

                        currentParent = elementStack.isEmpty() ? null : elementStack.peek();
                    }
                    break;

                case SELF_CLOSING_TAG:
                    Element selfClosing = new Element(token.getTagName());
                    for (var entry : token.getAttributes().entrySet()) {
                        selfClosing.setAttribute(entry.getKey(), entry.getValue());
                    }

                    if (currentParent != null) {
                        currentParent.appendChild(selfClosing);
                    } else if (root == null) {
                        root = selfClosing;
                    }
                    break;

                case TEXT:
                    TextNode textNode = new TextNode(token.getContent());
                    if (currentParent != null) {
                        currentParent.appendChild(textNode);
                    }
                    break;

                case COMMENT:
                    // Skip comments for now
                    break;
            }
        }

        // If no root was created, create a default one
        if (root == null) {
            root = new Element("html");
        }

        Document document = new Document(root);
        document.setDoctype(doctype);
        return document;
    }

    public String getDoctype() {
        return doctype;
    }
}

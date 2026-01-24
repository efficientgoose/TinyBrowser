package com.tinybrowser.dom;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private Element rootElement;
    private String doctype;

    public Document() {
        this.doctype = "";
    }

    public Document(Element rootElement) {
        this.rootElement = rootElement;
        this.doctype = "";
    }

    public Element getRootElement() {
        return rootElement;
    }

    public void setRootElement(Element rootElement) {
        this.rootElement = rootElement;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public List<Element> getElementsByTagName(String tagName) {
        List<Element> result = new ArrayList<>();
        if (rootElement != null) {
            collectElementsByTagName(rootElement, tagName.toLowerCase(), result);
        }
        return result;
    }

    private void collectElementsByTagName(Node node, String tagName, List<Element> result) {
        if (node instanceof Element) {
            Element element = (Element) node;
            if (element.getTagName().equals(tagName)) {
                result.add(element);
            }
        }

        for (Node child : node.getChildren()) {
            collectElementsByTagName(child, tagName, result);
        }
    }

    public Element getElementById(String id) {
        if (rootElement == null) {
            return null;
        }
        return findElementById(rootElement, id);
    }

    private Element findElementById(Node node, String id) {
        if (node instanceof Element) {
            Element element = (Element) node;
            String elementId = element.getAttribute("id");
            if (id.equals(elementId)) {
                return element;
            }
        }

        for (Node child : node.getChildren()) {
            Element result = findElementById(child, id);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!doctype.isEmpty()) {
            sb.append("<!DOCTYPE ").append(doctype).append(">\n");
        }
        if (rootElement != null) {
            sb.append(rootElement.toString());
        }
        return sb.toString();
    }
}

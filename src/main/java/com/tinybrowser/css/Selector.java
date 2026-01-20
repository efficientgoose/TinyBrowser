package com.tinybrowser.css;

import com.tinybrowser.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class Selector {
    private String tagName;  // null means any tag
    private String id;       // null means no id requirement
    private List<String> classes;

    public Selector() {
        this.classes = new ArrayList<>();
    }

    public Selector(String selectorString) {
        this();
        parseSelectorString(selectorString);
    }

    private void parseSelectorString(String selector) {
        if (selector == null || selector.isEmpty()) {
            return;
        }

        selector = selector.trim();

        // Universal selector
        if (selector.equals("*")) {
            return;
        }

        int pos = 0;
        StringBuilder tagBuilder = new StringBuilder();

        while (pos < selector.length()) {
            char ch = selector.charAt(pos);

            if (ch == '#') {
                // ID selector
                pos++;
                StringBuilder idBuilder = new StringBuilder();
                while (pos < selector.length()) {
                    char idChar = selector.charAt(pos);
                    if (idChar == '.' || idChar == '#') {
                        break;
                    }
                    idBuilder.append(idChar);
                    pos++;
                }
                this.id = idBuilder.toString();
            } else if (ch == '.') {
                // Class selector
                pos++;
                StringBuilder classBuilder = new StringBuilder();
                while (pos < selector.length()) {
                    char classChar = selector.charAt(pos);
                    if (classChar == '.' || classChar == '#') {
                        break;
                    }
                    classBuilder.append(classChar);
                    pos++;
                }
                this.classes.add(classBuilder.toString());
            } else {
                // Tag name
                tagBuilder.append(ch);
                pos++;
            }
        }

        String tag = tagBuilder.toString().trim();
        if (!tag.isEmpty()) {
            this.tagName = tag;
        }
    }

    public boolean matches(Element element) {
        if (element == null) {
            return false;
        }

        // Check tag name
        if (tagName != null && !tagName.equals(element.getTagName())) {
            return false;
        }

        // Check ID
        if (id != null) {
            String elementId = element.getAttribute("id");
            if (!id.equals(elementId)) {
                return false;
            }
        }

        // Check classes
        if (!classes.isEmpty()) {
            String classAttr = element.getAttribute("class");
            if (classAttr == null || classAttr.isEmpty()) {
                return false;
            }

            String[] elementClasses = classAttr.split("\\s+");
            for (String requiredClass : classes) {
                boolean found = false;
                for (String elementClass : elementClasses) {
                    if (requiredClass.equals(elementClass)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }

        return true;
    }

    public int[] getSpecificity() {
        int idCount = id != null ? 1 : 0;
        int classCount = classes.size();
        int tagCount = tagName != null ? 1 : 0;
        return new int[]{idCount, classCount, tagCount};
    }

    public int compareSpecificity(Selector other) {
        int[] thisSpec = this.getSpecificity();
        int[] otherSpec = other.getSpecificity();

        // Compare ID count
        if (thisSpec[0] != otherSpec[0]) {
            return Integer.compare(thisSpec[0], otherSpec[0]);
        }

        // Compare class count
        if (thisSpec[1] != otherSpec[1]) {
            return Integer.compare(thisSpec[1], otherSpec[1]);
        }

        // Compare tag count
        return Integer.compare(thisSpec[2], otherSpec[2]);
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void addClass(String className) {
        this.classes.add(className);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (tagName != null) {
            sb.append(tagName);
        } else if (classes.isEmpty() && id == null) {
            sb.append("*");
        }

        if (id != null) {
            sb.append("#").append(id);
        }

        for (String className : classes) {
            sb.append(".").append(className);
        }

        return sb.toString();
    }
}

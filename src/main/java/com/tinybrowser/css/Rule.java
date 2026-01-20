package com.tinybrowser.css;

import com.tinybrowser.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private List<Selector> selectors;
    private List<Declaration> declarations;

    public Rule() {
        this.selectors = new ArrayList<>();
        this.declarations = new ArrayList<>();
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public void addSelector(Selector selector) {
        this.selectors.add(selector);
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void addDeclaration(Declaration declaration) {
        this.declarations.add(declaration);
    }

    public boolean matches(Element element) {
        for (Selector selector : selectors) {
            if (selector.matches(element)) {
                return true;
            }
        }
        return false;
    }

    public Selector getMatchingSelector(Element element) {
        for (Selector selector : selectors) {
            if (selector.matches(element)) {
                return selector;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Selectors
        for (int i = 0; i < selectors.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(selectors.get(i).toString());
        }

        sb.append(" {\n");

        // Declarations
        for (Declaration decl : declarations) {
            sb.append("  ").append(decl.toString()).append(";\n");
        }

        sb.append("}");

        return sb.toString();
    }
}

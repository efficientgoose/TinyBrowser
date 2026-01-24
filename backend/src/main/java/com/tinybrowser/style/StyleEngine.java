package com.tinybrowser.style;

import com.tinybrowser.css.*;
import com.tinybrowser.dom.Document;
import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;

import java.util.*;

public class StyleEngine {

    public StyledNode computeStyles(Document document, StyleSheet styleSheet) {
        if (document == null || document.getRootElement() == null) {
            return null;
        }

        return computeStylesForNode(document.getRootElement(), styleSheet, null);
    }

    public StyledNode computeStyles(Node rootNode, StyleSheet styleSheet) {
        if (rootNode == null) {
            return null;
        }

        return computeStylesForNode(rootNode, styleSheet, null);
    }

    private StyledNode computeStylesForNode(Node node, StyleSheet styleSheet, StyledNode parent) {
        StyledNode styledNode = new StyledNode(node);

        if (node instanceof Element) {
            Element element = (Element) node;

            // Step 1: Apply global defaults
            Map<String, String> globalDefaults = CssDefaults.getGlobalDefaults();
            for (Map.Entry<String, String> entry : globalDefaults.entrySet()) {
                styledNode.setStyle(entry.getKey(), entry.getValue());
            }

            // Step 2: Apply tag-specific defaults
            Map<String, String> tagDefaults = CssDefaults.getDefaultsFor(element.getTagName());
            for (Map.Entry<String, String> entry : tagDefaults.entrySet()) {
                styledNode.setStyle(entry.getKey(), entry.getValue());
            }

            // Step 3: Inherit inheritable properties from parent
            if (parent != null) {
                for (Map.Entry<String, String> entry : parent.getComputedStyles().entrySet()) {
                    if (CssDefaults.isInheritable(entry.getKey())) {
                        styledNode.setStyle(entry.getKey(), entry.getValue());
                    }
                }
            }

            // Step 4: Apply matching CSS rules (sorted by specificity)
            if (styleSheet != null) {
                List<RuleWithSelector> matchingRules = getMatchingRulesWithSelectors(element, styleSheet);

                // Sort by specificity (lower specificity first, so higher overwrites)
                matchingRules.sort(Comparator.comparingInt(rws -> {
                    int[] spec = rws.selector.getSpecificity();
                    return spec[0] * 100 + spec[1] * 10 + spec[2];
                }));

                // Apply declarations in specificity order
                for (RuleWithSelector rws : matchingRules) {
                    for (Declaration decl : rws.rule.getDeclarations()) {
                        styledNode.setStyle(decl.getProperty(), decl.getValue());
                    }
                }
            }

        } else if (node instanceof TextNode) {
            // Text nodes inherit all styles from parent
            if (parent != null) {
                for (Map.Entry<String, String> entry : parent.getComputedStyles().entrySet()) {
                    styledNode.setStyle(entry.getKey(), entry.getValue());
                }
            }
        }

        // Recursively compute styles for children
        for (Node child : node.getChildren()) {
            StyledNode styledChild = computeStylesForNode(child, styleSheet, styledNode);
            styledNode.appendChild(styledChild);
        }

        return styledNode;
    }

    private List<RuleWithSelector> getMatchingRulesWithSelectors(Element element, StyleSheet styleSheet) {
        List<RuleWithSelector> result = new ArrayList<>();

        for (Rule rule : styleSheet.getRules()) {
            Selector matchingSelector = rule.getMatchingSelector(element);
            if (matchingSelector != null) {
                result.add(new RuleWithSelector(rule, matchingSelector));
            }
        }

        return result;
    }

    private static class RuleWithSelector {
        final Rule rule;
        final Selector selector;

        RuleWithSelector(Rule rule, Selector selector) {
            this.rule = rule;
            this.selector = selector;
        }
    }
}

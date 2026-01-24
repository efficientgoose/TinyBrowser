package com.tinybrowser.css;

import com.tinybrowser.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class StyleSheet {
    private List<Rule> rules;

    public StyleSheet() {
        this.rules = new ArrayList<>();
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public List<Rule> getRulesForElement(Element element) {
        List<Rule> matchingRules = new ArrayList<>();

        for (Rule rule : rules) {
            if (rule.matches(element)) {
                matchingRules.add(rule);
            }
        }

        return matchingRules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            sb.append(rule.toString()).append("\n\n");
        }
        return sb.toString();
    }
}

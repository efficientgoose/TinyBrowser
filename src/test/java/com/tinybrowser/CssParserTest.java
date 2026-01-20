package com.tinybrowser;

import com.tinybrowser.css.*;
import com.tinybrowser.dom.Element;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CssParserTest {

    @Test
    void testSingleRule() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse("div { color: red; }");

        assertNotNull(sheet);
        assertEquals(1, sheet.getRules().size());

        Rule rule = sheet.getRules().get(0);
        assertEquals(1, rule.getSelectors().size());
        assertEquals("div", rule.getSelectors().get(0).toString());

        assertEquals(1, rule.getDeclarations().size());
        Declaration decl = rule.getDeclarations().get(0);
        assertEquals("color", decl.getProperty());
        assertEquals("red", decl.getValue());
    }

    @Test
    void testMultipleDeclarations() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse("div { color: red; margin: 10px; }");

        Rule rule = sheet.getRules().get(0);
        assertEquals(2, rule.getDeclarations().size());

        Declaration decl1 = rule.getDeclarations().get(0);
        assertEquals("color", decl1.getProperty());
        assertEquals("red", decl1.getValue());

        Declaration decl2 = rule.getDeclarations().get(1);
        assertEquals("margin", decl2.getProperty());
        assertEquals("10px", decl2.getValue());
    }

    @Test
    void testClassSelector() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse(".container { width: 100px; }");

        Rule rule = sheet.getRules().get(0);
        Selector selector = rule.getSelectors().get(0);

        assertEquals(".container", selector.toString());
        assertNull(selector.getTagName());
        assertEquals(1, selector.getClasses().size());
        assertEquals("container", selector.getClasses().get(0));

        Declaration decl = rule.getDeclarations().get(0);
        assertEquals("width", decl.getProperty());
        assertEquals("100px", decl.getValue());
    }

    @Test
    void testIdSelector() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse("#main { background: blue; }");

        Rule rule = sheet.getRules().get(0);
        Selector selector = rule.getSelectors().get(0);

        assertEquals("#main", selector.toString());
        assertEquals("main", selector.getId());

        Declaration decl = rule.getDeclarations().get(0);
        assertEquals("background", decl.getProperty());
        assertEquals("blue", decl.getValue());
    }

    @Test
    void testGroupedSelectors() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse("h1, h2 { font-size: 20px; }");

        Rule rule = sheet.getRules().get(0);
        assertEquals(2, rule.getSelectors().size());

        assertEquals("h1", rule.getSelectors().get(0).toString());
        assertEquals("h2", rule.getSelectors().get(1).toString());

        assertEquals(1, rule.getDeclarations().size());
        Declaration decl = rule.getDeclarations().get(0);
        assertEquals("font-size", decl.getProperty());
        assertEquals("20px", decl.getValue());
    }

    @Test
    void testMultipleRules() {
        String css = "div { color: red; } p { margin: 5px; }";
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse(css);

        assertEquals(2, sheet.getRules().size());

        Rule rule1 = sheet.getRules().get(0);
        assertEquals("div", rule1.getSelectors().get(0).toString());
        assertEquals("color", rule1.getDeclarations().get(0).getProperty());

        Rule rule2 = sheet.getRules().get(1);
        assertEquals("p", rule2.getSelectors().get(0).toString());
        assertEquals("margin", rule2.getDeclarations().get(0).getProperty());
    }

    @Test
    void testSelectorMatching() {
        // Test tag selector
        Selector divSelector = new Selector("div");
        Element divElement = new Element("div");
        assertTrue(divSelector.matches(divElement));

        Element pElement = new Element("p");
        assertFalse(divSelector.matches(pElement));

        // Test class selector
        Selector classSelector = new Selector(".container");
        Element elementWithClass = new Element("div");
        elementWithClass.setAttribute("class", "container");
        assertTrue(classSelector.matches(elementWithClass));

        Element elementWithoutClass = new Element("div");
        assertFalse(classSelector.matches(elementWithoutClass));

        // Test ID selector
        Selector idSelector = new Selector("#main");
        Element elementWithId = new Element("div");
        elementWithId.setAttribute("id", "main");
        assertTrue(idSelector.matches(elementWithId));

        Element elementWithWrongId = new Element("div");
        elementWithWrongId.setAttribute("id", "other");
        assertFalse(idSelector.matches(elementWithWrongId));
    }

    @Test
    void testCombinedSelector() {
        Selector combined = new Selector("div.container#main");

        assertEquals("div", combined.getTagName());
        assertEquals("main", combined.getId());
        assertEquals(1, combined.getClasses().size());
        assertEquals("container", combined.getClasses().get(0));

        // Test matching
        Element match = new Element("div");
        match.setAttribute("id", "main");
        match.setAttribute("class", "container");
        assertTrue(combined.matches(match));

        // Wrong tag
        Element wrongTag = new Element("p");
        wrongTag.setAttribute("id", "main");
        wrongTag.setAttribute("class", "container");
        assertFalse(combined.matches(wrongTag));
    }

    @Test
    void testSpecificity() {
        Selector tagSelector = new Selector("div");
        int[] tagSpec = tagSelector.getSpecificity();
        assertEquals(0, tagSpec[0]); // id
        assertEquals(0, tagSpec[1]); // class
        assertEquals(1, tagSpec[2]); // tag

        Selector classSelector = new Selector(".container");
        int[] classSpec = classSelector.getSpecificity();
        assertEquals(0, classSpec[0]);
        assertEquals(1, classSpec[1]);
        assertEquals(0, classSpec[2]);

        Selector idSelector = new Selector("#main");
        int[] idSpec = idSelector.getSpecificity();
        assertEquals(1, idSpec[0]);
        assertEquals(0, idSpec[1]);
        assertEquals(0, idSpec[2]);

        Selector combined = new Selector("div.container#main");
        int[] combinedSpec = combined.getSpecificity();
        assertEquals(1, combinedSpec[0]);
        assertEquals(1, combinedSpec[1]);
        assertEquals(1, combinedSpec[2]);
    }

    @Test
    void testGetRulesForElement() {
        String css = "div { color: red; } .container { width: 100px; } #main { background: blue; }";
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse(css);

        Element element = new Element("div");
        element.setAttribute("class", "container");
        element.setAttribute("id", "main");

        List<Rule> matchingRules = sheet.getRulesForElement(element);
        assertEquals(3, matchingRules.size());
    }

    @Test
    void testEmptyCSS() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse("");

        assertNotNull(sheet);
        assertEquals(0, sheet.getRules().size());
    }

    @Test
    void testCSSWithComments() {
        String css = "/* This is a comment */ div { color: red; }";
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse(css);

        assertEquals(1, sheet.getRules().size());
        assertEquals("div", sheet.getRules().get(0).getSelectors().get(0).toString());
    }

    @Test
    void testMultiWordValue() {
        CssParser parser = new CssParser();
        StyleSheet sheet = parser.parse("body { font-family: Arial, sans-serif; }");

        Rule rule = sheet.getRules().get(0);
        Declaration decl = rule.getDeclarations().get(0);
        assertEquals("font-family", decl.getProperty());
        assertTrue(decl.getValue().contains("Arial"));
    }

    @Test
    void testUniversalSelector() {
        Selector universal = new Selector("*");

        Element div = new Element("div");
        assertTrue(universal.matches(div));

        Element p = new Element("p");
        assertTrue(universal.matches(p));
    }

    @Test
    void testMultipleClasses() {
        Selector selector = new Selector(".class1.class2");
        assertEquals(2, selector.getClasses().size());
        assertTrue(selector.getClasses().contains("class1"));
        assertTrue(selector.getClasses().contains("class2"));

        Element element = new Element("div");
        element.setAttribute("class", "class1 class2");
        assertTrue(selector.matches(element));

        Element partialMatch = new Element("div");
        partialMatch.setAttribute("class", "class1");
        assertFalse(selector.matches(partialMatch));
    }
}

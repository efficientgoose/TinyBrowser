package com.tinybrowser;

import com.tinybrowser.parser.HtmlTokenizer;
import com.tinybrowser.parser.Token;
import com.tinybrowser.parser.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlTokenizerTest {

    @Test
    void testSimpleTag() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<div></div>");

        Token token1 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token1.getType());
        assertEquals("div", token1.getTagName());

        Token token2 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token2.getType());
        assertEquals("div", token2.getTagName());

        Token token3 = tokenizer.nextToken();
        assertEquals(TokenType.EOF, token3.getType());
    }

    @Test
    void testTagWithAttributes() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<div class=\"test\" id=\"main\">");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token.getType());
        assertEquals("div", token.getTagName());
        assertEquals("test", token.getAttribute("class"));
        assertEquals("main", token.getAttribute("id"));
    }

    @Test
    void testTagWithSingleQuotedAttributes() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<div class='test' id='main'>");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token.getType());
        assertEquals("div", token.getTagName());
        assertEquals("test", token.getAttribute("class"));
        assertEquals("main", token.getAttribute("id"));
    }

    @Test
    void testNestedContent() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<p>Hello World</p>");

        Token token1 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token1.getType());
        assertEquals("p", token1.getTagName());

        Token token2 = tokenizer.nextToken();
        assertEquals(TokenType.TEXT, token2.getType());
        assertEquals("Hello World", token2.getContent());

        Token token3 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token3.getType());
        assertEquals("p", token3.getTagName());

        Token token4 = tokenizer.nextToken();
        assertEquals(TokenType.EOF, token4.getType());
    }

    @Test
    void testSelfClosingTag() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<br/>");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.SELF_CLOSING_TAG, token.getType());
        assertEquals("br", token.getTagName());

        Token eof = tokenizer.nextToken();
        assertEquals(TokenType.EOF, eof.getType());
    }

    @Test
    void testSelfClosingTagWithAttributes() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<img src=\"image.png\" alt=\"test\"/>");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.SELF_CLOSING_TAG, token.getType());
        assertEquals("img", token.getTagName());
        assertEquals("image.png", token.getAttribute("src"));
        assertEquals("test", token.getAttribute("alt"));
    }

    @Test
    void testMixedContent() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<div><p>text</p></div>");

        Token token1 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token1.getType());
        assertEquals("div", token1.getTagName());

        Token token2 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token2.getType());
        assertEquals("p", token2.getTagName());

        Token token3 = tokenizer.nextToken();
        assertEquals(TokenType.TEXT, token3.getType());
        assertEquals("text", token3.getContent());

        Token token4 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token4.getType());
        assertEquals("p", token4.getTagName());

        Token token5 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token5.getType());
        assertEquals("div", token5.getTagName());

        Token token6 = tokenizer.nextToken();
        assertEquals(TokenType.EOF, token6.getType());
    }

    @Test
    void testComment() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<!-- This is a comment -->");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.COMMENT, token.getType());
        assertEquals(" This is a comment ", token.getContent());
    }

    @Test
    void testDoctype() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<!DOCTYPE html>");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.DOCTYPE, token.getType());
        assertTrue(token.getContent().contains("DOCTYPE"));
    }

    @Test
    void testComplexHtml() {
        String html = "<html><head><title>Test</title></head><body><h1>Hello</h1></body></html>";
        HtmlTokenizer tokenizer = new HtmlTokenizer(html);

        // <html>
        Token token1 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token1.getType());
        assertEquals("html", token1.getTagName());

        // <head>
        Token token2 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token2.getType());
        assertEquals("head", token2.getTagName());

        // <title>
        Token token3 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token3.getType());
        assertEquals("title", token3.getTagName());

        // Test
        Token token4 = tokenizer.nextToken();
        assertEquals(TokenType.TEXT, token4.getType());
        assertEquals("Test", token4.getContent());

        // </title>
        Token token5 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token5.getType());
        assertEquals("title", token5.getTagName());

        // Continue with rest of structure...
    }

    @Test
    void testWhitespaceHandling() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<div>  \n  </div>", true);

        Token token1 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token1.getType());

        Token token2 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token2.getType());
    }

    @Test
    void testAttributesWithoutQuotes() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<div class=test>");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token.getType());
        assertEquals("test", token.getAttribute("class"));
    }

    @Test
    void testMultipleAttributes() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<input type=\"text\" name=\"username\" value=\"john\" required>");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token.getType());
        assertEquals("input", token.getTagName());
        assertEquals("text", token.getAttribute("type"));
        assertEquals("username", token.getAttribute("name"));
        assertEquals("john", token.getAttribute("value"));
        assertEquals("", token.getAttribute("required"));
    }

    @Test
    void testEmptyInput() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.EOF, token.getType());
    }

    @Test
    void testTextOnly() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("Just plain text");

        Token token = tokenizer.nextToken();
        assertEquals(TokenType.TEXT, token.getType());
        assertEquals("Just plain text", token.getContent());
    }

    @Test
    void testCaseInsensitiveTags() {
        HtmlTokenizer tokenizer = new HtmlTokenizer("<DIV CLASS=\"Test\"></DIV>");

        Token token1 = tokenizer.nextToken();
        assertEquals(TokenType.START_TAG, token1.getType());
        assertEquals("div", token1.getTagName());
        assertEquals("Test", token1.getAttribute("class"));

        Token token2 = tokenizer.nextToken();
        assertEquals(TokenType.END_TAG, token2.getType());
        assertEquals("div", token2.getTagName());
    }
}

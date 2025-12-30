package com.craftinginterpreters.lox;

import static com.craftinginterpreters.lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Scans the texts and generates tokens for the text. 
 * String of text == Tokens in the Grammar. 
 * Parser will get these tokens and generates the meaning ful. 
 */
public class Scanner {

    /**
     * Set of keywords
     */
    private static final Map<String, TokenType> keywords;

    /**
     * Statically assign them. 
     * TODO: Can we move this along with tokentype to ensure that checking of tokens is easier? 
     */
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    // Source string. 
    private final String source;
    // Generated tokens. 
    private final List<Token> tokens = new ArrayList<>();

    /** 
     * Variables for state maintance during scanning. 
     * Start pointer 
     * Current pointer 
     * Number of lines
     */
    private int start = 0;
    private int current = 0;
    private int line = 1;

    /**
     * Constructor with the string
     */
    Scanner(String source) {
        this.source = source;
    }

    /**
     * Main driver. 
     * While the source does not end, 
     * scan for each token, 
     * At the end EOF -- Special token 
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    /**
     * State maintenance to see if the characters in source string has ended. 
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Core engine for scanning. Goes through each character and based on that, creates token 
     * For multi-character it peeks ahead to get that token 
     * Comments are handled by checking and ignoring until end. 
     * on default, it can be either 
     *  Number -> Scanned set of digits 
     *  identifier -> Already declared or a token known. 
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // a comment goes until the end of the line so ignore all
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // ignore the white
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
        }
    }

    /**
     * Identifier -> 
     *   Find if the subset string is a Token 
     *   If not, then it is identifier and add that as in. 
     */
    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }

    /** 
     * Simple check for alphabets. 
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    /**
     * For alphanumeric
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Numeric checks
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Number -> 
     *   Integer -> Only digits 
     *   Floating point -> with a decimal 
     */
    private void number() {
        while (isDigit(peek()))
            advance();

        // Look for the fractional part, ensure that there is digits after '.' 
        if (peek() == '.' && isDigit(peekNext())) {
            // consume the "."
            advance();

            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Peeks Next to current pointer and gets a character, do not advance "current" pointer
     */
    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt((current + 1));
    }

    /** 
     * Quoted strings. Consumes anything between quotes '"'
    */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated String");
        }
        // the closing "
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * Matches with "advance" -> Moves the pointer while consuming the character and matching with expected. 
     * This is used to handle multi-character tokens like !=
     */
    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;
        current++;
        return true;
    }

    /**
     * Peeks the current pointer without "advance"
     */
    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    /**
     * Moves the current pointer, "advance" and returns the character at the current pointer. 
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * If identified as Token, then add it. Without any literal Object
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds token with the value assigned, like NUMBER (actual Number). STRING (actual string)
     * For known tokens the literal will be null
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}

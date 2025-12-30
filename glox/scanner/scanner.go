package scanner

import (
	"fmt"

	"gsmayya.com/glox/utils"
)

var start int = 0
var current int = 0
var line int = 1

func setValues() {
	start = 0
	current = 0
	line = 1
}

func ScanTokens(source string) []Token {
	// start of new scanning, so set values
	setValues()
	fmt.Println("Scanning : " + source)
	var tokens []Token
	for !isAtEnd(source) {
		start = current
		scanToken(source, &tokens)
	}
	tokens = append(tokens, Token{Type: EOF, Lexeme: "", Literal: nil, Line: line})
	return tokens
}

func scanToken(source string, tokens *[]Token) {
	var c byte = advance(source)
	switch c {
	case '(':
		addToken(source, tokens, LEFT_PAREN)
	case ')':
		addToken(source, tokens, RIGHT_PAREN)
	case '{':
		addToken(source, tokens, LEFT_BRACE)
	case '}':
		addToken(source, tokens, RIGHT_BRACE)
	case ',':
		addToken(source, tokens, COMMA)
	case '.':
		addToken(source, tokens, DOT)
	case '-':
		addToken(source, tokens, MINUS)
	case '+':
		addToken(source, tokens, PLUS)
	case ';':
		addToken(source, tokens, SEMICOLON)
	case '*':
		addToken(source, tokens, STAR)
	case '!':
		if match(source, '=') {
			addToken(source, tokens, BANG_EQUAL)
		} else {
			addToken(source, tokens, BANG)
		}
	case '=':
		if match(source, '=') {
			addToken(source, tokens, EQUAL_EQUAL)
		} else {
			addToken(source, tokens, EQUAL)
		}
	case '<':
		if match(source, '=') {
			addToken(source, tokens, LESS_EQUAL)
		} else {
			addToken(source, tokens, LESS)
		}
	case '>':
		if match(source, '=') {
			addToken(source, tokens, GREATER_EQUAL)
		} else {
			addToken(source, tokens, GREATER)
		}
	case '/':
		if match(source, '/') {
			// A comment goes until the end of the line.
			for peek(source) != '\n' && !isAtEnd(source) {
				advance(source)
			}
		} else {
			addToken(source, tokens, SLASH)
		}
	case ' ', '\r', '\t':
		// Ignore whitespace.
	case '\n':
		line++
	case '"':
		handleString(source, tokens)
	default:
		if isDigit(c) {
			number(source, tokens)
			break
		}
		if isAlpha(c) {
			identifer(source, tokens)
			break
		}
		utils.Error(line, "Unexpected character.")
	}
}

func match(source string, expected byte) bool {
	if isAtEnd(source) {
		return false
	}
	if source[current] != expected {
		return false
	}
	current++
	return true
}

func peek(source string) byte {
	if isAtEnd(source) {
		return '\000'
	}
	return source[current]
}

func advance(source string) byte {
	c := source[current]
	current++
	return c
}

func addToken(source string, tokens *[]Token, tType TokenType) {
	addTokenWithLiteral(source, tokens, tType, nil)
}

func addTokenWithLiteral(source string, tokens *[]Token, tType TokenType, literal any) {
	text := source[start:current]
	*tokens = append(*tokens, Token{Type: tType, Lexeme: text, Literal: literal, Line: line})
}

func isAtEnd(source string) bool {
	return current >= len(source)
}

func handleString(source string, tokens *[]Token) {
	for peek(source) != '"' && !isAtEnd(source) {
		if peek(source) == '\n' {
			line++
		}
		advance(source)
	}

	if isAtEnd(source) {
		utils.Error(line, "Unterminated string.")
		return
	}

	// The closing ".
	advance(source)

	// Trim the surrounding quotes.
	value := source[start+1 : current-1]
	addTokenWithLiteral(source, tokens, STRING, value)
}

func isDigit(c byte) bool {
	return c >= '0' && c <= '9'
}

func number(source string, tokens *[]Token) {
	for isDigit(peek(source)) {
		advance(source)
	}

	// Look for a fractional part.
	if peek(source) == '.' && isDigit(peekNext(source)) {
		// Consume the "."
		advance(source)

		for isDigit(peek(source)) {
			advance(source)
		}
	}

	value := source[start:current]
	addTokenWithLiteral(source, tokens, NUMBER, value)
}

func peekNext(source string) byte {
	if current+1 >= len(source) {
		return '\000'
	}
	return source[current+1]
}

func isAlpha(c byte) bool {
	return (c >= 'a' && c <= 'z') ||
		(c >= 'A' && c <= 'Z') ||
		c == '_'
}

func isAlphaNumeric(c byte) bool {
	return isAlpha(c) || isDigit(c)
}

func identifer(source string, tokens *[]Token) {
	for isAlphaNumeric(peek(source)) {
		advance(source)
	}
	text := source[start:current]
	tType, ok := keywords[text]
	if !ok {
		tType = IDENTIFIER
	}
	addToken(source, tokens, tType)
}

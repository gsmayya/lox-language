package com.craftinginterpreters.lox;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class ScannerTest {

    Scanner scanner;
    List<Token> tokens;

    @BeforeTest void init() {
        scanner = new Scanner("var a = 10;");
    }

    @Test public void testCreation() {
        assertNotNull(scanner);
    }

    @Test (dependsOnMethods = "testCreation")
    public void testTokens() {
        tokens = scanner.scanTokens();
        assertNotNull(tokens);
    }

    @Test (dependsOnMethods = "testTokens")
    public void testEachToken() {
        for (Token token : tokens) {
            assertNotNull(token.lexeme);
        }
    }
}

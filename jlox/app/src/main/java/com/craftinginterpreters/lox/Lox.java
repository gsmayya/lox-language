package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main driver for the interpreter
 */
public class Lox {

    // Global flag to handle Error management.
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    // Actual interpreter
    private static final Interpreter interpreter = new Interpreter();

    /**
     * Main method.
     * If no argument is given, then a REPL loop is started. Else the file is
     * interpreted and run.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Reads the file and runs it.
     * 
     * @param path
     * @throws IOException
     */
    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error
        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    /**
     * Starts a REPL loop to read each line and push it to interpreter.
     * 
     * @throws IOException
     */
    public static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    /**
     * Actual running. Either gets a line to interpret or entire file. Either way it
     * interprets
     * It uses new parser for each line, to parse the tokens.
     * Uses a single instance of Interpreter to keep the state.
     * 
     * @param source
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError) {
            return;
        }

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if (hadError) {
            return;
        }
        // System.out.println(new AstPrinter().print(statements));
        System.out.println(interpreter.interpret(statements));
    }

    /**
     * Method to display error
     * 
     * @param line
     * @param message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Actual method to push the error to Standard output.
     * 
     * @param line
     * @param where
     * @param message
     */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    /**
     * Creates a message to find where the happened in the token string.
     * 
     * @param token
     * @param message
     */
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    /**
     * Any run time error handling. This is almost exit.
     * 
     * @param error
     */
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
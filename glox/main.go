package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strings"

	"gsmayya.com/glox/scanner"
)

var hadError = false

func main() {
	argsWithProg := os.Args
	if len(argsWithProg) > 2 {
		fmt.Println("Usage: glox [script]")
		os.Exit(64)
	} else if len(argsWithProg) == 2 {
		runFile(argsWithProg[1])
	} else {
		runPrompt()
	}
}

func runFile(path string) {
	// Placeholder for file execution logic
	fmt.Printf("Running file: %s\n", path)
	contentBytes, err := os.ReadFile(path)
	if err != nil {
		log.Fatal(err) // Handle any errors that occur during file reading
	}
	// Convert the byte slice to a string
	fileContent := string(contentBytes)

	run(fileContent)

	if hadError {
		os.Exit(65)
	}
}

func runPrompt() {
	// Placeholder for interactive prompt logic
	fmt.Println("Entering interactive prompt mode...")
	reader := bufio.NewReader(os.Stdin)
	for true {
		fmt.Print("> ")
		var input string
		input, err := reader.ReadString('\n')
		if err != nil {
			fmt.Println("Error reading input:", err)
			return
		}
		run(strings.TrimSpace(input))
		hadError = false
	}
}

func run(source string) {
	Tokens := scanner.ScanTokens(source)
	for _, token := range Tokens {
		fmt.Println(token)
	}
}

#!/bin/sh

echo "Building the tool" 
./gradlew build 
echo "Generating the class" 
java -cp build/classes/java/main com.craftinginterpreters.tool.GenerateAst app/src/main/java/com/craftinginterpreters/lox/
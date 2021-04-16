#
# Compiler - CSE 423 - Spring 2020
## Contributors
- Nicholas Smith
- Daniel Aranda
- Ty Darnell
- Alden Towler

## Program Overview

For our compiler we decided to use Java because the Java Virtual Machine (JVM) allows for the program to be widely used, as well as the language was widely understood by our team members. We also used the JavaCC parser generator in order to help us generate our parser. The program offers a very understandable way to implement our generated grammar. Both of these factors should make it easier to develop the compiler and allow for ease of use by users. The compiler will be usable from the command line of Linux systems.

Compiler.java controls the main logic of the Compiler. Parser.jj handles all logic in regards to scannning and parsing. SymbolTable.java handles all logic in regards to the SymbolTable.

## Usage

#### Required software
- JDK 11

#### To use the compiler:

	$ javac Compiler.java

    $ java Compiler [flags] [.c file path]

#### To use -f option

    $ java Compiler -f [output file path] [.c file path]

#### To use -r option

    $ java Compiler -r [IR file]

#### Compiler Options

| Flags | Operation | Supported |
|-------|-----------|-----------|
| -t    | Output a token list |:heavy_check_mark:|
| -pt   | Output a parse tree |:heavy_check_mark:|
| -s    | Output symbol table |:heavy_check_mark:|
| -ir   | Output IR|:heavy_check_mark:|
| -f    | Output IR to a specified file|:heavy_check_mark:|
| -r    | Read in IR instead of source file|:heavy_check_mark:|
| -S    | Output assembly code | :heavy_check_mark: |


## Design Discussion

For the parser we read in character by character, ignoring whitespaces, till we recognize a token. As we take in tokens we add them to a call stack as the program looks at the parameters of that token in the grammar. The parser then looks ahead at other tokens to see if they meet these grammar rules. This process continues until we hit a base case, where no other inputs are needed. The parser then works back up the call stack building the tree incorporating in the grammar specifications. This system is simple to produce and add to, but comes with the trade of un-optimized, as the parser must search recursively through the whole program. We expect a big O(n), with a linear growth rate. This can be seen as a limitation but the capacity for expansion we think makes this limitation worth it.

## Language Specification
Currently we recognize:

#### Required Language Features

|Features|Scanner|Parser|IR|Back End |
|--------|-------|------|--|---------|
|Identifiers|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Variables|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Functions|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:x:|
|Keywords|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Arithmetic Expressions|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Assignment|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Boolean Expressions|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|GOTO Statements|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|If/Else Control Flow|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Unary Operators|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Return Statements|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Break Statements|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|While Loops|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:x:|

#### Optional Language Features

|Features|Scanner|Parser|IR|Back End |
|--------|-------|------|--|---------|
|++|:x:|:x:|:x:|:x:|
|--|:x:|:x:|:x:|:x:|
|-=|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:x:|
|+=|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:x:|
|*=|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:x:|
|/=|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:x:|
|>|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|<|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|\|\||:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|&&|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|>=|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|<=|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|==|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Types Other</br>Than Integers|:x:|:x:|:x:|:x:|
|For Loops|:x:|:x:|:x:|:x:|
|Binary Operators|:x:|:x:|:x:|:x:|
|Switch Statements|:x:|:x:|:x:|:x:|
|Pointers, Arrays, Strings|:x:|:x:|:x:|:x:|
|Preprocessor Statements|:x:|:x:|:x:|:x:|
|Struct, Enum|:x:|:x:|:x:|:x:|
|Casting, Type Promotion|:x:|:x:|:x:|:x:|
|Type Specs|:x:|:x:|:x:|:x:|

## Intermediate Language Design
In designing our Intermediate representation, we aimed for simplicity mimicking the general structure of c code but without the indentations and identifiers.
 - The IR is essentially a list of strings, which each represent 1 instruction, function, or operation.
 - Functions are represented by their name and parameters and are followed by a compound statement:
 	main(a, b){}
 - Compound Statements are contained by pairs of parenthesis.
 - Compound Statements have a local declaration block and a statement list.
 - Our IR ignores local declarations because they are already contained within the symbol table.
 - Expressions are restructured in a three address form.
 - This requires the use of placeholders, represented in the IR by Lx (where x is a number).
 - They take the form of Lz = Lx + Ly. where Lx and Ly are also placeholders which may be a similar expression or a base case such as a variable or constant. For large expressions, this can form a long chain.
 - In generating the IR, a placeholder's chain must be followed, writing the base cases of the placeholder to the IR before it can be written to the IR. In the above example x and y would have to be resolved and expanded before z could.
 - Keywords with a mathematical or conditional expression such as return, if, and while appear as they normally would, followed by a placeholder representing their expression.
 - Many placeholders hold single numbers or variables currently. This can be optimized later with constant folding and propagation.

 For samples of the IR, please see files ending in "\_IR_Sample" in the folder SampleOutputs

## Back End Design and Overview
### Current Status
- See language specification for features implemented in the backend.
- Successful execution of Back End result in the file "a.s"
- The back end produces assembly code given an intermediate representation.
- The back end reads lines of the IR one at a time.
- The back end generates assembly code for each line, the main function, switch_state(), for doing this switches states based on what it reads.
- This allows the back end to structure the code in blocks, via nesting states.
- Memory is managed by the MemoryManager class. This class monitors variable locations both in registers, managed by RegisterManager, and the stack, managed by StackManager.
	- The RegisterManager employs a LRU algorithm for general allocation, copying registers' contents to memory before giving the register over to the variable that needs it. There is also a function in place which demands a specific register. This function is only currently used for the niche of division.

#### Issues with Compiler
- Due to time constraints, several features are not implemented
	- Function calls
		- Functions themselves compile, however, the calling conventions are not implemented and thus they can not be called. The compiler will produce code with unknown behavior when function calls exist.
	- While loops
	- Optimization
		- As it is, the IR is extremely unoptimized. As such, this leads to a lot of unnecessary memory operations and variable assignments in the back end.
	- We have issues with daisy chained if-else statements.

### Best Results
If you want to check out what our compiler can do, try running it with the following file(s) in the tests folder:
	- simple.c

The rest of our test files were unable to produce valid assembly that could be executed. However, the assembly of simple.c can be assembled and executed. (Shown Below).

### To compile and run the a.s file

	$ gcc a.s

This produces an a.out file. If you want a different file name use:

	$ gcc -o [name] a.s

This will give and execution file of the desired name.

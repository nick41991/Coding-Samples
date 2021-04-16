targets= InterpreterMain.java Interpreter.java Function.java Variable.java LexerPatterns.java Lexer.java
all: $(targets)

InterpreterMain.java: lispInterpreter/InterpreterMain.java
	javac lispInterpreter/$@

Interpreter.java: lispInterpreter/Interpreter.java
	javac lispInterpreter/$@

Function.java: lispInterpreter/Function.java
	javac lispInterpreter/$@

Variable.java: lispInterpreter/Variable.java
	javac lispInterpreter/$@

LexerPatterns.java: lispInterpreter/LexerPatterns.java
	javac lispInterpreter/$@

Lexer.java: lispInterpreter/Lexer.java
	javac lispInterpreter/$@

clean:
	rm -rf lispInterpreter/*.class $(targets)

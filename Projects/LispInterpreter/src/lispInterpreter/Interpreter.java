package lispInterpreter;

import java.util.*;
import java.io.*;

/**
 * Evaluates LISP expressions in real time
 * @author Nicholas Smith
 * @bugs none
 * @todo none
 */
public class Interpreter {
	//Stores the user defined functions in memory
	private ArrayList<Function> functions;
	//Stores the user defined variables in memory
	private ArrayList<Variable> vars;
	private File intermediateResults;
	private PrintWriter output;
	
	/**
	 * Builds a new interpreter by initializing variable and function memories
	 */
	public Interpreter() {
		this.functions = new ArrayList<Function>();
		this.vars = new ArrayList<Variable>();
		try {
			this.intermediateResults = new File("intermediateResults.txt");
			this.output = new PrintWriter(this.intermediateResults);
		} catch (FileNotFoundException e) {
			System.out.println("Error in opening intermediate reults file");
		}
	}
	
	public void closeOut() {
		output.flush();
		output.close();
	}
	
	/**
	 * Takes in a new input, presumably from main prompt and evaluates it 
	 * @param input the string to be evaluated
	 * @return the evaluated string
	 */
	public String newInput(String input) {
		Lexer lexer = new Lexer(input);
		return eval(lexer.getTokens(), "newInput");
		
	}	
	
	/**
	 * Main evaluator of LISP expressions
	 * @param tokens a list of the tokens for the interpreter to evaluate
	 * @param caller a string representing what function called eval, used for debugging purposes
	 * @return a string representing the evaluated expression
	 */
	public String eval(List<String> tokens, String caller) {
		output.println("Eval call from: " + caller + "\ttokens passed: " + tokens);
		if(!balParen(tokens)) {
			System.out.println("Error: unbalanced parentheses");
			return "";
		}
		if(tokens == null) {
			System.out.println("Error: eval called with empty tokens");
			return "";
		}
		for(int i = 0; i < tokens.size(); i++) {
			String tok = tokens.get(i);
			if(tok.equals("(")) {
				int index = indexOfClosingParen(tokens);
				if(index > 0) {
					return eval(tokens.subList(i + 1, index), "eval, left paren");
				} else {
					//Throw a mismatched paren error here
				}
			/////////////Base Arithmetic////////////////
			} else if(tok.equals("+")){
				return add(tokens.subList(i + 1, tokens.size()));
				
			} else if(tok.equals("-")) {
				return sub(tokens.subList(i + 1, tokens.size()));
				
			} else if (tok.equals("*")){
				return mult(tokens.subList(i + 1, tokens.size()));
		
			} else if (tok.equals("/")){
				return div(tokens.subList(i + 1, tokens.size()));
				
			} else if (tok.equals("sqrt")){
				return sqrt(tokens.subList(i + 1, tokens.size()));
			
			} else if (tok.equals("exp")){
				return exp(tokens.subList(i + 1, tokens.size()));
			
			////////////Base Logic Comparisons///////////////
			} else if (tok.equals(">")){
				if(tokens.get(i + 1).equals("=")) {
					return greaterThanOrEq(tokens.subList(i + 2, tokens.size()));
				}
				return greaterThan(tokens.subList(i + 1, tokens.size()));
			
			} else if (tok.equals("<")){
				if(tokens.get(i + 1).equals("=")) {
					return lessThanOrEq(tokens.subList(i + 2, tokens.size()));
				}
				return lessThan(tokens.subList(i + 1, tokens.size()));
			
			} else if (tok.equals("=")){
				return equal(tokens.subList(i + 1, tokens.size()));
			
			} else if (tok.equals("!")){
				if(tokens.get(i + 1).equals("=")) {
					return notEqual(tokens.subList(i + 2, tokens.size()));
				}
				//Throw syntax error here
			/////////////Quote////////////////
			} else if (tok.equals("quote")){
				//Return whatever was inside of quote
				output.println("Quote call: " + tokens.subList(i + 1, tokens.size()));
				return buildString(tokens.subList(i + 1, tokens.size()));
			
			/////////////Var definition///////////////////
			} else if (tok.equals("define")){
				return define(tokens.subList(i + 1, tokens.size()));
			
			} else if (tok.equals("set")){
				if(tokens.get(1).equals("!")) {
					for(Variable x : this.vars) {
						if (x.toString().equals(tokens.get(2))) {
							x.setValue(eval(tokens.subList(3,tokens.size()), "eval, set!"));
							output.println("Set variable: " + x.toString() + " to: " + x.getValue());
							return x.toString();
						}
					}
					return "Undeclared variable!" + tokens.get(2);
				}
				
			} else if (tok.equals("defun")){
				return defun(tokens.subList(i + 1, tokens.size()));
				
			} else if (tok.equals("if")) {
				return ifStatement(tokens.subList(i + 1, tokens.size()));
			} else {
				//function call
				for(Function f : this.functions) {
					if(f.toString().equals(tokens.get(0))) {
						output.println("Call to function: " + f.toString() + " called with parameters: " + tokens.subList(1, tokens.size()));
						return eval(f.funcCall(tokens.subList(1, tokens.size())), "eval, func section");
					}
				}
				
				//variable reference
				for(Variable x : this.vars) {
					for(String s : tokens) {
						if (x.toString().equals(s)) {
							output.println("Reference to variable: " + x.toString() + " value stored: " + x.getValue());
							return "" + x.getValue();
						}
					}
				}
				
				//Constant Literal
				if(tokens.get(0).equals("(")){
					output.println("Constant literal: " + tokens.get(1).toString());
					return tokens.get(1).toString();
				}
				return tokens.toString().substring(1,tokens.toString().length()-1);
			}
		}
		
		return "";
	}
	
	/**
	 * Determines if a list contains a balanced number of left and right parentheses
	 * @param tokens the list to be checked
	 * @return true if balanced, false otherwise
	 */
	public boolean balParen(List<String> tokens) {
		int c = 0;
		for(String x : tokens) {
			if(x.equals("(")) {
				c++;
			} else if (x.equals(")")){
				c--;
			}
		}
		
		if(c == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines the index of the closest matching closing parenthesis in a list of tokens
	 * @param tokens the list to be evaluated
	 * @return the index of the closing parenthesis
	 */
	public int indexOfClosingParen(List<String> tokens) {
		int c = 0;
		int i = 0;
		for(String s : tokens) {
			if(s.equals("(")) {
				c++;
			} else if (s.equals(")")) {
				c--;
			} 
			if (c == 0) {
				return i;
			}
			i++;
		}
		//parentheses are mismatched
		return -1;
	}
	
	/**
	 * Builds a String from a List
	 * @param input the List to be built into a Strinng
	 * @return the String representing the list
	 */
	public String buildString(List<String> input) {
		String ret = "";
		for(String s : input) {
			ret = ret.concat(s) + " ";
		}
		return ret;
	}
	
	/**
	 * Adds two expressions together
	 * @param tokens the List representing what is to be added
	 * @return the sum of the expressions within tokens
	 */
	public String add(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function add. Add call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function add. Add call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function add. Add call: " + tokens);
			return "";
		}
		
		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		int result = aR + bR;
		
		output.println("add call: " + tokens + "( + " +  aR + " " + bR + " = " + result + ")");
		return "" + result;	
	}
	
	/**
	 * Subtracts one expression from another
	 * @param tokens the List representing what is to be evaluated
	 * @return the difference of the expressions within tokens
	 */
	public String sub(List <String> tokens) {

		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function sub. Sub call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function sub. Sub call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function sub. Sub call: " + tokens);
			return "";
		}
		
		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		int result = aR - bR;
		
		output.println("sub call: " + tokens + "( - " +  aR + " " + bR + " = " + result + ")");
		return "" + result;	
	}
	
	/**
	 * Multiplies two expressions together
	 * @param tokens the List representing what is to be multiplied
	 * @return the product of the expressions within tokens
	 */
	public String mult(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function mult. Mult call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function mult. Mult call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function mult. Mult call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		int result = aR * bR;
		
		output.println("mult call: " + tokens + "( * " +  aR + " " + bR + " = " + result + ")");
		return "" + result;	
	}
	
	/**
	 * Finds the quotient of two expressions
	 * @param tokens the List representing what is to be divided
	 * @return the quotient of the expressions within tokens
	 */
	public String div(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function div. Div call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function div. Div call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function div. Div call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		int result = aR / bR;
		
		output.println("div call: " + tokens + "( / " +  aR + " " + bR + " = " + result + ")");
		return "" + result;	
	}
	
	/**
	 * Finds the product of x^y
	 * @param tokens the List representing what is to be expon'd
	 * @return the result of x^y
	 */
	public String exp(List <String> tokens) {
		
			List <String> a = new ArrayList<String>();
			List <String> b = new ArrayList<String>();
			//"(" as first token indicates an inner function
			try {
				if(tokens.get(0).equals("(")) {
					a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
				} else {
					a.add(tokens.get(0));
				}
			
				//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
				if(tokens.get(a.size()).equals("(")) {
					b = tokens.subList(a.size(), tokens.size());
				} else {
					b.add(tokens.get(a.size()));
				}
			} catch(IndexOutOfBoundsException e) {
				System.out.println("Error too few arguments passed to function exp. Exp call: " + tokens);
				return "";
			}
			
			if((a.size() + b.size()) < tokens.size()) {
				System.out.println("Error too many arguments passed to function exp. Exp call: " + tokens);
				return "";
			} else if((a.size() + b.size()) > tokens.size()) {
				System.out.println("Error too many arguments passed to function exp. Exp call: " + tokens);
				return "";
			}

			int aR = Integer.parseInt(eval(a, "add"));
			int bR = Integer.parseInt(eval(b, "add"));
			int result = (int)Math.pow(aR, bR);
			
			output.println("exp call: " + tokens + "( exp " +  aR + " " + bR + " = " + result + ")");
			return "" + result;	
		}
	
	/**
	 * Takes the sqrt of a number contained in tokens
	 * @param tokens the list containing the number to be operated on
	 * @return the sqrt of what's stored in tokens
	 */
	public String sqrt(List <String> tokens) {
		
			List <String> a = new ArrayList<String>();
			//"(" as first token indicates an inner function
			try {
				if(tokens.get(0).equals("(")) {
					a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
				} else {
					a.add(tokens.get(0));
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Error in sqrt call expected one argument, got: " + tokens);
				return "";
			}
			if(a.size() != tokens.size()) {
				System.out.println("Error in sqrt call expected one argument, got: " + tokens);
				return "";
			}
			
			int aR = Integer.parseInt(eval(a, "add"));
			int result = (int)Math.sqrt(aR);
			
			output.println("sqrt call: " + tokens + "( sqrt " +  aR + " = " + result + ")");
			return "" + result;	
		}
	
	/**
	 * Determines if one expression is greater than another
	 * @param tokens the List representing what is to be compared
	 * @return the result of the comparison of the expressions within tokens
	 */
	public String greaterThan(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function greaterThan. greaterThan call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function greaterThan. greaterThan call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function greaterThan. greaterThan call: " + tokens);
			return "";
		}
		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		boolean result = aR > bR;
		
		output.println("greater than call: " + tokens + "( > " +  aR + " " + bR + " = " + result + ")");
		return "" + result;		
	}
	
	/**
	 * Determines if one expression is greater than or equal to another
	 * @param tokens the List representing what is to be compared
	 * @return the result of the comparison of the expressions within tokens
	 */
	public String greaterThanOrEq(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function greaterThanOrEq. greaterThanOrEq call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function greaterThanOrEq. greaterThanOrEq call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function greaterThanOrEq. greaterThanOrEq call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		boolean result = aR >= bR;
		
		output.println("greater than or equal to call: " + tokens + "( >= " +  aR + " " + bR + " = " + result + ")");
		return "" + result;
	}
	
	/**
	 * Determines if one expression is less than another
	 * @param tokens the List representing what is to be compared
	 * @return the result of the comparison of the expressions within tokens
	 */
	public String lessThan(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function lessThan. lessThan call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function lessThan. lessThan call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function lessThan. lessThan call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		boolean result = aR < bR;
		
		output.println("less than call: " + tokens + "( < " +  aR + " " + bR + " = " + result + ")");
		return "" + result;		
	}
	
	/**
	 * Determines if one expression is less than or equal to another
	 * @param tokens the List representing what is to be compared
	 * @return the result of the comparison of the expressions within tokens
	 */
	public String lessThanOrEq(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function add. Add call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function add. Add call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function add. Add call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		boolean result = aR <= bR;
		
		output.println("less than or equal to call: " + tokens + "( > " +  aR + " " + bR + " = " + result + ")");
		return "" + result;		
	}
	
	/**
	 * Determines if one expression is equal to another
	 * @param tokens the List representing what is to be compared
	 * @return the result of the comparison of the expressions within tokens
	 */
	public String equal(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function equal. Equal call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function equal. Equal call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function equal. Equal call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		boolean result = aR == bR;
		
		output.println("equal to call: " + tokens + "( == " +  aR + " " + bR + " = " + result + ")");
		return "" + result;
	}
	
	/**
	 * Determines if one expression is not equal to another
	 * @param tokens the List representing what is to be compared
	 * @return the result of the comparison of the expressions within tokens
	 */
	public String notEqual(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function notEqual. notEqual call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function notEqual. notEqual call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function notEqual. notEqual call: " + tokens);
			return "";
		}

		int aR = Integer.parseInt(eval(a, "add"));
		int bR = Integer.parseInt(eval(b, "add"));
		boolean result = !(aR == bR);
		
		output.println("not equal to call: " + tokens + "( == " +  aR + " " + bR + " = " + result + ")");
		return "" + result;		
	}
	
	/**
	 * Defines a new Variable to be stored in memory
	 * @param tokens the List containing the variable name and value
	 * @return the name of the created variable
	 */
	public String define(List <String> tokens) {
		
		List <String> a = new ArrayList<String>();
		List <String> b = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				a = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			} else {
				a.add(tokens.get(0));
			}
		
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
			if(tokens.get(a.size()).equals("(")) {
				b = tokens.subList(a.size(), tokens.size());
			} else {
				b.add(tokens.get(a.size()));
			}
		} catch(IndexOutOfBoundsException e) {
			System.out.println("Error too few arguments passed to function define. Define call: " + tokens);
			return "";
		}
		
		if((a.size() + b.size()) < tokens.size()) {
			System.out.println("Error too many arguments passed to function define. Define call: " + tokens);
			return "";
		} else if((a.size() + b.size()) > tokens.size()) {
			System.out.println("Error too many arguments passed to function define. Define call: " + tokens);
			return "";
		}
		
		this.vars.add(new Variable(a, eval(b, "define")));
		output.println(vars.get(vars.size() - 1).name + " successfully defined as: " + vars.get(vars.size() - 1).getValue());
		return "" + this.vars.get(vars.size() - 1).toString();		
	}

	/**
	 * Defines a new Function to be stored in memory
	 * @param tokens the List containing the Function name, parameters, and defining expression
	 * @return the name of the created function
	 */
	public String defun(List <String> tokens) {
		List <String> name = new ArrayList<String>();
		List <String> params = new ArrayList<String>();
		List <String> definition = new ArrayList<String>();
		//"(" as first token indicates an inner function
		name.add(tokens.get(0));
		//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
		try {
			if(tokens.get(1).equals("(")) {
				params = tokens.subList(1, indexOfClosingParen(tokens.subList(1, tokens.size())) + 2);
			} else {
				params.add(tokens.get(name.size()));
			}
			if(tokens.get(name.size() + params.size()).equals("(")) {
				definition = tokens.subList(name.size() + params.size(), tokens.size());
			} else {
				definition.add(tokens.get(name.size() + params.size()));
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("error in defun call: expected name (params) (definition), got " + tokens);
			return "";
		}
		if(name.size() + params.size() + definition.size() != tokens.size()){
			System.out.println("error in defun call: expected name (params) (definition), got " + tokens);
			return "";
		}
		
		this.functions.add(new Function(name, params, definition));
		output.println("User Function " + functions.get(functions.size() - 1).fname + " successfully defined with parameters" + params + ". definition: " + definition);
		return "" + this.functions.get(functions.size() - 1).toString();		
	}
	
	/**
	 * Evaluates a conditional statement
	 * @param tokens the conditional statement to be evaluated
	 * @return the result of the conditional
	 */
	public String ifStatement(List <String> tokens) {
		
		List <String> cond = new ArrayList<String>();
		List <String> condTrue = new ArrayList<String>();
		List <String> condFalse = new ArrayList<String>();
		//"(" as first token indicates an inner function
		try {
			if(tokens.get(0).equals("(")) {
				cond = tokens.subList(0, indexOfClosingParen(tokens) + 1);
			}
			//a.size is the number of tokens stored in a, thus b.size is tokens.size - a.size
		
			if(tokens.get(cond.size()).equals("(")) {
				condTrue = tokens.subList(cond.size(), indexOfClosingParen(tokens.subList(cond.size(), tokens.size())) + cond.size() + 1);
			} else {
				condTrue.add(tokens.get(cond.size()));
			}
		
			if(tokens.get(cond.size() + condTrue.size()).equals("(")) {
				condFalse = tokens.subList(cond.size() + condTrue.size(), indexOfClosingParen(tokens.subList(cond.size() + condTrue.size(), tokens.size()) ) + cond.size() + condTrue.size() + 1);
			} else {
				condFalse.add(tokens.get(cond.size() + condTrue.size()));
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error in if statement, expected (if (cond) (cond true) (cond false)), got " + tokens);
		}
			
		if(cond.size() + condTrue.size() + condFalse.size() != tokens.size()) {
			System.out.println("Error in if statement, expected (if (cond) (cond true) (cond false)), got " + tokens);
			return "";
		}
		
		//Final Evaluation
		if(eval(cond, "if").equals("true")) {
			output.println("Cond: " + cond + " Eval'd true. Executing: " + condTrue);
			return eval(condTrue, "if True");
		} else {
			output.println("Cond: " + cond + " Eval'd false. Executing: " + condFalse);
			return eval(condFalse, "if False");
		}
	}
}

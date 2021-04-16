package lispInterpreter;

import java.util.Scanner;

/**
 * Main Input=>Evaluate=>Output Loop for LISP interpreter
 * @author Nicholas Smith
 * @bugs none
 */
public class InterpreterMain {
	public static void main(String args[]) {
		Interpreter inter = new Interpreter();
		Scanner s = new Scanner(System.in);
		String input = "";
		System.out.println("Welcome to the interpreter for those with speech impediments,\ntype in LISP commands!");
		
		while(!input.equals("(quit)")) {
			System.out.print("> ");
			if(s.hasNextLine()) {
				input = s.nextLine();
				if(input.equals("(quit)")) {
					break;
				}
				System.out.println("> " + inter.newInput(input));
			} else {
				System.out.println("I didn't quite catch that. Try again");
			}
		}
		inter.closeOut();
		System.out.println("Goobye!");
		s.close();
	}
}

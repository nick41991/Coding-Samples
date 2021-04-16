package lispInterpreter;
import java.util.*;

/**
 * Parses LISP expressions into a List of Tokens
 * @author Nicholas Smith
 * @bugs none
 * @todo none
 */
public class Lexer {
	private String input;
	private List<String> tokens;

	public Lexer(String in) {
		this.input = in.trim().toLowerCase();
		this.tokens = tokenize(this.input);
	}
	
	public List<String> tokenize(String input){
		List<String> toks = new ArrayList<String>();
		
		//Avoid index oob error
		if(input.length() == 1) {
			toks.add(input);
			return toks;
		}
		
		for(int i = 0; i < input.length(); i++) {
			int j = i + 1;
			//Determine if input.substring(i, j) is a parenthesis or something else
			if(input.substring(i, j).matches(LexerPatterns.Letters) || input.substring(i, j).matches(LexerPatterns.Atom)) {
				//If it's something else, we want to find the end of the word ie where there is a space or paren
				while(input.substring(i, j + 1).matches(LexerPatterns.Literal) || input.substring(i, j + 1).matches(LexerPatterns.Atom)) {
					j++;
				}
				toks.add(input.substring(i, j));
			} else if(input.substring(i, j).matches(LexerPatterns.Parens)){
				//input.substring(i,j) is a parenthesis
				toks.add(input.substring(i, j));
			}
			//input from i to j has been tokenized so set i equal to j to skip
			i = j - 1;
		}
		return toks;
	}
	
	public List<String> getTokens(){
		return tokens;
	}
	
	public void setInput(String in) {
		input = in;
		//Update to input means tokens must be updated
		tokens = tokenize(this.input);
	}
	
}

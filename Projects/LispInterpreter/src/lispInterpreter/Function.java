package lispInterpreter;

import java.util.*;

/**
 * Defines a function with a name, list of parameters and a definition String
 * @author Nicholas Smith
 * @bugs none
 */
public class Function {
	public String fname;
	private List<String> def;
	private List<String> params;
	
	/**
	 * Creates a new user defined Function in memory
	 * @param name the name of the function
	 * @param params the parameters the function takes
	 * @param definition what the function does
	 */
	public Function(List<String> name, List<String> params, List<String> definition) {
		this.fname = "";
		for(String s : name) {
			this.fname = this.fname + s;
		}
		this.params = params;
		this.def = definition;		
	}
	
	/**
	 * Invokes the function using the passed parameters
	 * @param passedParams the parameters passed to the function
	 * @return the definition String with the defining parameters replaced with the passed parameters to be evaluated by the calling interpreter
	 */
	public List<String> funcCall(List<String> passedParams) {
//		if(checkAgreement(params)) {
//			return null;
//			//throw new IllegalArgumentException("Error in parameter agreement \nCall:" + this.name + " " + this.param.toString() + "\nUsage: (" + this.name + this.param.toString()+ ")");
//		}
		passedParams = forceAgreement(passedParams);
		if(checkAgreement(passedParams)) {
			System.out.println("Agreement error");
			return null;
			//throw error here
		}
		List <String> toEval = new ArrayList<String>();
		boolean paramAdded;
		for(String s : this.def) {
			if(s.matches(LexerPatterns.Literal)) {
				//ParamAdded is used to add Literals that are within definition but are not parameters (e.g. if)
				paramAdded = true;
				for(String x : this.params) {
					//Replace parameters in def string with passed parameters
					if(x.equals(s)) {
						toEval.add(passedParams.get(params.lastIndexOf(x)));
						paramAdded = true;
						break;
					} else {
						paramAdded = false;
					}
				}
				if(!paramAdded) {
					toEval.add(s);
				}
			} else {
				toEval.add(s);
			}
		}
		toEval = expand(toEval);
		return toEval;
	}
	
	/**
	 * Checks to make sure the number of parameters passed to the function agree with it's defintion
	 * @param toCheck the passed parameters
	 * @return true if not in agreement. false otherwise
	 */
	public boolean checkAgreement(List<String> toCheck) {
		if(toCheck.size() == this.params.size()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Condenses tokens from params into single parameters
	 * e.g. a func with params (x y) called with [(,(, +, 3, 4,), (,+, 3, 4,),)] 
 	 * becomes [(, (+ 3 4), (+ 3 4), )]
	 * @param params the list to be condensed
	 * @return the condensed list
	 */
	public List<String> forceAgreement(List<String> params){
		List<String> condensed = new ArrayList<String>();
		for(int i = 0; i < params.size(); i++) {
			if( i != 0 && params.get(i).equals("(")) {
				String tmp = "";
				while( i < params.size() - 1) {
					if(params.get(i).equals(")")){	
						tmp = tmp.concat(params.get(i));
						break;
					}
					tmp = tmp.concat(params.get(i) + " ");
					System.out.println("i: "+ i + " tmp string: " + tmp);
					i++;
				}
				condensed.add(tmp);
			} else {
				condensed.add(params.get(i));
			}
		}
		return condensed;
	}
	
	/**
	 * Expands the toEval List such that there are no complex tokens ie [(+ 3 4)] => [(, +, 3, 4, )] 
	 * @param toEval the List to be expanded
	 * @return the expanded List
	 */
	public List<String> expand(List<String> toEval){
		String toTok = "";
		for(String x : toEval) {
			//Put all tokens into a String
			toTok = toTok.concat(x + " ");
		}
		//tokenize String of tokens
		Lexer lex = new Lexer(toTok);
		return lex.getTokens();
	}
	
	@Override
	/**
	 * Returns a String representation of the Function
	 * @return the name of the function
	 */
	public String toString() {
		return this.fname;
	}
}

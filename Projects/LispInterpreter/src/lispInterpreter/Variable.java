package lispInterpreter;

import java.util.*;

/**
 * Defines a variable with a name and value
 * @author Nicholas Smith
 * @bugs none
 */
public class Variable {
	public String name;
	private int value;
	
	/**
	 * Creates a new Variable in memory
	 * @param name The name of the variable
	 * @param val the value to be stored in the variable
	 */
	public Variable(List<String> name, String val) {		
		this.name = name.get(0);
		value = Integer.parseInt(val);
	}
	
	/**
	 * Gives access to private field: value
	 * @return the value stored in value
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * Sets the private field value
	 * @param val the val the variable is to be set to
	 */
	public void setValue(String val) {
		this.value = Integer.valueOf(val);
	}
	
	@Override
	/**
	 * Returns a String representation of the variable
	 * @return the name of the variable
	 */
	public String toString() {
		return name;
	}
}

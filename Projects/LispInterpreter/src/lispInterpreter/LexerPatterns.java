package lispInterpreter;

/**
 * Defines a set of ranges which allow Lexer.java to split input strings
 * @author Nicholas Smith
 * @bugs none
 */
public class LexerPatterns {
	public static String Letters = "[a-zA-Z!]";
	public static String Literal = "[\\w]+";
	public static String Space = "[\\s]+";
	public static String Atom = "[\\d\\+\\-\\*\\/\\>\\<\\=\\!]?[\\d]*";
	public static String Parens = "[()]";
}

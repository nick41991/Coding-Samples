
/** Creates a token with a value and type
 * 
 */
public class SyntaxToken {
	String type;
	String image;

	public SyntaxToken(){
		type = null;
		image = null;
	}

	public SyntaxToken (String t, String i){
		type = t;
		image = i;
	}

	public String toString(){
		return ("<" + image + ", " + type + ">");
	}

}

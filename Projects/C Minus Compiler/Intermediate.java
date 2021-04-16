/**
	Generator class for an intermediate representation
*/
import java.util.ArrayList;
public class Intermediate
{
	private int ph_num; /*Used in assigning placeholder names*/
	private ArrayList<Placeholder> placeholders; // Store placeholders
	private SymbolTable st; // Store main symbol table
	private SymbolTable context; // Working table
	private int while_label;

	/*Constructor for class to generate IR from parse tree*/
	public Intermediate(SymbolTable sym){
		st = sym;
		ph_num = 1;
		placeholders = new ArrayList<Placeholder>();
		context = sym;
		while_label = 0;
	}

	/*Generates an IR from a parse tree*/
	public IntRep run(Node root){
		IntRep ir = new IntRep();

		io_traverse(root, 0);
		two_char_ph_flatten();
		one_char_ph_flatten();
		/*Placeholder print for debugging*/
		// for(Placeholder p : placeholders){
		// 	System.out.println(p.name + ": " + p.expression + ", " + p.height);
		// }
		/*Post flattening tree print for debugging*/
		//root.printParseTree(root,0);
		write_IR(root, 0, ir);
		return ir;
	}

	/*In order traversal of a tree, begins flattening process*/
	private void io_traverse(Node root, int height){
		/*If expression, branch into expression handler*/
		if(height == 1){
			context = st.getTable(root.getPayload());
		}
		if(root.getPayload().equals("Expression")){
			flatten(root, height);
		} else {
			/*If not expression, keep traversing*/
			for(Node c : root.children){
				io_traverse(c, height + 1);
			}
		}
	}

	/*Expression handling function*/
	private void flatten(Node root, int height){
		//If a node is an expression, we will create a placeholder for it
		//Make expression string

		String exp = "";
		for(Node c : root.children){
			if(c.getPayload().equals("Expression")){
				flatten(c, height + 1);
			}
			exp = exp.concat(c.getPayload());

			/*This part was added for handling function calls (or expression Node with expression children).
			After, a function call in the tree looks like: nameL1L2L3
			where name is the function name and each placeholder Lx is a param
			*/
			if(c.children != null){
				for(Node d: c.children){
					if(d.getPayload().equals("Expression")){
						flatten(d, height + 1);
						exp = exp.concat("L" + (ph_num - 1));
					}
				}
			}
		}

		/*Replace the expression with the generated placeholder*/
		Placeholder expression = new Placeholder("L" + ph_num, exp, context.name, height);
		placeholders.add(expression);
		/*Add ph to Symbol table*/
		context.addSymbol("int", expression.name);
		/*Update node with ph info*/
		root.setPayload(expression.name);
		root.children.clear();
		ph_num++;
	}

	/*Placeholders with mutilplication and division can hold more than one
	operation due to the tree structure.
	Here, they will be ironed into 3 addr holders.*/
	private void one_char_ph_flatten(){
		boolean modified = false;
		boolean two_char;
		int op_count;
		ArrayList<Placeholder> newPlaceholders = new ArrayList<Placeholder>();
		for(Placeholder p : placeholders){
			context = st.getTable(p.context);
			op_count = getOpCount(p.expression);
			/*A count of operators tells us how many operations occur within a ph*/
			if(op_count > 1){
				two_char = false;
				String exp;
				int ops = 0;
				char cur;
				/*Determine index for substring*/
				int j;
				for(j = 0; j < p.expression.length() && ops != 2; j++){
					switch(p.expression.charAt(j)){
						/*! is not included because it only has one expression attached to it*/
						case '+':
						case '-':
						case '*':
						case '/':
						case '%':
						case '^':
						case '&': /*Two character ops need to be checked for so that they can be handled in the appropriate function*/
							if(j < p.expression.length() - 1 && p.expression.charAt(j + 1) == '&'){
								two_char = true;
								break;
							}
						case '|':
							if(j < p.expression.length() - 1 && p.expression.charAt(j + 1) == '&'){
								two_char = true;
								break;
							}
						case '>':
							if(j < p.expression.length() - 1 && p.expression.charAt(j + 1) == '&'){
								two_char = true;
								break;
							}
						case '<':
							if(j < p.expression.length() - 1 && p.expression.charAt(j + 1) == '&'){
								two_char = true;
								break;
							}
							ops++;
						default:
							break;
					}
				}
				if(!two_char){

					modified = true;
					/*Create new placeholder*/
					exp = p.expression.substring(0, j - 1);
					Placeholder sub_expression = new Placeholder("L" + ph_num, exp, context.name, p.height + 1);
					context.addSymbol("int", sub_expression.name);
					p.expression = sub_expression.name.concat(p.expression.substring(j - 1));
					newPlaceholders.add(sub_expression);
					ph_num++;
				}
			}
		}
		if(modified){
			for(Placeholder p : newPlaceholders){
				placeholders.add(p);
			}
			one_char_ph_flatten();
		}
	}

	/*Count operators in a string*/
	private int getOpCount(String s){
		int op_count = 0;
		for(int i = 0; i < s.length(); i++){
			switch(s.charAt(i)){
				case '+':
				case '-':
				case '*':
				case '/':
				case '%':
				case '^':
				case '&':
				case '|':
				case '>':
				case '<':
					op_count++;
				default:
					break;
			}
		}
		return op_count;
	}

	/*Flatten out two character operations such as && and +=*/
	private void two_char_ph_flatten(){
		boolean modified = false;
		int op_count = 0;
		int i;
		String cur;
		ArrayList<Placeholder> newPlaceholders = new ArrayList<Placeholder>();
		for(Placeholder p : placeholders){
			context = st.getTable(p.context);
			for(i = 0; i < (p.expression.length() - 1); i++){
				cur = p.expression.substring(i, i + 1);
				if(cur.equals("&&")){
					op_count++;
				} else if(cur.equals("||")){
					op_count++;
				} else if(cur.equals("==")){
					op_count++;
				} else if(cur.equals(">=")){
					op_count++;
				} else if(cur.equals("<=")){
					op_count++;
				} else if(cur.equals(">>")){
					op_count++;
				} else if(cur.equals("<<")){
					op_count++;
				} else if(cur.equals("*=")){
					op_count++;
				} else if(cur.equals("/=")){
					op_count++;
				} else if(cur.equals("+=")){
					op_count++;
				} else if(cur.equals("-=")){
					op_count++;
				}
			}
			/*A count of operators tells us how many operations occur within a ph*/
			if(op_count > 1){
				modified = true;
				String exp;
				int ops = 0;
				/*Determine index for substring*/
				int j;
				for(j = 0; j < (p.expression.length() - 1) && ops != 2; j++){
					cur = p.expression.substring(j, j + 1);
					if(cur.equals("&&")){
						ops++;
					} else if(cur.equals("||")){
						ops++;
					} else if(cur.equals("==")){
						ops++;
					} else if(cur.equals(">=")){
						ops++;
					} else if(cur.equals("<=")){
						ops++;
					} else if(cur.equals(">>")){
						ops++;
					} else if(cur.equals("<<")){
						ops++;
					} else if(cur.equals("*=")){
						ops++;
					} else if(cur.equals("/=")){
						ops++;
					} else if(cur.equals("+=")){
						ops++;
					} else if(cur.equals("-=")){
						ops++;
					}
				}
				/*Create new placeholder*/
				exp = p.expression.substring(0, j - 1);
				Placeholder sub_expression = new Placeholder("L" + ph_num, exp, context.name, p.height + 1);
				context.addSymbol("int", sub_expression.name);
				p.expression = sub_expression.name.concat(p.expression.substring(j - 1));
				newPlaceholders.add(sub_expression);
				ph_num++;
			}
		}
		if(modified){
			for(Placeholder p : newPlaceholders){
				placeholders.add(p);
			}
			two_char_ph_flatten();
		}
	}

	/*Handles general program structure such as functions Statement lists will be handled in seperate function*/
	private void write_IR(Node node, int height, IntRep ir){
		//System.out.println("Working with table: " + table);
		String s = "";

		if(height == 0){ /*Top node is always Program*/
			/*Write nothing move to children*/
			for(Node c: node.children){
				context = st.getTable(c.getPayload());
				write_IR(c, height + 1, ir);
			}

		} else if(height == 1){ /*Function declarations*/

			s = s.concat(node.getPayload() + "(");
			// Code for adding parameters in IR.
			// With addition of parameter count in Symbol table, we do not need to do this.
			// Instead, parameters will be added from table.
			// if(node.children.get(1).children.size() > 0){
			// 	for(Node c : node.children.get(1).children){
			// 		s = s.concat(c.getPayload() + ", ");
			// 	}
			// 	s = s.substring(0, s.length() - 2);
			// }
			s = s.concat("){");
			ir.addLine(s);
			write_IR(node.children.get(2), height + 1, ir);
			ir.addLine("}");
		} else if(height == 2) { /*Compound statement processing, there are two parts, local declarations and the statement list*/
			write_Compound_Statement(node, ir);
		}

	}

	/*Writes IR compund statements*/
	private void write_Compound_Statement(Node node, IntRep ir){
		String s = "";
		/*Local Declarations may or may not exist*/
		if(node.children.size() > 1){
			/*Local Declarations-- Probably not necessary*/
			// for(Node c: node.children.get(0).children){
			// 	s = s.concat(c.getPayload());
			// 	ir.addLine(s);
			// 	s = "";
			// }
			/*Statement list*/
			for(Node c: node.children.get(1).children){
				write_Statement(c, ir);
			}
		} else {
			/*Just a statement list*/
			for(Node c: node.children.get(0).children){
				write_Statement(c, ir);
			}
		}
	}

	/*Writes IR for Statement Lists*/
	/*Each node passed to this function is a type of statment*/
	private void write_Statement(Node node, IntRep ir){
		/**
		Cases to handle:
			LabeledStatement	+
		  	ExpressionStatement	+
		  	SelectionStatement	+
		  	IterationStatement	+
		  	JumpStatement     	+
		*/
		String payload = node.getPayload();
		String s = "";
		String condition;
		//System.out.println(payload);

		if(payload.equals("if")){
			//boolean chained = true;
			Node tmp = node;
			//while(chained) {
			//	chained = false;
				condition = tmp.children.get(0).children.get(0).getPayload(); //if condition
				expandPlaceholder(condition, findPlaceholder(condition).expression, ir);
				s = s.concat("if " + condition + " {");
				ir.addLine(s);
				s = "";
				write_Compound_Statement(tmp.children.get(1), ir);
				s = s.concat("}");
				/*Handle elses'*/
				if(tmp.children.size() == 3){
					s = s.concat(" else {");
					ir.addLine(s);
					s = "";
					//System.out.println("if block:" + tmp.children.get(2).children.get(0).children.get(0).getPayload());

					if(tmp.children.get(2).children.get(0).children.get(0).getPayload().equals("if")){
						//System.out.println("if block:" + tmp.children.get(2).children.get(0).children.get(0).getPayload());

						write_Statement(tmp.children.get(2).children.get(0).children.get(0), ir);
					} else {
						write_Compound_Statement(tmp.children.get(2).children.get(0), ir);
					}
					ir.addLine("}");
					//Check if else-if chained
					// if(tmp.children.get(2).children.get(0).children.get(0).getPayload().equals("if")){//else-if chaining
					// 	tmp = tmp.children.get(2).children.get(0).children.get(0);
					// 	s = s.concat(" else ");
					// 	chained = true;
					// } else {
					// 	s = s.concat(" else {");
					// 	ir.addLine(s);
					// 	s = "";
					// 	write_Compound_Statement(tmp.children.get(2).children.get(0), ir);
					// 	s = s.concat("\n}");
					// 	ir.addLine(s);
					// 	s = "";
					// }
				} else {
					ir.addLine(s);
					s = "";
				}
			//}
		} else if(payload.equals("while")){
			condition = node.children.get(0).children.get(0).getPayload(); //if condition
			expandPlaceholder(condition, findPlaceholder(condition).expression, ir);
			ir.addLine("TOP_WHILE" + while_label + ":");
			s = s.concat("while " + condition + " {");
			ir.addLine(s);
			s = "";
			write_Compound_Statement(node.children.get(1), ir);
			/*Need to recalculate condition at end of loop.
			Putting it here again makes it easy for testing in asm*/
			expandPlaceholder(condition, findPlaceholder(condition).expression, ir);
			ir.addLine("goto TOP_WHILE" + while_label + ":");
			while_label++;
			s = s.concat("}");
			ir.addLine(s);
			s = "";
		} else if(payload.equals("GOTO")){
			s = s.concat("jmp " + node.children.get(0).getPayload());
			ir.addLine(s);
			s = "";
		} else if(payload.equals("Continue")){
			s = s.concat("continue");
			ir.addLine(s);
			s = "";
		} else if(payload.equals("Break")){
			s = s.concat("break");
			ir.addLine(s);
			s = "";
		} else if(payload.equals("Return")){
			s = s.concat("return ");
			if(node.children.size() > 0){
				s = s.concat(node.children.get(0).getPayload());
				expandPlaceholder(node.children.get(0).getPayload(), findPlaceholder(node.children.get(0).getPayload()).expression, ir);
			}
			ir.addLine(s);
			s = "";
		} else if(payload.charAt(0) == 'L' && findPlaceholder(payload) != null){ /*All expressions in the tree have been replaced with placeholders*/
			expandPlaceholder(payload, findPlaceholder(payload).expression, ir);
		} else { //If all else fails, assume its a Label
			s = s.concat(payload + ":");
			ir.addLine(s);
			//System.out.println(node.getPayload());
			write_Statement(node.children.get(0), ir);
		}

	}

	/*Follows chains of placeholders until at the top of a chain. Then writes IR in correct order.*/
	private void expandPlaceholder(String child, String exp, IntRep ir){
		String name;
		Placeholder ph;
		int i;
		int j;
		for(i = 0; i < exp.length() - 1; i++){
			if(exp.charAt(i) == 'L'){
				for(j = i + 1; j < exp.length(); j++){
					if(exp.charAt(j) < '0' || exp.charAt(j) > '9'){
						break;
					}
				}
				name = exp.substring(i, j);
				ph = findPlaceholder(name);
				i = j;
				expandPlaceholder(ph.name, ph.expression, ir);
			}
		}
		name = child.concat("=" + exp);
		ir.addLine(name);

	}

	/*Finds a placeholder within the placeholder list by name*/
	private Placeholder findPlaceholder(String name){
		for(Placeholder p : placeholders){
			if(p.name.equals(name)){
				return p;
			}
		}
		return null;
	}
}

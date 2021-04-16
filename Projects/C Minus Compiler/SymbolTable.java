import java.util.*;

/**
 * Class for handling anything in relation to the Symbol Table
 */
public class SymbolTable {
    //characteristics of a symbol
    public static class Symbol {
        //public String scope;
        public String type;
        public String symbol;

        public Symbol(String t, String sym) {
            type = t;
            symbol = sym;
        }
    };

    public SymbolTable parent; //keeps track of parent table
    public ArrayList<SymbolTable> children; //keeps track of child tables
    public ArrayList<Symbol> symbols; //keeps track of symbols in table
    public String name;
    public int level;
    public int num_params;

    public SymbolTable() {
        parent = null;
        children = new ArrayList<SymbolTable>();
        symbols = new ArrayList<Symbol>();
        name = "Program";
	num_params = 0;
    }

    public SymbolTable(String n) {
        parent = null;
        children = new ArrayList<SymbolTable>();
        symbols = new ArrayList<Symbol>();
        name = n;
	num_params = 0;
    }

    public void addSymbol(Symbol s) {
        symbols.add(s);
    }

    public void addSymbol(String type, String sym) {
	Symbol s = new Symbol(type, sym);
        symbols.add(s);
    }

    public void addChild(SymbolTable child) {
        children.add(child);
    }

    /* Creates all symbol tables for functions */
    public static SymbolTable createSymbolTable(Node node) {
        SymbolTable symRoot = new SymbolTable();
        Symbol sym;
        symRoot = getSymbols(symRoot, node, 1);

        return symRoot;
    }

	/*Get a symbol table by name*/
    public SymbolTable getTable(String name) {
	    for(SymbolTable st: children){
		    if(st.name == name){
			    return st;
		    }
	    }
	    return null;
    }

    /* Checks for any declarations
     * Recursively traverses tree until it finds an int
     */
    public static SymbolTable getSymbols(SymbolTable st, Node node, int level) {
        Symbol sym;

        for(Node n : node.children) {
            if(n.payload.equals("int")) {
                sym = new Symbol(n.payload, n.parent.payload);
                st.addSymbol(sym);
            } else if(n.payload.equals("if")) {
                SymbolTable s = new SymbolTable(n.payload);
                s.level = level;
                s = getSymbols(s, n, level + 1);
                st.addChild(s);
                s.parent = st;
            } else if(n.payload.equals("else")) {
                SymbolTable s = new SymbolTable(n.payload);
                s.level = level;
                s = getSymbols(s, n, level + 1);
                st.addChild(s);
                s.parent = st;
            } else if(n.payload.equals("while")) {
                SymbolTable s = new SymbolTable(n.payload);
                s.level = level;
                s = getSymbols(s, n, level + 1);
                st.addChild(s);
                s.parent = st;
            } else if(n.payload.equals("params")) {
                SymbolTable s = new SymbolTable(n.parent.payload);
                s.level = level;
		//Params
                s = getSymbols(s, n, level + 1);
		s.num_params = s.symbols.size();
		//Local Declarations
                s = getSymbols(s, node.children.get(2), level + 1);
                st.addChild(s);
                s.parent = st;
                break;
            } else {
                getSymbols(st, n, level);
            }
        }

        return st;
    }

    /* Prints the symbol table */
    public static void printSymbolTable(SymbolTable st) {
        int i;
        String name = st.name;
        if(st.parent != null) {
            name = st.name + " (" + st.parent.name + ")";
        }

        System.out.print("\n\n");
        for(i = 0; i < name.length() + 4; i++) {
            System.out.print("-");
        }
        System.out.println("\n| " + name + " | " + st.level);
        System.out.println("--------------------");
        System.out.println("Symbol\tType");
        System.out.println("--------------------");
        for(Symbol s : st.symbols) {
            System.out.println(s.symbol + "\t" + s.type);
        }
        if(st.symbols.isEmpty()) {
            System.out.println("Empty symbol table");
        }
        System.out.println("--------------------\n");

        for(SymbolTable s : st.children) {
            printSymbolTable(s);
        }

    }

    public Boolean lookUp(SymbolTable st) {
        return true;
    }



}

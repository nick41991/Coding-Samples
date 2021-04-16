import java.util.*;

public class Compiler {
    public static void main(String[] args) {
        Parser c = new Parser();

    	boolean tokenBool = false;
		boolean parseTreeBool = false;
    	boolean symbolTableBool = false;
		boolean irBool = false;
		boolean setFileName = false;
		boolean setIRName = false;
		boolean IRWriteBool = false;
		boolean irReadInBool = false;
        boolean asmBool = false;
		String fileName = null;
		String IRFileName = null;


		// worked on by daniel and alden
		for(int i = 0; i < args.length; i++){

        	switch(args[i]){
				case "-t":
					tokenBool = true;
					break;

				case "-pt":
					parseTreeBool = true;
					break;

				case "-s":
					symbolTableBool = true;
					break;

				case "-ir":
					irBool = true;
					break;

				case "-r":
					irReadInBool = true;
					break;


				case "-f":
					IRWriteBool = true;
					setIRName = true;
					break;

                		case "-S":
                    			asmBool = true;

				default:
					if (setIRName){

						IRFileName = args[i];
						setIRName = false;
					}
					else {
						fileName = args[i];
						setFileName = true;
					}
					break;

        	}
        }

// split to avoid trying to parse and scan an IR representation, worked on by daniel and alden

       	SymbolTable symRoot = null;
	IntRep ir;
	if(irReadInBool){

		//System.out.println("in reading section");

		ir = new IntRep(fileName);


		if(irBool){
			ir.write();
		}


		if (IRWriteBool){
			ir.toFile(IRFileName);
		}

	}else{

        	Node root = c.run(tokenBool, setFileName, fileName);
        	if (parseTreeBool) {
        		root.printParseTree(root,0);
		    }

        	symRoot = SymbolTable.createSymbolTable(root);

		Intermediate n = new Intermediate(symRoot);
		ir = n.run(root);

		//Optimize IR here -- Optimized IR should be equivlent to unoptimized
		//Simple constant fold and prop

		if(symbolTableBool) {
            		SymbolTable.printSymbolTable(symRoot);
        	}
		if(irBool){
			ir.write();
		}

		if (IRWriteBool){
			ir.toFile(IRFileName);
		}
	}
	//Begin Backend
	//Write ASM
	Backend backend = new Backend(ir, symRoot);
	backend.run(asmBool);
    }
}

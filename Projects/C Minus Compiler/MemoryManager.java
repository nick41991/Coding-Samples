// Manages memory
import java.util.ArrayList;
public class MemoryManager{
	private StackManager stack;
	private RegisterManager registers;
	private ArrayList<MemoryUnit> units;

	/*Unit of memory. Every variable gets a space in the stack when allocated, regardless of temporality*/
	public class MemoryUnit{
		String name; // reference
		String scope; // scope
		int offset;
		boolean inRegister;
		String register;

		public MemoryUnit(String n, String s, int o){
			name = n;
			scope = s;
			offset = o;
			inRegister = false;
			register = "";
		}
		public MemoryUnit(String n, String s, int o, String r){
			name = n;
			scope = s;
			offset = o;
			inRegister = true;
			register = r;
		}
	}


	public MemoryManager(){
		stack = new StackManager();
		registers = new RegisterManager();
		units = new ArrayList<MemoryUnit>();
	}

	// Push new references to stack
	public void addReference(String name, String scope){
		units.add(new MemoryUnit(name, scope, stack.push(name)));
	}

	/*The last string returned in the array list will always be the register for the reference
	but there may be instructions in the prior entries to move values around*/
	public ArrayList<String> accessReference(String name, String scope){
		ArrayList<String> access = new ArrayList<String>();
		MemoryUnit reference = null;
		//Find reference
		for(MemoryUnit m : units){
			if(m.name.equals(name) && m.scope.equals(scope)){
				reference = m;
				break;
			}
		}
		if(reference == null){

			//Not found allocate new reference
			reference = new MemoryUnit(name, scope, stack.push(name));
			units.add(reference);
		}

		if(reference.inRegister){
			access.add(reference.register);
		} else {
			String alloced = registers.gpr_allocate();
			/*See if any other unit was using the allocated register*/
			for(MemoryUnit m : units){
				if(m.register.equals(alloced)){
					/*Move unit from register back to stack*/
					m.register = "";
					m.inRegister = false;
					access.add("movl " + alloced + ", -" + (4 * m.offset)+ "(%rbp)");
					break; /*Should only be one unit in a register, so save cycles*/
				}
			}
			/*Move value into register*/
			reference.register = alloced;
			reference.inRegister = true;
			access.add("movl -" + (4 * reference.offset)+ "(%rbp), " + alloced);
			access.add(reference.register);
		}

		return access;
	}

	// Grab a register by its name for a specific value
	public ArrayList<String> grabRegister(String name, String scope, String register){
		ArrayList<String> access = new ArrayList<String>();

		MemoryUnit reference = null;
		//Locate name, scope pair
		for(MemoryUnit m : units){
			if(m.name.equals(name) && m.scope.equals(scope)){
				reference = m;
			}
		}
		if(reference != null){
			if(reference.register.equals(register)){
				//Register is alrady held by reference!
				access.add(register);
				return access;
			}
		}

		//move contents of requested register to the stack
		for(MemoryUnit m : units){
			if(m.register.equals(register)){
				m.register = "";
				m.inRegister = false;
				access.add("movl " + register + ", -" + (4 * m.offset)+ "(%rbp)");

			}
		}

		if(reference == null){
			reference = new MemoryUnit(name, scope, stack.push(name), registers.allocateByName(register));
		} else {
			reference.register = register;
		}
		access.add("movl -" + (4 * reference.offset)+ "(%rbp), " + reference.register);
		access.add(register);

		return access;
	}

}

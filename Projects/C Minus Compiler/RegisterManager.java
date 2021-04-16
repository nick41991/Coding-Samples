//Manages Registers
import java.util.ArrayList;

public class RegisterManager
{
	// Defines register behaviors
	public class Register
	{
		public String name; //rax, rbx, etc
		public boolean reserved; // preserve important registers
		public int used;

		public Register(String n, boolean r){ // Should only be called by RegisterManager constructor!
			name = n;
			reserved = r;
			used = 0;
		}

		//Access a register value
		public void access(){
			used = 0;
		}
	}

	public ArrayList<Register> registers;

	public RegisterManager(){
		registers = new ArrayList<Register>();
		registers.add(new Register("%eax", false));
		registers.add(new Register("%ecx", false));
		registers.add(new Register("%edx", false));
		registers.add(new Register("%ebx", false));
		registers.add(new Register("%esi", false));
		registers.add(new Register("%edi", false));
		registers.add(new Register("%ebp", true));
		registers.add(new Register("%esp", true));
		registers.add(new Register("%r8d", false));
		registers.add(new Register("%r9d", false));
		registers.add(new Register("%r10d", false));
		registers.add(new Register("%r11d", true));
		registers.add(new Register("%r12d", false));
		registers.add(new Register("%r13d", false));
		registers.add(new Register("%r14d", false));
		registers.add(new Register("%r15d", false));
	}

	// Get the least recently used general purpose register
	public Register getLRU(){
		Register ret = null;
		int oldest = -1;
		for(Register r: registers){
			if(oldest < r.used && !r.reserved){ // strictly less than oldest enforces closest register to rax.
				oldest = r.used;
				ret = r;
			}
		}
		return ret;
	}

	// Gets a specific register
	public Register getByName(String name){
		for(Register r: registers){
			if(r.name.equals(name)){
				return r;
			}
		}
		return null;
	}

	// Increments register used values for LRU algorithm
	public void tick(){
		for(Register r: registers){
			r.used++;
		}
	}

	// Allocates a general purpose register
	public String gpr_allocate(){
		Register r = getLRU();
		tick();
		r.access();
		return r.name;
	}

	//Allocate register by name
	public String allocateByName(String name){
		Register r = getByName(name);
		tick();
		r.access();
		return r.name;
	}

	// Updates a value stored in a register
	public void update(String name, String value){
		Register r = getByName(name);
		tick();
		r.access();
	}

	// Accesses a value stored in a register
	public void access(String name){
		tick();
		getByName(name).access();
	}

}

//Manages Stack
import java.util.ArrayList;

public class StackManager
{
	public class Entry
	{
		public int offset;
		public String name;

		public Entry(String name, int o){
			offset = o;
		}
	}

	public ArrayList<Entry> stack;
	public int size;

	public StackManager(){
		stack = new ArrayList<Entry>();
		size = 1;
	}

	public int push(String s){
		stack.add(new Entry(s, size));
		size++;
		return size - 1;
	}

}

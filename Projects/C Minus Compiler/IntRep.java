/*Holds an Intermediate representation*/
import java.util.ArrayList;
import java.nio.file.*;
import java.io.*;

public class IntRep {

	public ArrayList<String> rep;

	public IntRep(){
		rep = new ArrayList<String>();
	}

	public IntRep(String fileName){
		rep = new ArrayList<String>();
		try {
			/*Reads file into ArrayList for reading in the file*/
			rep = (ArrayList<String>)Files.readAllLines(Paths.get(fileName));
		}
		catch(Exception e){
			System.out.println("Intermediate representation:  File " + fileName + " not found.");
			return;
		}
	}

	public void addLine(String line){
		rep.add(line);
	}

	/*Output to std out*/
	public void write(){
		for(String s: rep){
			System.out.println(s);
		}
	}

	/*Output IR to a given file*/
	// worked on by Alden and Daniel
	public void toFile(String filename){
		try {
		
      		File IR = new File(filename);
      		IR.createNewFile();
      		
      		
    	} catch (IOException e) {
      		System.out.println("Error: ");
      		e.printStackTrace();
    	}
    	
    	try {
      		FileWriter fWriter = new FileWriter(filename);
     
    		BufferedWriter writer = new BufferedWriter(fWriter);
    		
    		for(String s: rep){
    			writer.write(s + '\n');
				
			}
    		
    		writer.close();
      		System.out.println("Succesfuly created " + filename);
    	} catch (IOException e) {
      		System.out.println("An error occurred.");
      		e.printStackTrace();
    	}
	}

}

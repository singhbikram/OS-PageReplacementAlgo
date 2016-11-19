
/**
 * Memory Page for The Free Memory list
 *
 */
public class MemPage {
	boolean free;
	String processID;
	int refTime;
	
	public MemPage(String s){
		free = true;
		processID = s;
	}

}

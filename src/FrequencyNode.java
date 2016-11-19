

public class FrequencyNode {
	String processID;
	int frequency;
	
	public FrequencyNode(String s){
		processID = s;
		int frequency = 1;
	}
	
	public void addCount()
	{
		frequency++;
	}

}
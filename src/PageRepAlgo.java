
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Page Replacement Algorithms Simulator:
 * FIFO, LRU, LFU, MFU, and Random replacement algorithms
 * @author Bikram Singh, Ian Hunter, Aditya Shah, Vinay Ponnaganti, Karma Pandya 
 *
 */
public class PageRepAlgo {
	private static LinkedList<ProcessMem> jobQueue;
	private static LinkedList<ProcessMem> runableQueue;
	private static LinkedList<MemPage> memory;
	private static ProcessMem processMem;
	private static int time;
	private static int hit;
	private static int miss;
	private static LinkedList<FrequencyNode> frequencies;

	public static void main(String[] args) {
		frequencies = new LinkedList<FrequencyNode>();
		
		// create a process queue
		jobQueue = new LinkedList<ProcessMem>();
		runableQueue = new LinkedList<ProcessMem>();

		// create memory and disk memory of 100 pages
		memory = new LinkedList<MemPage>();
		for (int i = 1; i <= 100; i++) {
			memory.add(new MemPage(""));
		}
		
		//create empty Frequency table
		for (int i = 0; i < 150; i++) {
			frequencies.add(new FrequencyNode(""));
		}
		

		
		System.out.println("MName\tFree\tProcessID\tNrefTime");
		for (MemPage p : memory) {
			System.out.println(p.free+ " " + p.processID+ " "+p.refTime);
		}

		// create the 150 jobs
		for (int i = 1; i < 150; i++) {
			jobQueue.add(new ProcessMem(i));
		}

		// sort the process list by arrival time
		Collections.sort(jobQueue);

		// print sorted queue
		System.out.println("PName\t\tATime\t\tNpage");
		for (ProcessMem p : jobQueue) {
			System.out.println(p);
		}
		int memIndex = 0;
		
		//If memory pages have more than 4 pages free,
		//Add 25 jobs initially to the runnable queue
		while(NumberOfFreePages(memory) >= 4)
		{
			ProcessMem pMem = jobQueue.pop();
			pMem.remainingTime--;
			int addFour = 4;
			
			for(Page p : pMem.pageList){
				p.inMem = true;
//				p.refTime = time;
				memory.get(memIndex).refTime = time;
				memory.get(memIndex).free = false;
				memory.get(memIndex).processID = p.processID;
				memIndex++;
				addFour--;
				time++;
				if(addFour == 0)
					break;
			}
			runableQueue.add(pMem);
		}
		
		//create frequency table for LFU ONLY
		for (int i = 0; i < 100; i++) {
			String processIDVal = memory.get(i).processID;
			FrequencyNode n = new FrequencyNode(processIDVal);
			
			if(frequencies.contains(n))
			{
				int index = 0;
				index = frequencies.indexOf(n);
				frequencies.get(index).addCount();
			}
			else
			{
				frequencies.add(n);
			}
			
		}
		
		//Use for LFU only
		for(FrequencyNode n: frequencies)
		{
			//System.out.println("FreqNodeID: "+ n.processID);
		}
		
		System.out.println("MemPages\n\tFree\tProcessID\tNrefTime");
		for (MemPage p : memory) {
			System.out.println(""+p.free+ " \t" + p.processID+ " \t\t"+p.refTime);
		}

		// Run PAGE REPLACEMENT ALGORITHM SIMULATOR
		
		//set time 0-600: 60 seconds of 1/10 of second time slices
	    time = 0;
		while (time < 600) {// for 600
			
			System.out.println("time: "+time);
			if (NumberOfFreePages(memory) >= 4) {
				System.out.println("Adding Jobs to Runnable.");
				if (!jobQueue.isEmpty() && jobQueue.peek().arrival_time <= time) {
					ProcessMem pMem = jobQueue.peek();
					pMem.timeEnter=time;
					//add job to runnableQueue
					runableQueue.add(jobQueue.pop());
				}
			} // end if

			// Check Runnable Queue and allocate memory
			allocateProcessMemory();
			time++;
		} // end while
		
		System.out.println("\n\n----------------Page Replacement Alg. LRU DONE------------------\n");
		System.out.println("hit count:  "+ hit);
		System.out.println("miss count: "+ miss);
		double ratio = (double) hit/ (double) miss;
		System.out.println("Hit/miss ratio: "+ratio);
		

	}

	private static void allocateProcessMemory() throws NoSuchElementException {
		// TODO Auto-generated method stub
		// runableQueue, memory

		if (NumberOfFreePages(memory) < 4) {
			// run FIFO, LRU, LFU, MFU, Random
			
			//runFIFO();
			//System.out.println("FIFO run: ");
			
			
			runLRU();
			System.out.println("LRU run:");
			
			//runLFU();
			//System.out.println("LFU run:");
			
			//runMFU();
			//System.out.println("MFU run:");
			
			
			//runRandom();
			//System.out.println("Random run:");
		}

		else {
			// run FIFO normally
			if (NumberOfFreePages(memory) > 0 && !runableQueue.isEmpty()) {

				// pop value of runnable queue to start processing
				processMem = runableQueue.peekFirst();
				System.out.println("Peeked: " + processMem);

				int pSize = processMem.pSize;
				int randIndex = findProcessPageIndex(processMem);

				int memoryIndex = firstFreeMemoryIndex(memory);

				// Allocate memory from Free Page List
				System.out.println("Allocating Free page memory");
				memory.get(memoryIndex).free = false;
				memory.get(memoryIndex).processID = processMem.pageList.get(randIndex).processID;
				System.out.println("Used memoryPage : " + memoryIndex);
				
				//Now The Page Process is in Mem
				processMem.pageList.get(randIndex).inMem = true;
				
				//decrement the process remaining time;
				runProcess(processMem);

				// number of Free Process Memory
				System.out.println("Number of Free Pages: " + NumberOfFreePages(memory));


				//Pop Process off runable Queue
				if(processMem.isDone == true)
				{
					ProcessMem printProcess = runableQueue.pop();
					printProcess.timeExit = time;
					System.out.println("Job Completed: "+ printProcess.pName+ " "
							+ " time enter:"+printProcess.timeEnter 
							+ " time exit:"+printProcess.timeExit  
							+ " size:" + printProcess.pSize 
							+ " duration:"+printProcess.service_time);
					
				}

			}
		}

	}
	
	/**
	 * Finds the most frequently used algorithm and removes from Memory
	 */
	private static void runMFU() {
		// TODO Auto-generated method stub
		//Removes the page with the smallest frequency
		String removeThisPID = "";
		int first = -1;
		int counter = 0;
		String pIDStr = "";
		for(FrequencyNode n: frequencies)
		{
			pIDStr = n.processID;
			counter = 0;
			for(FrequencyNode node: frequencies)
			{
				if(node.processID.equals(pIDStr))
				{
					counter++;
				}
			}
			if(counter>first)
			{
				
				removeThisPID = pIDStr;
			}
		}
		System.out.println("Remove this PID: "+ removeThisPID);
		//remove a Page with this processID from Memory Page
		removeMFU(removeThisPID);
	}
	
	/**
	 * Remove the most frequently used Memory Page
	 * @param pid
	 */
	private static void removeMFU(String pid)
	{
		int index = 0;
		boolean notHit = false;
		for(int i = 0; i<100; i++)
		{
			if(memory.get(i).processID.equals(pid))
			{
				//remove from memory
				memory.get(i).free = true;
				memory.get(i).processID = "";
				index = i;
				notHit = true;
				//clearProcessIDMemory(pid);
				hit++;
				break;
			}
			miss++;
		}
		if(notHit) runFIFO();
		
		System.out.println("MFU free memory page: "+memory.get(index).processID);
	}
	
	/**
	 * Remove the Least Frequently Used Memory Page
	 */
	private static void runLFU() {
		//Removes the page with the smallest frequency
		String removeThisPID = "";
		int first = 10000;
		int counter = 0;
		String pIDStr = "";
		for(FrequencyNode n: frequencies)
		{
			pIDStr = n.processID;
			counter = 0;
			for(FrequencyNode node: frequencies)
			{
				if(node.processID.equals(pIDStr))
				{
					counter++;
				}
			}
			if(counter<first)
			{
				removeThisPID = pIDStr;
			}
		}
		System.out.println("Remove this PID: "+ removeThisPID);
		removePID(removeThisPID);
	}
	
	private static void removePID(String pid)
	{
		int index = 0;
		boolean notHit = false;
		for(int i = 99; i>=0; i--)
		{
			if(memory.get(i).processID.equals(pid))
			{
				//remove from memory
				memory.get(i).free = true;
				memory.get(i).processID = "";
				notHit = true;
				index = i;
				hit++;
				break;
			}
			miss++;
		}
		if(notHit) runFIFO();
		
		System.out.println("MFU free memory page: "+memory.get(index).processID);
	}

	/**
	 * Randomly find a Memory page to remove from memory
	 */
	private static void runRandom() {
		// TODO Auto-generated method stub
		boolean done = false;
		int value= 0;
		String processIDval = "";
		Random r = new Random();
		while(done == false)
		{
			value = r.nextInt(100);
			if(memory.get(value).free == false)
			{
				hit++;
				memory.get(value).free = true;
				processIDval = memory.get(value).processID;
				clearProcessIDMemory(processIDval);
				System.out.println("Random free memory page: "+memory.get(value).processID);
				done = true;
			}
			miss++;
		}		
	}

	private static void clearProcessIDMemory(String processIDval) {
		// TODO Auto-generated method stub
		boolean found = false;
	    ListIterator<ProcessMem> pMemList = runableQueue.listIterator();
		while(pMemList.hasNext())
		{
			ProcessMem pMem = pMemList.next();
			if(pMem.pName.equals(processIDval))
			{
				System.out.println("ProcessIDval found: "+processIDval);
				for(Page p: pMem.pageList)
				{
					if(p.inMem == true)
					{
						System.out.println("page cleared: "+p.processID);
						p.inMem = false;
						break;
					}
				}
				break;
			}
		}
	}

	
	
	private static int findMemorySlot(String processID)
	{
		for(int i = memory.size()-1; i >=0; i--)
		{
			if(processID.equals(memory.get(i).processID))
			{
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Remove the Least Recently Used page from Memory
	 */
	private static void runLRU() {
		int faultPage = 0;
		int timeDifference = 0;
		int first = -1;
		for(MemPage p: memory){
			timeDifference = time - p.refTime;
			if(timeDifference > first){
				first = p.refTime;
				faultPage = memory.indexOf(p);
				
			}
			miss++;
		}
		hit++;
		System.out.println("LRU free memory page: "+memory.get(faultPage).processID);
		memory.get(faultPage).free = true;
	}

	/**
	 * FIFO- remove the Page in memory that was first in (time-based)
	 */
	private static void runFIFO() {
		int faultPage = 0;
		int first = 1000000000;
		for(MemPage p: memory){
			if(p.refTime < first){
				first = p.refTime;
				faultPage = memory.indexOf(p);
				
			}
			miss++;
		}
		hit++;
		System.out.println("FIFO free memory page: "+memory.get(faultPage).processID);
		memory.get(faultPage).free = true;
	}

	/**
	 * Method counts the number of free pages.
	 * 
	 * @param Linked
	 *            list of memory
	 * @return number of free pages
	 */
	public static int NumberOfFreePages(LinkedList<MemPage> l) {
		int freeSpace = 0;
		for (MemPage m : l) {
			if (m.free)
				freeSpace++;
		}
		return freeSpace;
	}// end main

	/**
	 * Finds the first Free Memory index
	 * 
	 * @param pMem
	 * @return index first Free Memory Index
	 */
	public static int firstFreeMemoryIndex(LinkedList<MemPage> pMem) {
		ListIterator<MemPage> memIter = pMem.listIterator();
		int index = 0;
		while (memIter.hasNext()) {
			MemPage memPage = memIter.next();
			if (memPage.free == true) {
				return index;
			}
			index++;
		}
		return 0;

	}

	/**
	 * 
	 */
	public static int findProcessPageIndex(ProcessMem pMem) {
		int index = 1;
		int rand = 0;
		Random r = new Random();

		for (Page p : pMem.pageList) {
			if (p.inMem == false) {
				// check to see if the Process page needs memory
				index = 0;
			}
		}

		while (index == 0) {
			rand = r.nextInt(pMem.pageList.size());
			if (pMem.pageList.get(rand).inMem == false) {
				index = 1;
				//pMem.hit++;
				hit++;
				return rand;
			}
			//pMem.miss++;
			miss++;
		}

		return 0;
	}
	
	private static void runProcess(ProcessMem pMem)
	{
		pMem.remainingTime --;
		if(pMem.remainingTime <= 0)
		{
			for(MemPage m: memory)
			{
				if(m.processID.equals(pMem.pName))
				{
					m.free = true;
				}
			}
			pMem.isDone = true;
		}
		
	}

}

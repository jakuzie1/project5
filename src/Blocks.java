import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.text.SimpleDateFormat;


public class Blocks implements Comparable<Blocks>
{
	private int number;			// Block number
	private String miner;		// Miner address
	private long timestamp; 	// Unix timestamp
	private int transactionCount;	// Transaction count
	private static ArrayList<Blocks> blocks = null;
	private StringBuilder returnString = new StringBuilder();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss z");
	private Date date;		// date in the format of dateFormat
	private static ArrayList<Transaction> transactions;	// holds each transaction object that corresponds to a Block

	public Blocks()
	{
		returnString.append("Empty Block");
		transactions = null;
	}

	public Blocks(int number)
	{
		this.number = number;
		returnString.append("Block Number: " + number);
		transactions = null;
	}

	public Blocks(int number, String miner)
	{
		this.number = number;
		this.miner = miner;
		returnString.append("Block Number: " + number + " Miner Address: " + miner);
		transactions = null;
	}

	public Blocks(int number, String miner, long timestamp, int transactions) throws FileNotFoundException, IOException
	{
		this.number = number;
		this.miner = miner;
		this.timestamp = timestamp;
		this.transactionCount = transactions;
		returnString.append("Block Number: " + number + " Miner Address: " + miner);
		readTransactions("ethereumtransactions1.csv");
	}

	public int getNumber()
	{
		return this.number;
	}

	public String getMiner()
	{
		return this.miner;
	}

	public int getTransactionCount() {
		return this.transactionCount;
	}
	
	public ArrayList<Transaction> getTransactions() {
		return new ArrayList<>(transactions);
	}

	public static ArrayList<Blocks> getBlocks()
	{
		return new ArrayList<>(blocks);
	}
	
	
	// given an ArrayList of Blocks, find each unique miner address and
	// the frequency of times it appears and print according to output
	public static LinkedHashMap<String,Integer> calUniqMiners(int from, int to) throws FileNotFoundException, IOException
	{	
		LinkedHashMap<String,Integer> output = new LinkedHashMap<String,Integer>();
		
		// if blocks ArrayList has not been read, do so now
		if (blocks == null)
		{
			readFile("ethereumP1data.txt");
		}

		// initialize ArrayLists to store addresses and frequencies
		ArrayList<String> uniqMiners = new ArrayList<String>();
		ArrayList<Integer> uniqMinersFreq = new ArrayList<Integer>();
		// holds each miner address
		String miner;
		// loop through all Blocks
		for (int i = from; i < to; ++i)
		{
			miner = blocks.get(i).getMiner();
			// enter if the miner is new
			if (!(uniqMiners.contains(miner)))
			{
				// add the miner and add the frequency of 1
				uniqMiners.add(miner);
				uniqMinersFreq.add(1);
				output.put(miner,1);
			}
			// otherwise increment the frequency of that miner
			else
			{
				for (int j = 0; j < uniqMiners.size(); ++j)
				{
					if (uniqMiners.get(j).equals(miner))
					{
						uniqMinersFreq.set(j, uniqMinersFreq.get(j) + 1);
						output.put(miner, output.get(miner) + 1);
					}
				}
			}
		}
		
		return output;

		/*
		 * // print according to output System.out.println("Number of unique Miners: " +
		 * uniqMiners.size() + "\n");
		 * System.out.println("Each unique Miner and its frequency:"); for (int i = 0; i
		 * < uniqMiners.size(); ++i) { System.out.println("Miner Address: " +
		 * uniqMiners.get(i) + "\nMiner Frequency: " + uniqMinersFreq.get(i) + "\n"); }
		 */
		
	}

	// calculate the difference in the block numbers of two blocks
	public static int blockDiff(Blocks minuend, Blocks subtrahend)
	{
		int diff = minuend.getNumber() - subtrahend.getNumber();

		return diff;
	}

	// given the Block number retrieve the Blocks object that corresponds to that number from blocks ArrayList and return it
	public static Blocks getBlockByNumber(int num) throws FileNotFoundException, IOException {

		if(blocks == null) {
			Blocks.readFile("ethereumP1data.txt");
		}

		for(int i = 0; i < blocks.size(); ++i) {
			if (blocks.get(i).getNumber() == num) {
				return blocks.get(i);
			}
		}

		return null;
	}

	public String toString()
	{
		return returnString.toString();
	}

	// reads a file of given filename and returns an ArrayList of Blocks
	public static ArrayList<Blocks> readFile(String filename) throws FileNotFoundException, IOException 
	{
		// construct a file object for the file with the given name.
		File file = new File(filename);

		// construct a scanner to read the file.
		Scanner fileScanner = new Scanner(file);

		// blocks ArrayList to store Blocks objects
		ArrayList<Blocks> b = new ArrayList<Blocks>();

		// create the Array that will store each lines data so we can grab the IDs
		String[] fileData = null;

		// Store each line of the file into the ArrayList.
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();

			// split each line along the commas
			fileData = line.trim().split(",");

			// fileData[0] corresponds to block number, fileData[9] to miner address
			// fileData[16] corresponds to unix timestamp, fileData[17] corresponds to transaction count
			b.add(new Blocks(Integer.parseInt(fileData[0]), fileData[9], Integer.parseInt(fileData[16]), Integer.parseInt(fileData[17])));
		}

		fileScanner.close();

		blocks = new ArrayList<>(b);

		return b;
	}

	// sort the blocks ArrayList in ascending order based on block number
	public static void sortBlocksByNumber() throws FileNotFoundException, IOException {
		if (blocks==null) {
			readFile("ethereumP1.txt");
		}

		Collections.sort(blocks);
	}

	@Override
	public int compareTo(Blocks b) {
		Integer x = number;
		Integer y = b.getNumber();
		return x.compareTo(y);
	}

	// print the date with the correct format
	public String getDate() {
		// initialize date in milliseconds
		date = new Date(timestamp * 1000);
		dateFormat.setTimeZone(TimeZone.getTimeZone("CST"));
		return dateFormat.format(date);
	}

	// find the difference in time between two given Blocks in hours, minutes, and seconds
	public static int[] timeDiff(Blocks first, Blocks second) {
		//make sure given Blocks aren't null
		int output[] = new int[3];
		
		if ((first == null) || (second == null)) {
			System.out.println("A given Block is null.");
			return null;
		}
		else {
			String hours = " hours, ";
			String minutes = " minutes, and ";
			String seconds = " seconds.";
			// use timestamps to find hours, minutes, seconds
			int diffInSeconds = (int) Math.abs(first.timestamp - second.timestamp);
			int diffInMinutes = diffInSeconds / 60;
			int diffInHours = diffInMinutes / 60;
			diffInSeconds = diffInSeconds % 60;
			diffInMinutes = diffInMinutes % 60;

			if (diffInHours == 1) {
				hours = " hour, ";
			}
			if (diffInMinutes == 1) {
				minutes = " minute, and ";
			}
			if (diffInSeconds == 1) {
				seconds = " second.";
			}
			
			output[0] = diffInHours;
			output[1] = diffInMinutes;
			output[2] = diffInHours;

			System.out.println("The difference in time between Block " + first.getNumber() + " and Block " + second.getNumber() + " is "
					+ diffInHours + hours + diffInMinutes + minutes + diffInSeconds + seconds);
			
			return output;
		}
	}

	// return the number of transactions between two Blocks not inclusive
	// return -1 if the Blocks are null/not in the blocks ArrayList
	// or if second is before first in the ArrayList
	public static int transactionDiff(Blocks first, Blocks second) throws FileNotFoundException, IOException {

		// if blocks ArrayList has not been read, do so now and sort it
		if (blocks == null)
		{
			readFile("ethereumP1data.txt");
			sortBlocksByNumber();
		}

		// make sure given Blocks aren't null
		if ((first == null) || (second == null)) {
			return -1;
		}

		int indexA = -1;		// index of first in blocks ArrayList
		int indexB = -1;		// index of second in blocks ArrayList
		int count = 0;			// number of transactions between the two Blocks


		// for loop to find indexA and indexB
		for (int i = 0; i < blocks.size(); ++i) {
			if (first.getNumber() == blocks.get(i).getNumber()) {
				indexA = i;
			}
			if (second.getNumber() == blocks.get(i).getNumber()) {
				indexB = i;
			}
		}

		// make sure first and second are elements of blocks
		if ((indexA < 0) || (indexB < 0)) {
			return -1;
		}
		// make sure first comes before second
		if (indexA >= indexB) {
			return -1;
		}

		// for loop to count the transactions
		for (int i = indexA+1; i < indexB; ++i) {
			count += blocks.get(i).getTransactionCount();
		}

		return count;
	}

	private void readTransactions(String filename) throws FileNotFoundException, IOException 
	{
		// construct a file object for the file with the given name.
		File file = new File(filename);

		// construct a scanner to read the file.
		Scanner fileScanner = new Scanner(file);

		// blocks ArrayList to store Blocks objects
		Set<Transaction> b = new TreeSet<Transaction>();

		// create the Array that will store each lines data so we can grab the IDs
		String[] fileData = null;

		// Store each line of the file into the ArrayList.
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();

			// split each line along the commas
			fileData = line.trim().split(",");

			
			// if the transaction is for this block, add it
			if (Integer.parseInt(fileData[3]) == this.number) {
				// fileData[3] corresponds to block number, fileData[4] to index, fileData[8] to gasLimit,
				// fileData[9] to gasPrice, fileData[5] to fromAddress, fileData[6] to toAddress
				b.add(new Transaction(Integer.parseInt(fileData[3]), Integer.parseInt(fileData[4]), Integer.parseInt(fileData[8]), Double.valueOf(fileData[9]).longValue(), fileData[5], fileData[6]));
			}
		}

		fileScanner.close();

		transactions = new ArrayList<>(b);
	}
	
	public static LinkedHashMap<Integer,Double> avgTransactionCost() {
		double avg = 0.0;
		LinkedHashMap<Integer,Double> output = new LinkedHashMap<Integer,Double>();
		
		for (int j = 0; j < blocks.size(); j++) {
			// loop over every transaction for this block
			for (int i = 0; i < transactions.size(); ++i) {
				// add the cost of each transaction to avg
				avg += (transactions.get(i).transactionCost());
			}
			// divide avg by the amount of transactions to find the average cost
			avg = avg / (double) transactions.size();
			output.put(blocks.get(j).getNumber(), avg);
		}
		
		return output;
	}
	
	public void uniqFromTo() {
		ArrayList<String> from = new ArrayList<>();				// holds unique from addresses
		ArrayList<ArrayList<String>> to = new ArrayList<>();	// holds ArrayLists that hold each to address
		ArrayList<Double> cost = new ArrayList<>();
		double total = 0.0;
		
		String address;	// holds address 
		
		// first find each unique from address
		for (int i = 0; i < this.transactionCount; ++i) {
			address = transactions.get(i).getFromAddress();
			// add the address if it is not already there
			if (!(from.contains(address))) {
				from.add(address);
			}
		}
		
		// then fill each ArrayList with unique to addresses
		// loop over from ArrayList 
		for (int i = 0; i < from.size(); ++i) {
			// initialize ArrayList
			ArrayList<String> addr = new ArrayList<String>();
			// loop over all transactions
			for (int j = 0; j < this.transactionCount; ++j) {
				// enter if the transactions from address matches the unique from address
				if (from.get(i).equals(transactions.get(j).getFromAddress())) {
					// add to address to the ArrayList in index i of to
					addr.add(transactions.get(j).getToAddress());
					// add the cost of this transaction to total
					total += transactions.get(j).transactionCost();
				}
			}
			// add ArrayList to ArrayList 
			to.add(addr);
			// add total to cost ArrayList
			cost.add(total);
			// reset total
			total = 0.0;
		}
		
		// visualize which address sent to another unique address "0x___ -> 0x___"
		System.out.println("Each transaction by from address for Block " + this.number + ":\n");
		for (int i = 0; i < from.size(); ++i) {
			System.out.println("From " + from.get(i)); // + " : " + cost.get(i) + " ETH");
			
			Iterator<String> itr = to.get(i).iterator();
			while (itr.hasNext()) {
				System.out.println(" -> " + itr.next());
			}
			// print to 8 decimal points
			System.out.printf("Total cost of transactions: %.8f ETH\n", cost.get(i));
			System.out.println();
			
			
			
		}
		
		
	}
	
	
	
	

}

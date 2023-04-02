import java.io.*;
import java.net.*; 

/* 
 * Student Name: Maximus Moessis
 * Student ID: 47083581
 * 
 * This client contains print statements that give additional information such as 
 * which server is the largest, how many cores it has, and the number of those servers.
 * Young Lee has requested the print statements be commented out for ease of marking, however
 * I have left in the exception printers.  
 */

public class LRR {  
	public static void main(String[] args) {  
		try {      
			Socket s = new Socket("localhost", 50000);  
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));  
			
			/* Temp string used to read the BufferedReader Stream.
			 * Only used when a message actually needs to be accessed and
			 * not just read, since assigning it to the received message
			 * every time would be an unnecessary memory allocation.
			 */
			String str; 
		

			/* Initial Setup */

			sendMsg("HELO", dout); 
			recMsg(dis);
			String username = System.getProperty("user.name"); 
			sendMsg("AUTH "+ username, dout);
			recMsg(dis);
			sendMsg("REDY", dout);
			/* To exactly mimick DS-client, the client must keep a record of the first job
			 * so that it doesn't have to re-request it after the GETS information is retrieved. 
			 */
			String firstJob = recMsg(dis); 
			String[] firstSplit = firstJob.split(" ");


			/* Obtaining the Number of Servers */

			sendMsg("GETS All", dout);
			str = recMsg(dis);
			String[] strSplit = str.split(" ");
			
			int numOfServers = Integer.parseInt(strSplit[1]);
			
			sendMsg("OK", dout);
			
			int LSCores = 0;
			String serverName = ""; // Name of the largest server
			String[] serverNames = new String[numOfServers]; // Array containing all the server names.


			/* Finding the largest server type and initialising array of server names */

	//		System.out.println("\n Server List: \n");
			for (int i = 0; i < numOfServers; i++) {
				str = recMsg(dis);
				strSplit = str.split(" ");

				serverNames[i] = strSplit[0]; // initialising an array of the server names
				
				if (Integer.parseInt(strSplit[4]) > LSCores) {
					LSCores = Integer.parseInt(strSplit[4]);
					serverName = strSplit[0];
				}
			}

			/* Finding how many of the largest server type there are */

			int numOfLargest = 0; // amount of the largest server type.

			/* Every time the largest server name is found in the list of servers, increment the amount */
			for(int i = 0; i < numOfServers; i++) {
				if (serverNames[i].equals(serverName)) numOfLargest++;
			}

			sendMsg("OK", dout); 

	//		System.out.println("\n The Largest Server is "+serverName+" with "+LSCores+" Cores.\n");
	//		System.out.println(" There are also "+numOfLargest+" of this type.\n");

			/* The start of the Largest Round Robin algorithm */

			int rr = 0; // Used to create round robin fashion (explained more inside loop).
			str = "";

			/* Scheduling the saved 'firstJob' */
			if (recMsg(dis).equals(".")) { // receive the dot message after "OK" from the server listing.
				sendMsg("SCHD "+firstSplit[2]+" "+serverName+" "+rr%numOfLargest, dout);
				rr++;
			}

			while (!str.equals("NONE")) {  // Stop scheduling when there are no more jobs to schedule. 
				recMsg(dis);
				
				sendMsg("REDY", dout);
				str = recMsg(dis);
				strSplit = str.split(" ");

				while (strSplit[0].equals("JCPL")) { // check if it's JCPL
					sendMsg("REDY", dout);
					str = recMsg(dis);
					strSplit = str.split(" ");
				}
				
				/* RR fashion is achieved by modding the 'rr' variable by the number of largest servers.
				 * rr is then incremented after every SCHD, creating this round robin fashion. 
				 * Once the loop is completed, this 'rr' variable also contains the total amount of jobs scheduled.
				 */
				if (strSplit[0].equals("JOBN")) { // if received JOBN then schedule the job. 
					sendMsg("SCHD "+strSplit[2]+" "+serverName+" "+rr%numOfLargest, dout);
					rr++;
				}

			}
		//	System.out.println("\n The total amount of jobs scheduled was "+rr);

			// Then close the connection
			closeCon(dis, dout, s);

		} catch(Exception e) { 
			System.out.println(e); 
		}
	}  


	/* HELPER FUNCTIONS */

	/* 
	 * Sends a message through the data output stream dout
	 * Automatically:
	 * 		Puts end of line character \n 
	 * 		Flushes()
	 * 		Prints to console
	 */
	static void sendMsg(String msg, DataOutputStream inDout) throws IOException {

		inDout.write((msg+"\n").getBytes());
		inDout.flush();

//		System.out.println("Client says: "+msg);
	}

	/*
	 * Receives a message through the BufferedReader dis.
	 * Automatically:
	 * 		Reads the line and prints to console
	 * 		Returns the line read in a string.
	 */
	static String recMsg(BufferedReader inDis) throws IOException {

		String inStr = (String)inDis.readLine();
//		System.out.println("Server says: "+inStr);

		return inStr;
	}

	/* 
	 * Closes the connection to the socket as well as sending a message to quit.
	 * Returns true if successfully closed. 
	 */

	static void closeCon(BufferedReader inDis, DataOutputStream inDout, Socket inS) throws IOException {
		try {
			sendMsg("QUIT", inDout);			
			recMsg(inDis);
			inDout.close();  
			inDis.close();
			inS.close(); 

//			System.out.println("Connection Closed!");
		} catch (Exception e) {
			System.out.println(e);

//			System.out.println("Connection Failed to Close!");;
		}
	}
}  

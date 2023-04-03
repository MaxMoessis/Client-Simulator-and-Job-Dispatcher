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
			/* 
			 * To exactly mimick DS-client, the client must keep a record of the first job
			 * so that it doesn't have to re-request it after the GETS information is retrieved. 
			 */
			String firstJob = recMsg(dis); 
			String[] firstSplit = firstJob.split(" ");

			/*
			 * In order to have absolutely no magic numbers, I have delcared variables 
			 * for the positions of specific information inside the split strings.
			 */

			 short NOS = 1;     // Number of servers displayed during GETS All
			 short SN = 0;      // The server name
			 short cores = 4;   // Amount of cores in the largest server.
			 short comType = 0; // When checking for NONE, SCHD, JCPL, etc
			 short jobID = 2;   // Which Job ID to schedule.


			/* Obtaining the Number of Servers */

			sendMsg("GETS All", dout);
			str = recMsg(dis);
			String[] strSplit = str.split(" ");
			
			int numOfServers = Integer.parseInt(strSplit[NOS]);
			
			sendMsg("OK", dout);
			

			/* Finding the largest server type and initialising array of server names */

			int LSCores = 0; // Number of cores in the largest server type.
			String largestServer = ""; // Name of the largest server type.
			String[] serverNames = new String[numOfServers]; // Array containing all the server type names.

	//		System.out.println("\n Server List: \n");
			for (int i = 0; i < numOfServers; i++) {
				str = recMsg(dis);
				strSplit = str.split(" ");

				serverNames[i] = strSplit[SN]; // initialising an array of the server names
				
				if (Integer.parseInt(strSplit[4]) > LSCores) {
					LSCores = Integer.parseInt(strSplit[cores]);
					largestServer = strSplit[SN];  // the final allocated name to 'largestServer' will be the largest server.
				}
			}

			/* Finding how many of the largest server type there are */

			int numOfLargest = 0; // amount of the largest server type.

			/* Every time the largest server name is found in the list of servers, increment the amount */
			for(int i = 0; i < numOfServers; i++) {
				if (serverNames[i].equals(largestServer)) numOfLargest++;
			}

			sendMsg("OK", dout); 

	//		System.out.println("\n The Largest Server is "+largestServer+" with "+LSCores+" Cores.\n");
	//		System.out.println(" There are also "+numOfLargest+" of this type.\n");

			/* The start of the Largest Round Robin algorithm */

			int rr = 0; // Used to create round robin fashion (explained more inside loop).
			str = "";

			/* Schedule the saved 'firstJob' */
			if (recMsg(dis).equals(".")) { // receive the dot message after "OK" from the server listing.
				sendMsg("SCHD "+firstSplit[jobID]+" "+largestServer+" "+rr%numOfLargest, dout);
				rr++;
			}
			/* Schedule the rest of the jobs */
			while (!str.equals("NONE")) {  // Stop scheduling when there are no more jobs to schedule. 
				recMsg(dis);
				
				sendMsg("REDY", dout);
				str = recMsg(dis);
				strSplit = str.split(" ");

				while (strSplit[comType].equals("JCPL")) { // check if it's JCPL
					sendMsg("REDY", dout);
					str = recMsg(dis);
					strSplit = str.split(" ");
				}
				
				/* RR fashion is achieved by modding the 'rr' variable by the number of largest servers.
				 * rr is then incremented after every SCHD, creating this round robin fashion. 
				 * Once the loop is completed, this 'rr' variable also contains the total amount of jobs scheduled.
				 */
				if (strSplit[comType].equals("JOBN")) { // if received JOBN then schedule the job. 
					sendMsg("SCHD "+strSplit[jobID]+" "+largestServer+" "+rr%numOfLargest, dout);
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

//			System.out.println("Connection Closed");
		} catch (Exception e) {
			System.out.println(e);

//			System.out.println("Connection Failed to Close!");;
		}
	}
}  

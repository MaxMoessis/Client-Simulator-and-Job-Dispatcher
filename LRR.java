import java.io.*;
import java.net.*; 
 
public class LRR {  
	public static void main(String[] args) {  
		try {      
			Socket s = new Socket("localhost", 50000);  
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));  
			
			String str; // String used to read the BufferedReader Stream.

			sendMsg("HELO", dout); 
		
			str = recMsg(dis);
			
			String username = System.getProperty("user.name"); 
	
			sendMsg("AUTH "+ username, dout);

			str = recMsg(dis);

			sendMsg("REDY", dout);
			
			str = recMsg(dis);
			
			sendMsg("GETS All", dout);
			
			str = recMsg(dis);

			String[] strSplit = str.split(" ");
			
			int numOfServers = Integer.parseInt(strSplit[1]);
			
			sendMsg("OK", dout);
			
			int LSCores = 0;
			String serverName = "";
			
			System.out.println("\n Server List: \n");
			for (int i = 0; i < numOfServers; i++) {
				str = recMsg(dis);
				strSplit = str.split(" ");
				
				if (Integer.parseInt(strSplit[4]) > LSCores) {
					LSCores = Integer.parseInt(strSplit[4]);
					serverName = strSplit[0];
				}
			}

			int numOfLargest = 0; // Now to find out how many of that server type are present.

			sendMsg("OK", dout);
			str = recMsg(dis);
			sendMsg("GETS All", dout);
			str = recMsg(dis);
			sendMsg("OK", dout);

			for(int i = 0; i < numOfServers; i++) {
				str = recMsg(dis);
				strSplit = str.split(" ");

				if (strSplit[0].equals(serverName)) numOfLargest++;
			}

			sendMsg("OK", dout); 
			str = recMsg(dis);
			
			// This will return the amount of the largest server types. 

			System.out.println("\n The Largest Server is "+serverName+" with "+LSCores+" Cores.\n");
			System.out.println(" There are also "+numOfLargest+" of this type.\n");
			
			sendMsg("QUIT", dout);
			
			str = recMsg(dis);
			
			dout.close();  
			dis.close();
			s.close();  
		} catch(Exception e) { System.out.println(e); }  
	}  

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

		System.out.println("Client says: "+msg);
	}

	/*
	 * Receives a message through the BufferedReader dis.
	 * Automatically:
	 * 		Reads the line and prints to console
	 * 		Returns the line read in a string.
	 */
	static String recMsg(BufferedReader inDis) throws IOException {

		String inStr = (String)inDis.readLine();
		System.out.println("Server says: "+inStr);

		return inStr;
	}
}  


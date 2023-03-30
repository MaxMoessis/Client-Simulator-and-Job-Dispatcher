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
			int LSid = 0;
			
			System.out.println("Server:\n");
			for (int i = 0; i < numOfServers; i++) {
				str = recMsg(dis);
				strSplit = str.split(" ");
				
				if (Integer.parseInt(strSplit[4]) > LSCores) {
					LSCores = Integer.parseInt(strSplit[4]);
					LSid = Integer.parseInt(strSplit[1]);
				}
			}
			
			System.out.println("\n The Largest Server is "+LSid+"(id) with "+LSCores+" Cores.\n");
			
			sendMsg("OK", dout);
			
			str = recMsg(dis);
			
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
	static void sendMsg(String msg, DataOutputStream inDout) {

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
	static String recMsg(BufferedReader inDis) {

		String inStr = (String)inDis.readLine();
		System.out.println("Server says: "+inStr);

		return inStr;
	}
}  

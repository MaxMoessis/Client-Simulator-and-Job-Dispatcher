import java.io.*;
import java.lang.annotation.Documented;
import java.net.*; 
 
public class LRR {  
	public static void main(String[] args) {  
		try {      
			Socket s = new Socket("localhost", 50000);  
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));  
			
			String str; // String used to read the BufferedReader Stream.

			// System.out.println("Client: HELO\n");
			// dout.write(("HELO\n").getBytes());  
			// dout.flush(); 
			dout.sendMsg("HELO"); 
		
			// str = (String)dis.readLine();
			// System.out.println("Server: " + str);
			str = dis.recMsg();
			
			String username = System.getProperty("user.name"); 
			
			// System.out.println("Client: AUTH pooch\n");
			// dout.write(("AUTH "+ username +"\n").getBytes());
			// dout.flush();
			dout.sendMsg("AUTH "+ username);
			
			// str = dis.readLine();
			// System.out.println("Server: " + str);
			str = dis.recMsg();
			
			// System.out.println("Client: REDY\n");
			// dout.write(("REDY\n").getBytes());
			// dout.flush();
			dout.sendMsg("REDY");
			
			// str = dis.readLine();
			// System.out.println("Server: " + str);
			str = dis.recMsg();
			
			// System.out.println("Client: GETS All\n");
			// dout.write(("GETS All\n").getBytes());
			// dout.flush();
			dout.sendMsg("GETS All");
			
			// str = dis.readLine(); 
			str = dis.recMsg();

			String[] strSplit = str.split(" ");
			
			int numOfServers = Integer.parseInt(strSplit[1]);
			
			// System.out.println("Client: OK\n");
			// dout.write(("OK\n").getBytes());
			// dout.flush();
			dout.sendMsg("OK");
			
			int LSCores = 0;
			int LSid = 0;
			
			System.out.println("Server: \n");
			for (int i = 0; i < numOfServers; i++) {
				// str = dis.readLine(); 
				// System.out.println(str);
				str = dis.recMsg();
				
				strSplit = str.split(" ");
				
		// Loop that finds the largest server (amount of cores & its id)
				
				if (Integer.parseInt(strSplit[4]) > LSCores) {
					LSCores = Integer.parseInt(strSplit[4]);
					LSid = Integer.parseInt(strSplit[1]);
				}
			}
			
			System.out.println("\n The Largest Server is "+LSid+"(id) with "+LSCores+" Cores.\n");
			
			// System.out.println("Client: OK\n");
			// dout.write(("OK\n").getBytes());
			// dout.flush();
			dout.sendMsg("OK");
			
			// str = dis.readLine();
			// System.out.println("Server: " + str);
			str = dis.recMsg();
			
			// System.out.println("Client: QUIT\n");
			// dout.write(("QUIT\n").getBytes());
			// dout.flush();
			dout.sendMsg("QUIT");
			
			// str = dis.readLine();
			// System.out.println("Server: " + str +"\n");
			str = dis.recMsg();
			
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
	public void sendMsg(String msg) {
		this.write((msg+"\n").getBytes());
		this.flush();

		System.out.println("Client says: "+msg);
	}

	/*
	 * Receives a message through the BufferedReader dis.
	 * Automatically:
	 * 		Puts end of line character \n
	 * 		Prints to console
	 */
	public String recMsg() {
		String inStr = (String)this.readLine();
		System.out.println("Server says: "+inStr);

		return inStr;
	}
}  

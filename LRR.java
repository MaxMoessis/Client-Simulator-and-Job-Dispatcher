import java.io.*;  
import java.net.*; 

public class LRR {  
	public static void main(String[] args) {  
		try {      
			Socket s = new Socket("localhost", 50000);  
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));  

			String str; // String used to read the BufferedReader Stream.

			System.out.println("Client: HELO\n");
			dout.write(("HELO\n").getBytes());  
			dout.flush();  

			str = (String)dis.readLine();
			System.out.println("Server: " + str);

			String username = System.getProperty("user.name"); 

			System.out.println("Client: AUTH pooch\n");
			dout.write(("AUTH "+ username +"\n").getBytes());
			dout.flush();

			str = dis.readLine();
			System.out.println("Server: " + str);

			System.out.println("Client: REDY\n");
			dout.write(("REDY\n").getBytes());
			dout.flush();

			str = dis.readLine();
			System.out.println("Server: " + str);

			System.out.println("Client: GETS All\n");
			dout.write(("GETS All\n").getBytes());
			dout.flush();

			str = dis.readLine(); 
			String[] strSplit = str.split(" ");

			int numOfServers = Integer.parseInt(strSplit[1]);

			System.out.println("Client: OK\n");
			dout.write(("OK\n").getBytes());
			dout.flush();

			int LSCores = 0;
			int LSid = 0;

			System.out.println("Server: \n");
			for (int i = 0; i < numOfServers; i++) {
				str = dis.readLine(); 
				System.out.println(str);

				strSplit = str.split(" ");

		// Loop that finds the largest server (amount of cores & its id)

				if (Integer.parseInt(strSplit[4]) > LSCores) {
					LSCores = Integer.parseInt(strSplit[4]);
					LSid = Integer.parseInt(strSplit[1]);
				}
			}

			System.out.println("\n The Largest Server is "+LSid+"(id) with "+LSCores+" Cores.\n");

			System.out.println("Client: OK\n");
			dout.write(("OK\n").getBytes());
			dout.flush();

			str = dis.readLine();
			System.out.println("Server: " + str);

			System.out.println("Client: QUIT\n");
			dout.write(("QUIT\n").getBytes());
			dout.flush();

			str = dis.readLine();
			System.out.println("Server: " + str +"\n");

			dout.close();  
			dis.close();
			s.close();  
		} catch(Exception e) { System.out.println(e); }  
	}  
}

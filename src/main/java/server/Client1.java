package main.java.server;

import java.io.*;
import java.net.Socket;

public class Client1 {
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	
	public static void main(String[] args) {
		try {
			Socket s = new Socket("40.127.162.43", 12345);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			
//			PrintWriter out  = new PrintWriter(s.getOutputStream());
//	        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));  
//			out.println("Hej");
//			out.flush();
//			String recieved = in.readLine();
//			System.out.println(recieved);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}

/*
 * Author : Navin Sethi
 * ID : 169086962
 * Date : 2026/01/21
 * Description : Handles Clients once thread is created
 * 
 */

package server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


// this class is a client in a current connection to the server
public class ClientHandler extends Thread {
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (IOException e) {
			
		}
	}
	
	public void post(String postmsg) {
		out.print(postmsg);
	}
	
	public String get() {
		String line = "";
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return line;
	}
	
	// this is the only thing being ran and will continue to be ran in order to keep connection,
	// put all client communication related external function in here
	public void run() {
		try {
			
			
			String command;
			while((command = in.readLine()) != null) {

			}
		} catch (IOException e) {
			System.err.print("Error in reciving message");
		}
	}
	

}
 
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
import java.nio.charset.StandardCharsets;

import server.gui.Board;

// this class is a client in a current connection to the server
public class ClientHandler extends Thread {
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Board board;
	private String handshake;
	private String IP;
	
	public ClientHandler(Socket socket, Board board) {
		this.socket = socket;
		this.board = board;
		IP = socket.getInetAddress().getHostAddress();
		
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
		} catch (IOException e) {
 
		}
		
	}
	
	public void send(String postmsg) {
		out.write(postmsg);
		out.flush();
	}
	
	public String recv() {
		try {
			String retstring = new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			return retstring;
		} catch (IOException e) {
			String retstring = new String("ERROR There was an error in reciving a message from the server.");
			return retstring;
		}
	}
	
	// this is the only thing being ran and will continue to be ran in order to keep connection,
	// put all client communication related external function in here
	public void run() {
		
		String handshake = board.getConfigString();

		out.println(handshake);
		
		String command;
		while(socket.isConnected()) {
			try {
				command = in.readLine();
				System.out.println(command);
				processCommand(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void processCommand(String command) {
		String[] parts = command.split(" ", 2);
		String cmd = parts[0].toUpperCase();
		int x;
		int y;
		String returnMessage;
		
		switch(cmd) {
		case "NOTE":
			x = Integer.parseInt(parts[1]);
			y = Integer.parseInt(parts[2]);
			String color = parts[3];
			String message = parts[4];
			returnMessage = board.POSTnote(x, y, color, message);
			send(returnMessage);
			break;
		case "PIN":
			x = Integer.parseInt(parts[1]);
			y = Integer.parseInt(parts[2]);
			returnMessage = board.addPin(x, y);
			send(returnMessage);
			break;
		case "GET":
			StringBuilder getmsg = new StringBuilder();
			getmsg.append(board.getAllNotes());
			getmsg.append(board.getAllPins());
			send(getmsg.toString());
			break;
		case "DISCONNECT":
			try {
				socket.close();
				System.out.println(parts[1]+" has disconnected from IP : "+ IP + "\nThread "+threadId());
				interrupt();
			} catch (IOException e) {
				System.out.println("Failed to disconnect user : " +IP);
			}
			break;
		}
	}

}
 
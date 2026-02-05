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
		out.println(postmsg);
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
		
		try {
		String handshake = board.getConfigString();

		out.println(handshake);
		
		String command;
		while((command = in.readLine()) != null) {
				System.out.println(command);
				processCommand(command);
				
		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processCommand(String command) {
		if (command == null || command.trim().isEmpty()) {
            return;
        }
		String[] parts = command.split(" ");
		String cmd = parts[0].toUpperCase();
		int x;
		int y;
		String returnMessage;
		
		try{
			switch(cmd) {
				case "NOTE":
					System.out.println("GOT MESSAGE : NOTE");
					if (parts.length < 4) return;
					x = Integer.parseInt(parts[1]);
					y = Integer.parseInt(parts[2]);
					String color = parts[3];
					StringBuilder message = new StringBuilder();
					for (int i = 4; i < parts.length; i++) {
                		message.append(parts[i]);
						// Adds space unless its the last word
                		if (i < parts.length - 1) {
                    		message.append(" ");
                		}
            		}
					returnMessage = board.POSTnote(x, y, color, message.toString());
					System.out.println("RETURNING MESSAGE : "+ returnMessage);
					send(returnMessage);
					break;
				case "PIN":
					x = Integer.parseInt(parts[1]);
					y = Integer.parseInt(parts[2]);
					returnMessage = board.addPin(x, y);
					send(returnMessage);
					break;
				case "UNPIN":
					x = Integer.parseInt(parts[1]);
					y = Integer.parseInt(parts[2]);
					returnMessage = board.removePin(x, y);
					send(returnMessage);
					break;
				case "GET":
					System.out.println("GOT MESSAGE : GET");
					StringBuilder getmsg = new StringBuilder();
					getmsg.append(board.getAllNotes());
					getmsg.append(board.getAllPins());
					System.out.println("SENDING RESPONSE");
					send(getmsg.toString());
					System.out.println("SENT RESPONSE");
					break;
				case "CLEAR":
					returnMessage = board.clear();
					send(returnMessage);
					break;
				case "SHAKE":
					returnMessage = board.shake();
					send(returnMessage);
					break;
				case "DISCONNECT":
					socket.close();
					System.out.println(parts[1]+" has disconnected from IP : "+ IP + "\nThread "+threadId());
					interrupt();
				default:
					send("ERROR_UNKNOWN_COMMAND");
					break;
			}
		} catch (IOException e) {
			System.err.println("ERROR_PROCESSING_COMMAND: " + command);
			e.printStackTrace();
			send("ERROR_SERVER_EXCEPTION");
		}
		
	}
			
}




 
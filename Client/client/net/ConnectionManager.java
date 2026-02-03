package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import client.gui.ClientGUI;
import client.gui.Note;

public class ConnectionManager extends Thread{
	
	private Socket socket;
	private String alias;
	boolean connectionStatus = false;
	private BufferedReader read;
	private PrintWriter write;
	private String handshake;
	
	
	public void connectToServer (String ip, int port, String alias) {
		this.alias = alias;
			try {
			
			socket = new Socket(ip, port);
			connectionStatus = socket.isConnected();
			
		}catch(UnknownHostException e1) {
			ClientGUI.guiError("ERROR : The host your trying to connect to does not exist."
							+ "assure that the IP address and the PORT number are correct, then try connecting again");
		} catch (IOException e) {
			ClientGUI.guiError("ERROR : SOMETHING WENT WRONG");
		}

	}
	
	public boolean getConnectionStatus() {
		return connectionStatus;
	}
	
	public void send(String toSend) {
		write.println(toSend);
	}
	
	
	public String get(String toSend) {
		write.println(toSend);
		String getline = "";
		try {
			getline = read.readLine();
		} catch (IOException e) {
			ClientGUI.guiError("Failed to GET, please try again");
		}
		
		return getline;
	}
	
	public List<Note> parseNotes(String response) {
		List 
	}
	
	
	@Override
	public void run() {
		try {
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			write = new PrintWriter(socket.getOutputStream());

			handshake = read.readLine();
						
			String recv;
			while(connectionStatus) {	

			}
			
		} catch(IOException e) {
			ClientGUI.guiError("ERROR : Could not recieve data from server");
		}
	}
	
	
	public void processCommand(String recv) {
		
		switch(recv) {
			case "NOTE":
				break;
			case "PIN":
				break;
			case "OK":
				break;
		}
		
	}
	
	public String getHandshake() {
		return handshake;
	}
}

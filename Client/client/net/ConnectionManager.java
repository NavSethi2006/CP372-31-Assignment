package client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import client.gui.ClientGUI;

public class ConnectionManager extends Thread{
	
	Socket socket;
	String alias;
	boolean connectionStatus;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	public void connectToServer (String ip, int port, String alias) {
		this.alias = alias;
		try {
			socket = new Socket(ip, port);	
			connectionStatus = socket.isConnected();

			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
		}catch(UnknownHostException e1) {
			ClientGUI.guiError("ERROR : The host your trying to connect to does not exist."
							+ "assure that the IP address and the PORT number are correct, then try connecting again");
		} catch (IOException e) {
			
		}

	}
	
	public boolean getConnectionStatus() {
		return connectionStatus;
	}
	
	public void send(String toSend) {
		try {
			dos.writeUTF(toSend);
		} catch (IOException e) {
			ClientGUI.guiError("ERROR : Could not send data to server");
		}
	}
	
	
	@Override
	public void run() {
		try {
			
			String recv;
			while(connectionStatus) {	
					recv = dis.readUTF();	
			}
			
		} catch(IOException e) {
			ClientGUI.guiError("ERROR : Could not recieve data from server");
		}
	}
}

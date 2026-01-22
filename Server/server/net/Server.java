/*
 * Author : Navin Sethi
 * ID : 169086962
 * Date : 2026/01/21
 * Description : Gateway to main code
 * 
 */

package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


// Server class and gateway to main code
public class Server {
	
	private ServerSocket server;
	private int port;
	private boolean ServerIsAlive;
	private ArrayList<Socket> clientsockets;
	private ServerAdmin admin;
	
	// Server constructor, takes in port
	public Server(int port) {
		this.port = port;
	}
	
	/* 
	 *  Server start, constructs ServerAdmin, ServerSocket,
	 *  While ServerIsAlive is true, the clients sockets connecting to the server will
	 *	constantly be listened too, once a client is found it will open a new thread to 
	 *	ClientHandler, this allows us to have multiple clients in one server
	 */
	 
	public void Start() {
		try {
			server = new ServerSocket(port);
			ServerIsAlive = true;
			
			System.out.println("Server running on\n IP ADDRESS: "+ server.getInetAddress().getHostAddress()+"\n"
					+ "on PORT: "+ server.getLocalPort());
			
			admin = new ServerAdmin(server);
			admin.start();

			
			while(ServerIsAlive) {
				Socket clientSocket = new Socket();
				clientsockets.add(clientSocket);
				ClientHandler client = new ClientHandler(clientSocket);
				admin.update_clients(clientsockets);
				client.start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

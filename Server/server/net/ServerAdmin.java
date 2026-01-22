/*
 * Author : Navin Sethi
 * ID : 169086962
 * Date : 2026/01/21
 * Description : Server interface to run commands on a simple GUI
 * 
 */

package server.net;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;


// not requied however i wanted to make an easy way for an admin of this server to be able
// to communicate and moderate clients that are connected
public class ServerAdmin extends Thread{

	ServerSocket server;
	ArrayList<Socket> clients;
	public ServerAdmin(ServerSocket server) {
		this.server = server;
	}
	
	public void update_clients(ArrayList<Socket> clients) {
		this.clients = clients;
	}
	
	public static void log() {
		
	}
	
	public void run()
	{
		
	}
}

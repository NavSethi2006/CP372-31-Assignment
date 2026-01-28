/*
 * Author : Navin Sethi
 * ID : 169086962
 * Date : 2026/01/21
 * Description : Gateway to main code
 * 
 */

package server.net;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import server.gui.Board;


// Server class and gateway to main code
public class Server {
	
	private ServerSocket server;
	private int port;
	private boolean ServerIsAlive;
	private ArrayList<Socket> clientsockets = new ArrayList<Socket>();
	private ServerAdmin admin;
	private Board tempboard;
		
	// Server constructor, takes in port
	public Server(int port, Board board) {
		this.port = port;
		tempboard = board;
	}
	
	/* 
	 *  Server start, constructs ServerAdmin, ServerSocket,
	 *  While ServerIsAlive is true, the clients sockets connecting to the server will
	 *	constantly be listened too, once a client is found it will open a new thread to 
	 *	ClientHandler, this allows us to have multiple clients in one server
	 */
	 
	public void Start() {
		try {
			try {
			server = new ServerSocket(port);
			} catch(BindException e) {
				System.err.println("Something went wrong with opening the socket. You may not have permissions, try again later");
				System.exit(1);
			}
			ServerIsAlive = true;
			
			System.out.println("Server running on\nIP ADDRESS: "+server.getInetAddress().getHostAddress()+"\n"
					+ "on PORT: "+ server.getLocalPort());
			
			admin = new ServerAdmin(server);
			admin.start();

			System.out.println("Clients can connect using the IP address(es) : "+ getMainLANIP());
			
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	               System.out.println("\nShutting down server...");
	               stopServer();
	        }));
			
			
			while(ServerIsAlive) {
				Socket clientSocket = server.accept();
				clientsockets.add(clientSocket);
				ClientHandler client = new ClientHandler(clientSocket, tempboard);
				admin.update_clients(clientsockets);
				client.start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
    public void stopServer() {
        ServerIsAlive = false;
        try {
            if (server != null && !server.isClosed()) {
                server.close();
                System.out.println("Server socket closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

	private String getMainLANIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                
                // Skip loopback and inactive interfaces
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    
                    // Only IPv4 addresses
                    if (addr instanceof Inet4Address) {
                        String ip = addr.getHostAddress();
                        
                        // Check if it's in private LAN range
                        if (ip.startsWith("192.168.") || 
                            ip.startsWith("10.") || 
                            ip.startsWith("172.16.") ||
                            ip.startsWith("172.17.") ||
                            ip.startsWith("172.18.") ||
                            ip.startsWith("172.19.") ||
                            ip.startsWith("172.20.") ||
                            ip.startsWith("172.21.") ||
                            ip.startsWith("172.22.") ||
                            ip.startsWith("172.23.") ||
                            ip.startsWith("172.24.") ||
                            ip.startsWith("172.25.") ||
                            ip.startsWith("172.26.") ||
                            ip.startsWith("172.27.") ||
                            ip.startsWith("172.28.") ||
                            ip.startsWith("172.29.") ||
                            ip.startsWith("172.30.") ||
                            ip.startsWith("172.31.")) {
                            return ip; // Return the first LAN IP found
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting LAN IP: " + e.getMessage());
        }
        return null; // No LAN IP found
    }
}


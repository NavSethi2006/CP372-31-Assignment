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
import java.util.List;

import server.gui.Board;

public class Server {
    
    private ServerSocket server;
    private int port;
    private boolean ServerIsAlive;
    private List<ClientHandler> clients = new ArrayList<>();
    private ServerAdmin admin;
    private Board tempboard;
        
    public Server(int port, Board board) {
        this.port = port;
        tempboard = board;
    }
    
    public synchronized void broadcast(String message) {
        System.out.println(clients.size() + " clients: " + message);
        List<ClientHandler> disconnected = new ArrayList<>();
        
        for (ClientHandler client : clients) {
            try {
                client.send(message);
            } catch (Exception e) {
                System.err.println("Failed to send to client, marking for removal");
                disconnected.add(client);
            }
        }
        
        clients.removeAll(disconnected);
    }
    
    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client removed. Total clients: " + clients.size());
    }
     
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
                System.out.println("Accepted client from IP : "+ clientSocket.getInetAddress().getHostAddress()+":"+clientSocket.getPort());
                ClientHandler client = new ClientHandler(clientSocket, tempboard, this); 
                clients.add(client);
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
                
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    
                    if (addr instanceof Inet4Address) {
                        String ip = addr.getHostAddress();
                        
                        if (ip.startsWith("192.168.") || 
                            ip.startsWith("10.") || 
                            (ip.startsWith("172.") && 
                             Integer.parseInt(ip.split("\\.")[1]) >= 16 && 
                             Integer.parseInt(ip.split("\\.")[1]) <= 31)) {
                            return ip;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting LAN IP: " + e.getMessage());
        }
        return null;
    }
}
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

import server.gui.Board;

public class ClientHandler extends Thread {
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Board board;
    private Server server;
    private String IP;
    private volatile boolean running = true;
    
    public ClientHandler(Socket socket, Board board, Server server) {
        this.socket = socket;
        this.board = board;
        this.server = server;
        IP = socket.getInetAddress().getHostAddress();
        
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error creating streams: " + e.getMessage());
        }
    }
    
    public void send(String postmsg) {
        if (out != null && !socket.isClosed()) {
            out.println(postmsg);
            out.flush();
        }
    }
    
    public void run() {
        try {
            String handshake = board.getConfigString();
            out.println(handshake);
            
            String command;
            while(running && (command = in.readLine()) != null) {
                System.out.println("Received from " + IP + ": " + command);
                processCommand(command);
            }
        } catch(Exception e) {
            if (running) {
                System.err.println("Client " + IP + " error: " + e.getMessage());
            }
        } finally {
            cleanup();
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
        
        try {
            switch(cmd) {
                case "NOTE":
                    System.out.println("Processing NOTE command");
                    if (parts.length < 4) {
                        send("ERROR INVALID_NOTE_FORMAT");
                        return;
                    }
                    x = Integer.parseInt(parts[1]);
                    y = Integer.parseInt(parts[2]);
                    String color = parts[3];
                    StringBuilder message = new StringBuilder();
                    for (int i = 4; i < parts.length; i++) {
                        message.append(parts[i]);
                        if (i < parts.length - 1) {
                            message.append(" ");
                        }
                    }
                    returnMessage = board.POSTnote(x, y, color, message.toString());
                    send(returnMessage);
                    
                    // Broadcast to all clients if successful
                    if (returnMessage.startsWith("OK")) {
                        String broadcastMsg = String.format("NOTE %d %d %s %s false", 
                            x, y, color, message.toString());
                        server.broadcast(broadcastMsg);
                    }
                    break;
                    
                case "PIN":
                    if (parts.length < 3) {
                        send("ERROR INVALID_PIN_FORMAT");
                        return;
                    }
                    x = Integer.parseInt(parts[1]);
                    y = Integer.parseInt(parts[2]);
                    returnMessage = board.addPin(x, y);
                    send(returnMessage);
                    
                    // Broadcast to all clients if successful
                    if (returnMessage.startsWith("OK")) {
                        server.broadcast(String.format("PIN %d %d", x, y));
                    }
                    break;
                    
                case "UNPIN":
                    if (parts.length < 3) {
                        send("ERROR INVALID_UNPIN_FORMAT");
                        return;
                    }
                    x = Integer.parseInt(parts[1]);
                    y = Integer.parseInt(parts[2]);
                    returnMessage = board.removePin(x, y);
                    send(returnMessage);
                    
                    // Broadcast to all clients if successful
                    if (returnMessage.startsWith("OK")) {
                        server.broadcast(String.format("UNPIN %d %d", x, y));
                    }
                    break;
                    
                case "GET":
                    System.out.println("Processing GET command");
                    StringBuilder getmsg = new StringBuilder();
                    getmsg.append(board.getAllNotes());
                    getmsg.append(board.getAllPins());
                    getmsg.append("\n"); // Empty line to signal end
                    send(getmsg.toString());
                    break;
                    
                case "CLEAR":
                    returnMessage = board.clear();
                    send(returnMessage);
                    // Broadcast to all clients
                    server.broadcast("CLEAR");
                    break;
                    
                case "SHAKE":
                    System.out.println("Processing SHAKE command - ATOMIC operation");
                    returnMessage = board.shake();
                    send(returnMessage);
                    // ATOMIC
                    StringBuilder shakeState = new StringBuilder();
                    shakeState.append("SHAKE_STATE\n");
                    shakeState.append(board.getAllNotes());
                    shakeState.append(board.getAllPins());
                    shakeState.append("\n"); 
                    
                    server.broadcast(shakeState.toString());
                    System.out.println("Broadcasted atomic SHAKE state to all clients");
                    break;
                    
                case "DISCONNECT":
                    System.out.println("Client disconnecting: " + IP);
                    running = false;
                    send("OK DISCONNECT");
                    break;
                    
                default:
                    send("ERROR UNKNOWN_COMMAND");
                    break;
            }
        } catch (NumberFormatException e) {
            send("ERROR INVALID_NUMBER_FORMAT");
        } catch (Exception e) {
            System.err.println("ERROR processing command: " + command);
            e.printStackTrace();
            send("ERROR SERVER_EXCEPTION");
        }
    }
    
    private void cleanup() {
        running = false;
        server.removeClient(this);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
        System.out.println("Client " + IP + " disconnected. Thread " + threadId());
    }
}
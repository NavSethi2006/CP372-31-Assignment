package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import client.gui.BoardPanel;
import client.gui.ClientGUI;
import client.gui.Note;
import client.gui.Pin;

public class ConnectionManager extends Thread {
    
    private Socket socket;
    private String alias;
    private volatile boolean connectionStatus = false;
    private volatile boolean running = true;
    private BufferedReader read;
    private PrintWriter write;
    private String handshake;
    private BoardPanel board;
    
    public void connectToServer(String ip, int port, String alias, BoardPanel board) {
        this.alias = alias;
        try {
            socket = new Socket(ip, port);
            connectionStatus = socket.isConnected();
            this.board = board;
            
        } catch(UnknownHostException e1) {
            ClientGUI.guiError("ERROR : The host you're trying to connect to does not exist.");
        } catch (IOException e) {
            ClientGUI.guiError("ERROR : Could not connect to server - " + e.getMessage());
        }
    }
    
    public boolean getConnectionStatus() {
        return connectionStatus;
    }
    
    public synchronized void send(String toSend) {
        if (write != null && connectionStatus) {
            System.out.println("Sending: " + toSend);
            write.println(toSend);
            write.flush();
        }
    }
    
    public void requestGet() {
        send("GET");
    }
    
    public String get() {
        requestGet();
        return "GET requested";
    }
    
    public void disconnect() {
        System.out.println("Disconnecting from server...");
        running = false;
        
        try {
            if (write != null && connectionStatus) {
                send("DISCONNECT " + alias);
                Thread.sleep(100);
            }
        } catch (Exception e) {
            // Ignore
        }
        
        connectionStatus = false;
        
        try {
            if (read != null) read.close();
            if (write != null) write.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Disconnected successfully");
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write = new PrintWriter(socket.getOutputStream(), true);

            handshake = read.readLine();
            System.out.println("Received handshake: " + handshake);
                        
            String recv;
            while(running && connectionStatus) {    
                recv = read.readLine();
                if (recv == null) {
                    System.out.println("Server closed connection");
                    break;
                }
                System.out.println("GOT MESSAGE: " + recv);
                processCommand(recv);
            }
            
        } catch(IOException e) {
            if (running) {
                SwingUtilities.invokeLater(() -> {
                    ClientGUI.guiError("ERROR : Lost connection to server");
                });
            }
        } finally {
            disconnect();
        }
    }
    
    private void parseGetResponse() {
        try {
            List<Note> newNotes = new ArrayList<>();
            List<Pin> newPins = new ArrayList<>();
            
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                System.out.println("GET line: " + line);
                
                if (line.isEmpty()) {
                    break;
                }
                
                if (line.startsWith("NOTE")) {
                    parseNoteLine(line, newNotes);
                } else if (line.startsWith("PIN")) {
                    parsePinLine(line, newPins);
                } else if (line.equals("NO_NOTES")) {
                    break;
                }
            }
            
            updateNotesWithPins(newNotes, newPins);
            
            System.out.println("Loaded " + newNotes.size() + " notes and " + newPins.size() + " pins");
            
        } catch (IOException e) {
            System.err.println("Error parsing GET response: " + e.getMessage());
        }
    }
    
    private void parseShakeState() {
        try {
            System.out.println("Parsing ATOMIC SHAKE_STATE");
            List<Note> newNotes = new ArrayList<>();
            List<Pin> newPins = new ArrayList<>();
            
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                System.out.println("SHAKE_STATE line: " + line);
                
                if (line.isEmpty()) {
                    break;
                }
                
                if (line.startsWith("NOTE")) {
                    parseNoteLine(line, newNotes);
                } else if (line.startsWith("PIN")) {
                    parsePinLine(line, newPins);
                } else if (line.equals("NO_NOTES")) {
                    break;
                }
            }
            
            updateNotesWithPins(newNotes, newPins);
            
            System.out.println("SHAKE complete - Board now has " + newNotes.size() + " notes and " + newPins.size() + " pins");
            
        } catch (IOException e) {
            System.err.println("Error parsing SHAKE_STATE: " + e.getMessage());
        }
    }
    
    private void updateNotesWithPins(List<Note> newNotes, List<Pin> newPins) {
        // Update pinned status based on pins
        for (Note note : newNotes) {
            boolean isPinned = false;
            for (Pin pin : newPins) {
                if (note.containsPoint(pin.getX(), pin.getY())) {
                    isPinned = true;
                    break;
                }
            }
            note.setPinned(isPinned);
        }
        
        final List<Note> finalNotes = newNotes;
        final List<Pin> finalPins = newPins;
        
        SwingUtilities.invokeLater(() -> {
            board.updateBoard(finalNotes, finalPins);
        });
    }
    
    private void parseNoteLine(String line, List<Note> notesList) {
        try {
            String data = line.substring(5).trim();
            String[] parts = data.split(" ");
            
            if (parts.length >= 5) {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String color = parts[2];
                
                boolean pinned = Boolean.parseBoolean(parts[parts.length - 1]);
                
                StringBuilder msg = new StringBuilder();
                for (int i = 3; i < parts.length - 1; i++) {
                    if (i > 3) msg.append(" ");
                    msg.append(parts[i]);
                }
                String message = msg.toString();
                
                notesList.add(new Note(x, y, board.noteWidth, board.noteHeight, color, message, pinned));
                System.out.println("Parsed note: '" + message + "' at (" + x + "," + y + ") pinned=" + pinned);
            }
        } catch (Exception e) {
            System.err.println("Error parsing note line: " + line);
            e.printStackTrace();
        }
    }
    
    private void parsePinLine(String line, List<Pin> pinsList) {
        try {
            String[] parts = line.split(" ");
            if (parts.length == 3) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                pinsList.add(new Pin(x, y));
                System.out.println("Parsed pin at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error parsing pin line: " + line);
        }
    }
    
    public void processCommand(String recv) {
        if (recv == null || recv.trim().isEmpty()) {
            return;
        }
        
        String[] parts = recv.split(" ");
        String cmd = parts[0].toUpperCase();
        
        switch(cmd) {
            case "OK":
                System.out.println("Server OK: " + recv);
                break;
                
            case "ERROR":
                System.err.println("Server error: " + recv);
                final String errorMsg = recv;
                SwingUtilities.invokeLater(() -> {
                    ClientGUI.guiError("Server Error: " + errorMsg);
                });
                break;
                
            case "NOTE":
                if (recv.startsWith("NOTE ")) {
                    handleNoteUpdate(parts);
                } else {
                    parseGetResponse();
                }
                break;
                
            case "PIN":
                handlePinUpdate(parts);
                break;
                
            case "UNPIN":
                handleUnpinUpdate(parts);
                break;
                
            case "CLEAR":
                SwingUtilities.invokeLater(() -> {
                    board.clearBoard();
                });
                break;
                
            case "SHAKE_STATE":
                // ATOMIC
                System.out.println("Received ATOMIC SHAKE_STATE");
                parseShakeState();
                break;
                
            case "NO_NOTES":
                SwingUtilities.invokeLater(() -> {
                    board.updateBoard(new ArrayList<>(), new ArrayList<>());
                });
                break;
        }
    }
    
    private void handleNoteUpdate(String[] parts) {
        try {
            if (parts.length >= 6) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                String color = parts[3];
                
                boolean pinned = Boolean.parseBoolean(parts[parts.length - 1]);
                
                StringBuilder msg = new StringBuilder();
                for (int i = 4; i < parts.length - 1; i++) {
                    if (i > 4) msg.append(" ");
                    msg.append(parts[i]);
                }
                String message = msg.toString();
                
                final Note newNote = new Note(x, y, board.noteWidth, board.noteHeight, color, message, pinned);
                
                SwingUtilities.invokeLater(() -> {
                    board.notes.removeIf(n -> n.getX() == newNote.getX() && n.getY() == newNote.getY());
                    board.notes.add(newNote);
                    board.repaint();
                });
                
                System.out.println("Added/updated note: '" + message + "' at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error handling note update");
            e.printStackTrace();
        }
    }
    
    private void handlePinUpdate(String[] parts) {
        try {
            if (parts.length == 3) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                
                final Pin newPin = new Pin(x, y);
                
                SwingUtilities.invokeLater(() -> {
                    if (!board.pins.contains(newPin)) {
                        board.pins.add(newPin);
                    }
                    
                    for (Note note : board.notes) {
                        if (note.containsPoint(x, y)) {
                            note.setPinned(true);
                        }
                    }
                    board.repaint();
                });
                
                System.out.println("Added pin at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error handling pin update");
        }
    }
    
    private void handleUnpinUpdate(String[] parts) {
        try {
            if (parts.length == 3) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                
                SwingUtilities.invokeLater(() -> {
                    board.pins.removeIf(p -> p.getX() == x && p.getY() == y);
                    
                    for (Note note : board.notes) {
                        if (note.containsPoint(x, y)) {
                            boolean stillPinned = false;
                            for (Pin pin : board.pins) {
                                if (note.containsPoint(pin.getX(), pin.getY())) {
                                    stillPinned = true;
                                    break;
                                }
                            }
                            note.setPinned(stillPinned);
                        }
                    }
                    board.repaint();
                });
                
                System.out.println("Removed pin at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error handling unpin update");
        }
    }
    
    public String getHandshake() {
        return handshake;
    }
    
    public void setBoardPanel(BoardPanel board) {
        this.board = board;
    }
}
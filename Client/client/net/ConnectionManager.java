package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import client.gui.BoardPanel;
import client.gui.ClientGUI;
import client.gui.Note;
import client.gui.Pin;

public class ConnectionManager extends Thread{
	
	private Socket socket;
	private String alias;
	boolean connectionStatus = false;
	private BufferedReader read;
	private PrintWriter write;
	private String handshake;
	private BoardPanel board;
	
	public void connectToServer (String ip, int port, String alias, BoardPanel board) {
		this.alias = alias;
			try {
			
			socket = new Socket(ip, port);
			connectionStatus = socket.isConnected();
			this.board = board;
			
		} catch(UnknownHostException e1) {
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
		System.out.println("sent");
		write.println(toSend);
		write.flush();
	}
	
	public String get() {

        send("GET");
        StringBuilder response = new StringBuilder();
        try {
            String line;
            while ((line = read.readLine()) != null && !line.trim().isEmpty()) {
                response.append(line).append("\n");
                System.out.println("GET Response line: " + line);
            }
            System.out.println("GET Response:\n" + response.toString());
            
            // Parse the response and update notes/pins
            parseGetResponse(response.toString());
            
        } catch (IOException e) {
            ClientGUI.guiError("Failed to GET, please try again");
            return "";
        }
        
        return response.toString();
	}

	private void parseGetResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return;
        }
        
        // Clear current lists
        board.notes.clear();
        board.pins.clear();
        
        String[] lines = response.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            System.out.println("Parsing line: " + line);
            
            // Parse NOTE format from server: "NOTE x,y,color,content,isPinned"
            if (line.startsWith("NOTE")) {
                try {
                    // Format: "NOTE x,y,color,content,isPinned"
                    String data = line.substring(5); // Remove "NOTE "
                    String[] parts = data.split(",");
                    if (parts.length >= 5) {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        String color = parts[2];
                        String message = parts[3];
                        boolean pinned = Boolean.parseBoolean(parts[4]);
                        
                        board.notes.add(new Note(x, y, color, message, pinned));
                        System.out.println("Added note: " + message + " at (" + x + "," + y + ")");
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing note: " + line);
                }
            } 
            // Parse PIN format: "PIN x y"
            else if (line.startsWith("PIN")) {
                try {
                    String[] parts = line.split(" ");
                    if (parts.length == 3) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        board.pins.add(new Pin(x, y));
                        System.out.println("Added pin at (" + x + "," + y + ")");
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing pin: " + line);
                }
            }
            // Handle "NO_NOTES" response
            else if (line.equals("NO_NOTES")) {
                board.notes.clear();
            }
        }
        
        // Update pinned status for notes based on pins
        for (Note note : board.notes) {
            boolean isPinned = false;
            for (Pin pin : board.pins) {
                if (pin.getX() == note.getX() && pin.getY() == note.getY()) {
                    isPinned = true;
                    break;
                }
            }
            note.setPinned(isPinned);
        }
        
        System.out.println("Parsed " + board.notes.size() + " notes and " + board.pins.size() + " pins");
    }
	
	
	@Override
	public void run() {
		try {
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			write = new PrintWriter(socket.getOutputStream(), true);

			handshake = read.readLine();
						
			String recv;
			while(connectionStatus) {	
				recv = read.readLine();
				System.out.println("GOT MESSAGE :"+ recv);
				processCommand(recv);
				
			}
			
		} catch(IOException e) {
			ClientGUI.guiError("ERROR : Could not recieve data from server");
		}
	}
	
	private void processNoteCommand(String []parts) {
		try {
            // Check format: "NOTE x,y,color,content,isPinned" or "NOTE x y color content"
            if (parts[1].contains(",")) {
                // Format: "NOTE x,y,color,content,isPinned"
                String[] noteParts = parts[1].split(",");
                if (noteParts.length >= 5) {
                    int x = Integer.parseInt(noteParts[0]);
                    int y = Integer.parseInt(noteParts[1]);
                    String color = noteParts[2];
                    String message = noteParts[3];
                    boolean pinned = Boolean.parseBoolean(noteParts[4]);
                    
                    // Remove existing note at this position
                    board.notes.removeIf(note -> note.getX() == x && note.getY() == y);
                    
                    // Add new note
                    board.notes.add(new Note(x, y, color, message, pinned));
                    System.out.println("Processed NOTE command: " + message + " at (" + x + "," + y + ")");
                }
            } else if (parts.length >= 5) {
                // Format: "NOTE x y color message"
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                String color = parts[3];
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 4; i < parts.length; i++) {
                    messageBuilder.append(parts[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();
                
                // Check if pinned
                boolean pinned = false;
                for (Pin pin : board.pins) {
                    if (pin.getX() == x && pin.getY() == y) {
                        pinned = true;
                        break;
                    }
                }
                
                // Remove existing note at this position
                board.notes.removeIf(note -> note.getX() == x && note.getY() == y);
                
                // Add new note
                board.notes.add(new Note(x, y, color, message, pinned));
                System.out.println("Processed NOTE command: " + message + " at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error processing NOTE command: " + String.join(" ", parts));
        }
		
	}
	
	private void processPinCommand(String []parts) {
        try {
            if (parts.length == 3) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                
                // Add pin
                board.pins.add(new Pin(x, y));
                
                // Update any note at this position to be pinned
                for (Note note : board.notes) {
                    if (note.getX() == x && note.getY() == y) {
                        note.setPinned(true);
                    }
                }
                
                System.out.println("Processed PIN command at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error processing PIN command: " + String.join(" ", parts));
        }

		
	}
	
	private void processUnpinCommand(String []parts) {
        try {
            if (parts.length == 3) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                
                // Remove pin
                board.pins.removeIf(pin -> pin.getX() == x && pin.getY() == y);
                
                // Update any note at this position to be unpinned
                for (Note note : board.notes) {
                    if (note.getX() == x && note.getY() == y) {
                        note.setPinned(false);
                    }
                }
                
                System.out.println("Processed UNPIN command at (" + x + "," + y + ")");
            }
        } catch (Exception e) {
            System.err.println("Error processing UNPIN command: " + String.join(" ", parts));
        }
	}
	
	private void processClearCommand() {
		board.notes.clear();
		board.pins.clear();
	}
	
	
	public void processCommand(String recv) {
		if (recv == null || recv.trim().isEmpty()) {
            return;
        }
		String[] parts = recv.split(" ");
		String cmd = parts[0].toUpperCase();
		
	    switch(cmd) {
        case "OK":
            System.out.println("Operation successful: " + recv);
            break;
        case "ERROR":
            System.err.println("Server error: " + recv);
            break;
        case "NOTE":
            // Process NOTE command from server
            processNoteCommand(parts);
            break;
        case "PIN":
            // Process PIN command from server
            processPinCommand(parts);
            break;
        case "UNPIN":
            // Process UNPIN command from server
            processUnpinCommand(parts);
            break;
        case "CLEAR":
            // Process CLEAR command from server
            processClearCommand();
            break;
	    }
	}
	
	public String getHandshake() {
		return handshake;
	}
}

package server.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class Board {

	private int boardWidth;
	private int boardHeight;
	private int noteWidth;
	private int noteHeight;
	private List<String> validColors;
	private List<Note> notes;
	private Set<Pin> pins;
	private ReentrantLock lock = new ReentrantLock();
	
	public Board(int boardWidth, int boardHeight, int noteWidth, int noteHeight) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.noteWidth = noteWidth;
		this.noteHeight = noteHeight;
		
		// valid colors for now
		validColors = new ArrayList<String>();
		validColors.add("Red");
		validColors.add("Blue");
		validColors.add("Green");
		validColors.add("Yellow");
		
		notes = new ArrayList<Note>();
		pins = new HashSet<Pin>();
	}
	

	// helper function
    private boolean isNotePinned(Note note) {
        for (Pin pin : pins) {
            if (pin.isInsideNote(note)) {
                return true;
            }
        }
        return false;
    }
	
	public String POSTnote(int x, int y, String Color, String message) {
		lock.lock();
		try {
			if(validColors.contains(Color.toLowerCase())) {
				return "[-] POST note ERROR COLOR_NOT_SUPPORTED " + Color + " is not a valid color";
			}
			Note note = new Note(x, y, Color, message, noteWidth, noteHeight);
			if(!note.isWithinBounds(boardWidth, boardHeight)) {
				return "[-] POST note ERROR OUT_OF_BOARD_BOUNDS x:"+x+" y:"+y+" exceed border bounds"; 
			}
				
			for (Note allnotes : notes) {
				if(note.completeOverlap(allnotes)) {
					return "[-] POST note ERROR OVERLAP_NOTE x:"+x+" y:"+y+" already has a note that exists";
				}
			}
			notes.add(note);
			return "[+] OK NOTE_POSTED";
		} finally {
			lock.unlock();
		}
	}
	
	public String addPin(int x, int y) {
		lock.lock();
		
		try {
			boolean insideAnyNote = false;
			for(Note note: notes) {
				if(note.containsPoint(x, y)) {
					insideAnyNote = true;
					break;
				}
			}
			
			if(insideAnyNote) {
				return "[-] POST pin ERROR PIN_OVERLAP_WITH_NOTE x:"+x+" y:"+y+" already has a note associated with that position";
			}
			pins.add(new Pin(x,y));
			return "OK PIN_ADDED";
			
		} finally {
			lock.unlock();
		}
	}
	
	public String removePin(int x, int y) {
		lock.lock();
		try {
		Pin pintoremove = new Pin(x,y);
		if(pins.contains(pintoremove)) {
			pins.remove(pintoremove);
			return "OK PIN_REMOVED";
		} else {
			return "[-]ERROR PIN_NOT_FOUND No pin at this location exists";
		}
		
		} finally {
		lock.unlock();
		}
	}
	
	public String shake() {
		lock.lock();
		try {
			List<Note> notestokeep = new ArrayList<Note>();
			for(Note note : notes ) {
				if(isNotePinned(note)) {
					notestokeep.add(note);
				}
			}
			notes.clear();
			notes.addAll(notestokeep);
			return "OK SHAKE_COMPLETE";
		}finally {
			lock.unlock();
		}
	}
	
	public String clear() {
		lock.lock();
		try {
			notes.clear();
			pins.clear();
			return "OK CLEAR_COMPLETE";
		} finally {
			lock.unlock();
		}
	}
	
	public String getAllNotes() {
			
			StringBuilder allNotesResponse = new StringBuilder();
			if(!notes.isEmpty()) {
			for(Note note : notes) {
				allNotesResponse.append(note.toProtocolString(isNotePinned(note)));
				allNotesResponse.append("\n");
			}
			} else {
				allNotesResponse.append("NO_NOTES");
			}
			return allNotesResponse.toString();
	}
	public String getAllPins() {
			StringBuilder allPinsResponse = new StringBuilder();
			if(!pins.isEmpty()) {
			for(Pin pin: pins) {
				allPinsResponse.append(pin.toProtocol());
				allPinsResponse.append("\n");
			}
			} else {
				allPinsResponse.append(" ");
			}
			return allPinsResponse.toString();
			
	}
	
	public String getNotes(Integer containsX, Integer containsY, String refersTo) {
		lock.lock();
		try {
			List<Note> matchingNotes = new ArrayList<Note>();
			for(Note note : notes) {
				boolean matches = true;
				if(containsX != null && containsY != null && !note.containsPoint(containsX, containsY))matches = false;
				if(refersTo != null && !note.getMessage().toLowerCase().contains(refersTo.toLowerCase()))matches = false;
				if(matches) matchingNotes.add(note);
			}
			
			StringBuilder res = new StringBuilder();
			res.append("OK ").append(matchingNotes.size());
			for(Note note: matchingNotes) {
				res.append("\n");
				res.append("NOTE ").append(note.toProtocolString(isNotePinned(note)));
			}
			
			return res.toString();
		} finally {
			
			lock.unlock();
		}
	}
	
	public String getPins() {
		lock.lock();
		try {
			StringBuilder res = new StringBuilder();
			res.append("OK ").append(pins.size());
			for(Pin pin: pins) {
				res.append("\n");
				res.append(pin.toProtocol().toString());
			}
			
			return res.toString();
		} finally {
			lock.unlock();
		}
	}
	
	public String getConfigString() {
		return String.format("%d %d %d %d %s", boardWidth, boardHeight, noteWidth, noteHeight, 
							String.join(" ", validColors));
	}
	
	
}

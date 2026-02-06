# Bulletin Board System - Client-Server Application

A distributed client-server application implementing a networked bulletin board system using Java TCP sockets. Multiple concurrent clients can post, query, pin, and manage notes on a centralized server.

## System Requirements

- **Java Development Kit (JDK)**: Version 8 or higher
- **Operating System**: Windows, macOS, or Linux
- **Network**: Server and clients must be on the same network (or accessible via IP)
- **Display**: GUI requires graphical display capability

## Project Structure

```
.
├── Server/
│   └── server/
│       ├── BullitinBoardServer.java    # Main server entry point
│       ├── gui/
│       │   ├── Board.java              # Board logic and data management
│       │   ├── Note.java               # Note data structure
│       │   ├── Pin.java                # Pin data structure
│       │   └── ServerAdminGUI.java     # (Optional) Server admin interface
│       └── net/
│           ├── Server.java             # Server socket and client management
│           ├── ClientHandler.java      # Individual client handler thread
│           └── ServerAdmin.java        # Server administration utilities
│
├── Client/
│   └── client/
│       ├── BullitinBoardClient.java    # Main client entry point
│       ├── gui/
│       │   ├── BoardPanel.java         # Visual board rendering
│       │   ├── ClientGUI.java          # Main GUI controller
│       │   ├── ControlPanel.java       # Action buttons panel
│       │   ├── StatusPanel.java        # Status and logging panel
│       │   ├── Note.java               # Note visualization
│       │   └── Pin.java                # Pin visualization
│       └── net/
│           └── ConnectionManager.java  # Server connection management
│
└── README.md
```

## Compilation Instructions

### Step 1: Create Binary Directory

```bash
mkdir bin
```

### Step 2: Compile Server

```bash
javac Server/server/gui/*.java Server/server/net/*.java Server/server/*.java -d bin
```

### Step 3: Compile Client

```bash
javac Client/client/gui/*.java Client/client/net/*.java Client/client/*.java -d bin
```

### One-Line Compilation (All at Once)

```bash
mkdir -p bin && javac Server/server/gui/*.java Server/server/net/*.java Server/server/*.java Client/client/gui/*.java Client/client/net/*.java Client/client/*.java -d bin
```

## Running the Application

### Starting the Server

**Basic Usage (Default Settings):**
```bash
java -cp bin server.BullitinBoardServer
```
- Default Port: `8080`
- Default Board: `800x600` pixels
- Default Note Size: `50x50` pixels

**Example:**
```bash
java -cp bin server.BullitinBoardServer 4554 1000 800 60 40 red blue green yellow white pink
```

### Starting the Client

```bash
java -cp bin client.BullitinBoardClient
```

**Connection Setup:**
1. Enter Server IP address (e.g., `192.168.1.100` or `localhost`)
2. Enter Server Port (e.g., `8080` or `4554`)
3. Enter your Username/Alias
4. Click "Enter" to connect

## Usage Guide

### Client Interface Overview

**Control Panel Buttons:**
- **POST**: Create a new note on the board
- **GET**: Refresh and display all current notes
- **PIN**: Pin a note at specific coordinates
- **UNPIN**: Remove a pin from coordinates
- **SHAKE**: Remove all unpinned notes (atomic operation)
- **CLEAR**: Remove all notes and pins
- **Refresh**: Manually refresh the board display
- **+ / -**: Zoom in/out on the board
- **Reset View**: Reset zoom and position

**Board Interaction:**
- **Right-click + Drag**: Pan around the board
- **Mouse Wheel**: Zoom in/out
- **Visual Indicators**: 
  - Yellow border = Pinned note
  - Normal border = Unpinned note
  - Red pins = Pin locations

### Creating a Note (POST)

1. Click the **POST** button
2. Enter X coordinate (e.g., `100`)
3. Enter Y coordinate (e.g., `150`)
4. Select a color from the dropdown
5. Enter your message text
6. Click "Post"

**Note:** Notes must be within board boundaries and cannot completely overlap existing notes.

### Pinning Notes

1. Click the **PIN** button
2. Enter the X and Y coordinates **inside** an existing note
3. Click "Pin"

**Important:** Pins can only be placed inside existing notes. The coordinates must fall within a note's boundaries.

### Using SHAKE

The SHAKE command removes all **unpinned** notes from the board:
1. Pin the notes you want to keep
2. Click the **SHAKE** button
3. All unpinned notes are removed atomically
4. All clients see the update simultaneously

### Using CLEAR

The CLEAR command removes **everything** from the board:
- All notes (pinned and unpinned)
- All pins
- Complete board reset

## Protocol Commands

### Client → Server Commands

| Command | Format | Description |
|---------|--------|-------------|
| NOTE | `NOTE x y color message` | Post a new note |
| PIN | `PIN x y` | Add a pin at coordinates |
| UNPIN | `UNPIN x y` | Remove a pin at coordinates |
| GET | `GET` | Request current board state |
| SHAKE | `SHAKE` | Remove all unpinned notes |
| CLEAR | `CLEAR` | Remove all notes and pins |
| DISCONNECT | `DISCONNECT username` | Gracefully disconnect |

### Server → Client Responses

| Response | Format | Description |
|----------|--------|-------------|
| OK | `OK MESSAGE` | Operation successful |
| ERROR | `ERROR TYPE message` | Operation failed |
| NOTE | `NOTE x y color message pinned` | Broadcast note update |
| PIN | `PIN x y` | Broadcast pin added |
| UNPIN | `UNPIN x y` | Broadcast pin removed |
| CLEAR | `CLEAR` | Broadcast clear operation |
| SHAKE_STATE | `SHAKE_STATE\nNOTE...\nPIN...\n` | Atomic board state after shake |

### Error Codes

- `COLOR_NOT_SUPPORTED`: Invalid color specified
- `OUT_OF_BOARD_BOUNDS`: Note exceeds board dimensions
- `OVERLAP_NOTE`: Note completely overlaps existing note
- `PIN_NOT_IN_NOTE`: Pin coordinates not inside any note
- `PIN_ALREADY_EXISTS`: Pin already exists at location
- `PIN_NOT_FOUND`: No pin exists at specified location
- `INVALID_FORMAT`: Malformed command
- `UNKNOWN_COMMAND`: Unrecognized command

## Troubleshooting

### Compilation Issues

**Problem:** `javac: command not found`
- **Solution:** Install JDK and ensure it's in your PATH

**Problem:** `error: package does not exist`
- **Solution:** Ensure you're compiling from the project root directory

### Server Issues

**Problem:** `BindException: Address already in use`
- **Solution:** Port is already in use. Choose a different port or kill the process using that port:
  ```bash
  # Find process on port (Linux/Mac)
  lsof -i :8080
  # Kill process
  kill -9 <PID>
  
  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  ```

**Problem:** Cannot connect from other computers
- **Solution:** 
  - Check firewall settings
  - Ensure server is binding to correct network interface
  - Use the LAN IP address shown in server console output

### Client Issues

**Problem:** "Host does not exist" error
- **Solution:** 
  - Verify server IP address is correct
  - Use `localhost` or `127.0.0.1` for local testing
  - Use LAN IP (e.g., `192.168.x.x`) for network connections

**Problem:** Notes not appearing after POST
- **Solution:** 
  - Click GET/Refresh to update board
  - Check server console for errors
  - Verify coordinates are within board bounds

**Problem:** Cannot pin a note
- **Solution:** 
  - Ensure coordinates are **inside** an existing note
  - Note must already exist before pinning
  - Check that coordinates fall within note boundaries (x to x+width, y to y+height)

**Problem:** SHAKE not working
- **Solution:**
  - Ensure notes are properly pinned before shaking
  - Check server console for errors
  - Verify all clients receive SHAKE_STATE broadcast

### Network Issues

**Problem:** "Connection refused"
- **Solution:**
  - Ensure server is running before starting client
  - Verify correct IP and port
  - Check network connectivity

**Problem:** Lost connection during operation
- **Solution:**
  - Check network stability
  - Restart both client and server
  - Check server console for error messages

## Valid Colors

The following colors are supported by default:
- red
- blue
- green
- yellow
- white
- pink
- cyan

Colors can be customized when starting the server by providing color names as command-line arguments.

## Notes on Thread Safety

- All board operations are synchronized using `ReentrantLock`
- SHAKE and CLEAR operations are atomic
- Server broadcasts updates to all clients simultaneously
- Client GUI updates occur on the Event Dispatch Thread (EDT)

## Development Notes

- Default package structure (no package declarations in root)
- Java Swing for GUI components
- TCP sockets for network communication
- Multi-threaded server architecture
- Observer pattern for real-time updates

## Authors

- Navin Sethi (ID: 169086962)
- Abisan Vijayakaran ( 169044552)

## Assignment

CP372 (Sections D ) – Assignment 01  
Client–Server Bulletin Board System
---

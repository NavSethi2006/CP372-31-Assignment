/*
 * Author : Navin Sethi
 * ID : 169086962
 * Date : 2026/01/21
 * Description : Main class
 * 
 */
package server;

import java.net.ServerSocket;

import server.net.Server;


public class BullitinBoardServer {

	
	// help print for which the server admin can use to pick and choose what to define
	public static String helpprint()
	{
		return "arg 1: port definition, default 80"
			+  "arg 2: board width, default 500"
			+  "arg 3: board height, default 300";
	}
	
	// main method, based on arguments specified, align server to meet needs such as port specification
	public static void main(String []args) {
		if(args[0].contains("help")) { System.err.print(helpprint());}
		
		int port = 80;
		int boardwidth = 500;
		int boardheight = 300;
		
		
		
		switch(args.length) {
		case 1:
			port = Integer.parseInt(args[0]);
			break;
		case 2:
			port = Integer.parseInt(args[0]);
			boardwidth = Integer.parseInt(args[1]);
			break;
		case 3:
			port = Integer.parseInt(args[0]);
			boardwidth = Integer.parseInt(args[1]);
			boardheight = Integer.parseInt(args[2]);
			break;
		default:
			break;
			
		}
		
		
		new Server(port).Start();;
	}
}

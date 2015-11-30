/*
 * Name: Carlos Gonzalez
 * Date:9/3/2015
 * CSE 473: Introduction to Computer Networks
 * Washington University in St. Louis
 * Lab 1
 * 
 * Inputs: An IP address, a port number, and a String or String sequence.
 * Output: The response of the server to which the packet was sent to.
 * 
 * MapClient allows the user to send a UDP packet to any UDP server, although for the
 * purposes of this lab that server should be MapServer.
 * MapClient requires at least three inputs: the address of the server to be connected
 * to, the port number of the socket to bind to, and a String or sequence of Strings
 * to be sent to this server, which is the command. MapClient modifies the String inputs to the format
 * required by MapServer; it inserts a colon after each of the inputs after the third. 
 * No error checking for MapServer is done by MapClient.
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MapClient {

	public static void main(String args[]) throws Exception {

		//The Ip Address of the packet to send the packet to.
		InetAddress serverAdr = InetAddress.getByName(args[0]); 
		
		// The port number of the receiving server.
		int portNum = Integer.parseInt(args[1]); 
		
		String out = args[2]; // The packet payload in type String.

		// Modify the rest of the inputs, adding a colon after each.
		for (int i = 3; i < args.length; i++) {
			out = out + ":" + args[i];
		}

		// Open the datagramSocket
		DatagramSocket socket = new DatagramSocket();

		// The buffer is of size of the US-ASCII standard.
		byte[] outBuf = out.getBytes("US-ASCII");

		// Create the outgoing packet based on inputs and send it.
		DatagramPacket outPkt = new DatagramPacket(outBuf, outBuf.length,
				serverAdr, portNum);
		socket.send(outPkt);

		// Create buffer and packet for reply, then receive it.
		byte[] inBuf = new byte[1000];
		DatagramPacket inPkt = new DatagramPacket(inBuf, inBuf.length);
		socket.receive(inPkt);

		// Print buffer contents and close socket.
		String reply = new String(inBuf, 0, inPkt.getLength(), "US-ASCII");
		System.out.println(reply);
		socket.close();
	}
}

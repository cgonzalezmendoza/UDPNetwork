/*
 * Name: Carlos Gonzalez
 * Date:9/3/2015
 * CSE 473: Introduction to Computer Networks
 * Washington University in St. Louis
 * Lab 1
 * 
 * Inputs: (Optional) A port number to be used instead of the default 31357. 
 * Outputs: None.
 * 
 * MapServer creates a UDP Server, which allows the user to input the 
 * port number to be used when other applications bind to its socket. 
 * If not port number is selected, a default port number of 31357 is used.
 * The server itself stores (key,value) pairs, which can be modified by the 
 * clients accessing the server.
 * There are four commands that the server can interpret:
 * 
 * get(k):  Returns the value part of the pair whose key is k. A get command 
 * should be formatted as
 * get:this is the key string
 *          
 * put(k,v): Adds the pair (k,v) to the set, possibly replacing some other 
 * pair (k,x). A put command's format is
 * put:another key string:and the corresponding  value
 *   
 * swap(k1,k2): Swaps the values stored with the two keys. If either k1 or k2
 * is not found then the operation does nothing. A swap command should 
 * be formatted as
 * swap:key string 1:key string 2
 * 
 * remove(k): Deletes the pair (k,v) from the server. 
 * A remove command should be formatted as
 * remove:this is the key string
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class MapServer {

	// The HashMap container that will be used to store keys and values.
   private static HashMap<String, String> hmap =
		   new HashMap<String, String>();

	public static void main(String args[]) throws Exception {

		int port; // The port number to be used,
		String stringOutput = ""; // The returning data in String type
		byte[] bytesOutput; // The returning data in a bytes array.

		// Determine whether a port number was input or uses the default port
		// number 31357.
		if (args.length > 0) {
			port = Integer.parseInt(args[0]); // Default port number.
		} else {
			port = 31357;
		}

		// Open a datagram socket on specified port
		DatagramSocket socket = new DatagramSocket(port);

		// Create two packets, the incoming and the output packets, sharing the
		// same buffer.
		byte[] buf = new byte[1000];
		DatagramPacket inPkt = new DatagramPacket(buf, buf.length);
		DatagramPacket outPkt = new DatagramPacket(buf, buf.length);

		while (true) {
			// Wait for the incoming packet
			socket.receive(inPkt);

			// Read the payload of the incoming packet, and split it 
			// to identify its components.
			String rawData =
					new String(buf, 0, inPkt.getLength(), "US-ASCII");
			String[] splitData = rawData.split(":");

			// Error check the length of the input, if no key is given, display
			// an error.
			if (splitData.length < 2)
				stringOutput = "error: unrecognizable input: " + rawData;

			else {
				//Using a switch statement identify the command and execute it.
				switch (splitData[0]) {

				// If the get command is given in the correct format, get the
				// value of the given key.
				case "get":
					if (hmap.containsKey((splitData[1])))
						stringOutput = "succes:" + hmap.get((splitData[1]));
					else
						stringOutput = "no match";
					break;

				// If the put command is given in the correct format, store the
				// (key,value) pair.
				case "put":
					if (splitData.length < 3)
						stringOutput = "error: unrecognizable input: "
								+ rawData;
					else {
						if (hmap.containsKey((splitData[1])))
							stringOutput = "updated:" + splitData[1];
						else
							stringOutput = "success";
						hmap.put((splitData[1]), splitData[2]);
						break;
					}

					// If the remove command is given in the correct format,
					// remove the (key,value) pair.
				case "remove":
					if (hmap.containsKey((splitData[1]))) {
						hmap.remove((splitData[1]));
						stringOutput = "success";
					} else
						stringOutput = "no match";
					break;

				// If the swap command is given in the correct format,
				// swap the values of the two given keys.
				case "swap":
					if (splitData.length < 3)
						stringOutput = "error: unrecognizable input: "
								+ rawData;
					else {
						if (hmap.containsKey((splitData[1]))
								&& hmap.containsKey((splitData[2]))) {
							String t = hmap.get((splitData[1]));
							hmap.put((splitData[1]), hmap.get((splitData[2])));
							hmap.put((splitData[2]), t);
							stringOutput = "success";
						} else
							stringOutput = "no match";
					}
					break;

				// If the input is not identified as a valid command, 
			    // display an error.
				default:
					stringOutput = "error: unrecognizable input: " + rawData;
				}
			}
			// Change the type of the output to be able to be put in the
			// DatagramPacket object.
			bytesOutput = stringOutput.getBytes("US-ASCII");

			// Set the address, port, and length fields of the out packet to
			// return contents of the incoming packet to sender.
			outPkt.setData(bytesOutput);
			outPkt.setAddress(inPkt.getAddress());
			outPkt.setPort(inPkt.getPort());
			outPkt.setLength(bytesOutput.length);

			// Return the packet.
			socket.send(outPkt);

			// Reset the String output to prevent errors when returning other
			// packets.
			stringOutput = "";
		}
	}
}

/**
 * Project 7
 * @author eejiofor
 * @author rober230
 */

import java.util.Scanner;
import edu.purdue.cs.cs180.channel.*;

public class Requester implements MessageListener {
	private TCPChannel chann;

	public Requester(Channel c) {    
		c.setMessageListener(this);
		this.chann = (TCPChannel) c;
		prompt();

	}
	public void prompt()
	{

		Scanner s = new Scanner(System.in);
		System.out.printf("%s\n%s\n%s\n%s\n%s\n",
				"1. CL50 - Class of 1950 Lecture Hall",
				"2. EE - Electrical Engineering Building",
				"3. LWSN - Lawson Computer Science Building",
				"4. PMU - Purdue Memorial Union",
				"5. PUSH - Purdue University Student Health Center");

		System.out.print("Enter your location (1-5): ");
		String input = s.nextLine();

		try {
			int enter = Integer.parseInt(input);
			if (enter == 1) {
				try {
					chann.sendMessage("REQUEST CL50");
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				System.out.printf("Waiting for volunteer...\n");

			} 
			else if (enter == 2) {
				try {
					chann.sendMessage("REQUEST EE");
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				System.out.printf("Waiting for volunteer...\n");

			} 
			else if (enter == 3) {
				try {
					chann.sendMessage("REQUEST LWSN");
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				System.out.printf("Waiting for volunteer...\n");

			} 
			else if (enter == 4) {
				try {
					chann.sendMessage("REQUEST PMU");
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				System.out.printf("Waiting for volunteer...\n");

			} 
			else if (enter == 5) {
				try {
					chann.sendMessage("REQUEST PUSH");
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				System.out.printf("Waiting for volunteer...\n");

			} 
			else if (enter > 5 || enter < 0)
			{
				System.out.println("Invalid input. Please try again.");
				prompt();
			}
		} catch (Exception e) {
			System.out.println("Invalid number format. Please try again.");
			prompt();
		}
	}

	public static void main(String[] args) throws ChannelException {
		TCPChannel chan = null;
		try {
			chan = new TCPChannel("localhost", 1025);
		} catch (ChannelException e) {
			e.printStackTrace();
		}
		Requester r = new Requester(chan);
		//new TCPChannel(args[0], Integer.parseInt(args[1]))
	}

	public void messageReceived(String message, int clientID) {
		System.out.println("Volunteer " + message + " assigned and will arrive shorty");
		prompt();
	}
}
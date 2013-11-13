import java.util.Scanner;

import edu.purdue.cs.cs180.channel.*;

public class Volunteer implements MessageListener {
	private Channel chann;
	public Volunteer(Channel c) {
		c.setMessageListener(this);
		this.chann = c;
		prompt();
	}

	public void prompt()
	{
		Scanner s = new Scanner(System.in);
		System.out.print("Press ENTER when ready:");
		String enter = s.nextLine();
		if (enter.equals("")) {
			try {
				chann.sendMessage("VOLUNTEER " + chann.getID());
			} catch (ChannelException e) {
				e.printStackTrace();
			}
			System.out.println("Waiting for assignment...");
		}
	}

	public static void main(String[] args) {
		TCPChannel chan = null;
		try {
			chan = new TCPChannel("localhost", 1025);
		} catch (ChannelException e) {
			e.printStackTrace();
		}
		Volunteer v = new Volunteer(chan);
		//new TCPChannel(Integer.parseInt(args[0]))
	}

	public void messageReceived(String message, int clientID) {
		System.out.println("Procced to " + message);
		prompt();
	}
}

/**
 * Project 7
 * @author eejiofor
 * @author rober230
 */

import edu.purdue.cs.cs180.channel.*;

import java.util.*;

public class Server implements MessageListener {
	private Channel channel;
	private String algorithm;
	private ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();
	private ArrayList<Request> requests = new ArrayList<Request>();
	private final int[][] times = {{0, 8, 6, 5, 4}, {8, 0, 4, 2, 5}, {6, 4, 0, 3, 1}, 
								   {5, 2, 3, 0, 7}, {4, 5, 1, 7, 0}};

	public Server(Channel channel, String algorithm) {
		this.algorithm = algorithm;
		this.channel = channel;
		channel.setMessageListener(this);
	}

	class Request {
		final int id;
		final String location;
		final String urgency;

		Request(int id, String location, String urgency) {
			this.id = id;
			this.location = location;
			this.urgency = urgency;
		}
	}

	class Volunteer {
		final int id;
		final String location;

		Volunteer(int id, String location) {
			this.id = id;
			this.location = location;
		}
	}

	public int getTime(String loc)
	{
		int coord = 0;
		if (loc.equals("CL50"))
			coord = 0;
		if (loc.equals("EE"))
			coord = 1;
		if (loc.equals("LWSN"))
			coord = 2;
		if (loc.equals("PMU"))
			coord = 3;
		if (loc.equals("PUSH"))
			coord = 4;
		return coord;		
	}

	public void sendMessage(Volunteer v, Request r)
	{
		try {
			channel.sendMessage("LOCATION " + r.location + " " + r.urgency, v.id);
			channel.sendMessage("VOLUNTEER " + v.id + " " + times[getTime(v.location)][getTime(r.location)], r.id);
		} catch (ChannelException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void messageReceived(String message, int id) {
		String[] m = message.split(" ");

		if (algorithm.equals("FCFS"))
		{
			if (m[0].equals("REQUEST")) {
				requests.add(new Request(id, m[1], m[2]));
				Volunteer volunteer = volunteers.remove(0);
				Request request = requests.remove(0);
				sendMessage(volunteer, request);

			} else if (m[0].equals("VOLUNTEER")) {
				volunteers.add(new Volunteer(id, m[1]));
				Request request = requests.remove(0);
				Volunteer volunteer = volunteers.remove(0);
				sendMessage(volunteer, request);
			}
		}

		if (algorithm.equals("CLOSEST")) {
			if (m[0].equals("REQUEST")) {
				requests.add(new Request(id, m[1], m[2]));
				if (volunteers.size() > 1 && requests.size() == 1) {
					Request request = requests.remove(0);
					int temp = 10;
					int index = 0;
					for (int i = 0; i < volunteers.size(); i++)
					{
						Volunteer volunteer = volunteers.get(i);
						int curTime = times[getTime(volunteer.location)][getTime(request.location)];
						if (curTime < temp)
						{
							temp = curTime;
							index = i;
						}			
					}
					Volunteer volunteer = volunteers.remove(index);
					sendMessage(volunteer, request);
				}
				else {
					Volunteer volunteer = volunteers.remove(0);
					Request request = requests.remove(0);
					sendMessage(volunteer, request);
				}
			} else if (m[0].equals("VOLUNTEER")) {
				volunteers.add(new Volunteer(id, m[1]));
				if (requests.size() > 1 && volunteers.size() == 1) {
					Volunteer volunteer = volunteers.remove(0);
					int temp = 10;
					int index = 0;
					for (int i = 0; i < requests.size(); i++)
					{
						Request request = requests.get(i);
						int curTime = times[getTime(volunteer.location)][getTime(request.location)];
						if (curTime < temp)
						{
							temp = curTime;
							index = i;
						}			
					}
					Request request = requests.remove(index);
					sendMessage(volunteer, request);

				}
				else {
					Request request = requests.remove(0);
					Volunteer volunteer = volunteers.remove(0);
					sendMessage(volunteer, request);
				}
			}
		} else if (algorithm.equals("URGENCY")) {
			if (m[0].equals("REQUEST")) {
				requests.add(new Request(id, m[1], m[2]));
				if (volunteers.size() >= 1 && requests.size() == 1) {
					Volunteer volunteer = volunteers.remove(0);
					Request request = requests.remove(0);
					sendMessage(volunteer, request);	
				}
			} else if (m[0].equals("VOLUNTEER")) {
				volunteers.add(new Volunteer(id, m[1]));
				if (requests.size() > 1 && volunteers.size() == 1) {
					ArrayList<Request> emergency = new ArrayList<Request>();
					ArrayList<Request> urgent = new ArrayList<Request>();
					ArrayList<Request> normal = new ArrayList<Request>();
					Volunteer volunteer = volunteers.remove(0);
					for (int x = 0; x < requests.size(); x++) {
						if (requests.get(x).urgency.equals("EMERGENCY")) 
							emergency.add(requests.get(x));
						else if (requests.get(x).urgency.equals("URGENT"))
							urgent.add(requests.get(x));
						else if (requests.get(x).urgency.equals("NORMAL"))
							normal.add(requests.get(x));
					}
					if (emergency.isEmpty() == false)
					{
						sendMessage(volunteer, emergency.get(0));
						requests.remove(emergency.get(0));
					}
					else if (urgent.isEmpty() == false)
					{
						sendMessage(volunteer, urgent.get(0));
						requests.remove(urgent.get(0));
					}
					else if (normal.isEmpty() == false)
					{
						sendMessage(volunteer, normal.get(0));
						requests.remove(normal.get(0));
					}

				}
			}
		}
	}

	public static void main(String[] args) {
		Channel channel = new TCPChannel(Integer.parseInt(args[0]));
		new Server(channel, args[1]);
	}
}
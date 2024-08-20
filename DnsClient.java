import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress; // Only used for InetAddress.getByAddress(byte[] addr)
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DnsClient {

	public static void main(String[] args) throws Exception{
		Pattern ip_pattern = Pattern.compile("^@([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$");
		Pattern name_pattern = Pattern.compile("^(((?!\\-))(xn\\-\\-)?[a-z0-9\\-_]{0,61}[a-z0-9]{1,1}\\.)*(xn\\-\\-)?([a-z0-9\\-]{1,61}|[a-z0-9\\-]{1,30})\\.[a-z]{2,}$");
		
		int timeOut = 5;
		int maxRetries = 3;
		int port = 53;
		int reqType = 1;
		String server = "";
		String name = "";
		
		if(args.length > 9 || args.length < 2) {
			throw new IllegalArgumentException("Invalid number of arguments.");
		}
		boolean t = false;
		boolean r = false;
		boolean p = false;
		boolean flag = false;
		boolean ip = false;
		boolean servername = false;

		for(int i = 0; i < args.length; i++) {
			
			if(args[i].equals("-t")) {
				if(!t && i==0) {
					try {
						timeOut = Integer.parseInt(args[i+1]);					} catch (Exception e) {
						throw new IllegalArgumentException("Timeout must be an Integer."); 
					}
					i++;
					t = true;
				}
				else {
					throw new IllegalArgumentException("Wrong input syntax.");
				}
			}
			else if(args[i].equals("-r")) {
				if(!r && (i==0 || i == 2) && !p && !flag && !ip && !servername) {
					try {
						maxRetries = Integer.parseInt(args[i+1]);					
						} catch (Exception e) {
						throw new IllegalArgumentException("Maximum number of retries must be an Integer."); 
					}
					i++;
					r = true;
				}
				else {
					throw new IllegalArgumentException("Wrong input syntax.");
				}
			}
			else if(args[i].equals("-p")) {
				if(!p && (i==0 || i == 2 || i == 4) && !flag && !ip && !servername) {
					try {
						port = Integer.parseInt(args[i+1]);					
						} catch (Exception e) {
						throw new IllegalArgumentException("Port number must be an Integer."); 
					}
					i++;
					p = true;
				}
				else {
					throw new IllegalArgumentException("Wrong input syntax.");
				}
			}
			else if(args[i].equals("-mx")) {
				if(!flag && (i==0 || i == 2 || i == 4 || i == 6) && !ip && !servername) {
					reqType = 2;
					flag = true;
				}
				else {
					throw new IllegalArgumentException("Wrong input syntax.");
				}
			}
			else if(args[i].equals("-ns")) {
				if(!flag && (i==0 || i == 2 || i == 4 || i == 6) && !ip && !servername) {
					reqType = 3;
					flag = true;
				}
				else {
					throw new IllegalArgumentException("Wrong input syntax.");
				}
			}
			else if(args[i].startsWith("@")) {
				Matcher matcher = ip_pattern.matcher(args[i]);
				if(matcher.matches()) {
				if(!ip && !servername) {
					server = args[i].substring(1);
					ip = true;
				}
				else {
					throw new IllegalArgumentException("Wrong input syntax.");
				}
				}
				else {
					throw new IllegalArgumentException("Invalid IP Address.");
				}
			}
			else if(!servername) {
				Matcher matcher = name_pattern.matcher(args[i]);
				if(matcher.matches()) {
					name = args[i];
					servername = true;
				}
				else {
					throw new IllegalArgumentException("Invalid name.");
				}
			}
			else {
				throw new IllegalArgumentException("Wrong input syntax.");
			}
		}
		
		if((!ip) || (!servername)) {
			throw new IllegalArgumentException("Missing IP Address or name.");
		}
		
		System.out.println("DnsClient sending request for " + name);
		System.out.println("Server: " + server);
		String type = "";
		if(reqType == 1) type = "A";
		else if (reqType == 2) type = "MX";
		else type = "NS";
		System.out.println("Request type: " + type);
		
		
		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(timeOut * 1000);
		String[] addr_str = server.split("\\.");
		byte[] addr = {(byte)(Integer.parseInt(addr_str[0])),
				(byte)(Integer.parseInt(addr_str[1])),
				(byte)(Integer.parseInt(addr_str[2])),
				(byte)(Integer.parseInt(addr_str[3]))};
		InetAddress iPAddress = InetAddress.getByAddress(addr);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		PacketHeader head = new PacketHeader();
		Question q = new Question(name, reqType);
		Packet packet = new Packet(head, q);
		
		sendData = Packet.tobyteArray(packet.bts);
		
		DatagramPacket sendPacket =
				new DatagramPacket(sendData, sendData.length, iPAddress, port);
		DatagramPacket receivePacket =
				new DatagramPacket(receiveData, receiveData.length);
		int currentRetries = 0;
		
		long startTime = System.currentTimeMillis();
		while(currentRetries <= maxRetries) {
			try {
				if(currentRetries > 0) {
					System.out.println("Timeout. Retrying...");
				}
				clientSocket.send(sendPacket);
				clientSocket.receive(receivePacket);
				break;
			} catch (Exception e) {
				currentRetries += 1;
			}
		}
		if(currentRetries > maxRetries) {
		System.out.println("ERROR \t Maximum number of retries " + Integer.toString(maxRetries) + " exceeded");
		}
		else {
			long elapsed = System.currentTimeMillis() - startTime;
			System.out.println("Response received after "+ Float.toString(((float)elapsed) / 1000) + " seconds (" + Integer.toString(currentRetries)+ " retries)");

			Packet pc = new Packet();
			try {
				pc = new Packet(receivePacket.getData());
			} catch (Exception e) {
				System.out.println("ERROR \t Invalid response ");
			}
			if(pc.answers.length == 0) {
				System.out.println("NOTFOUND");
			}
			else {
				System.out.println("***Answer Section ("+ Integer.toString(pc.answers.length) + " records)***");
				for(int i = 0; i < pc.answers.length;i++) {
					System.out.println(Packet.interpret(pc.answers[i], pc.header.AA));
				}
			}
			if(pc.additional.length > 0) {
				System.out.println("***Additional Section ("+ Integer.toString(pc.additional.length) + " records)***");
				for(int i = 0; i < pc.additional.length ;i++) {
					System.out.println(Packet.interpret(pc.additional[i], pc.header.AA));
				}
			}
			clientSocket.close();
		}
	}
	
}

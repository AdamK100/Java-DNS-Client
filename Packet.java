import java.util.ArrayList;

public class Packet {
	
	PacketHeader header;
	Question[] questions;
	Answer[] answers;
	Answer[] additional;
	ArrayList<Byte> bts = new ArrayList<Byte>();
	
	//Given a packet header and a question, returns a Packet for a query.
	public Packet(PacketHeader header, Question q) {
		Question[] qs = { q };
		this.header = header;
		this.questions = qs;
		//Adding the header and question sections' bytes to the packet byte array.
		bts.addAll(header.hBytes);
		bts.addAll(q.qBytes);
	}
	
	//Given a byte array, returns a Packet object.
	public Packet(byte[] b){
		
		//Parsing the header
		PacketHeader h = new PacketHeader();
		h.id = byte2short(b[0], b[1]);
		h.qr = getBits(b[2],7,1);
		h.opcode =  getBits(b[2],6,4);
		h.AA = getBits(b[2], 2, 1);
		h.TC = getBits(b[2], 1, 1);
		h.RD = getBits(b[2], 0, 1);
		h.RA = getBits(b[3], 7, 1);
		h.Z = getBits(b[3], 6, 3);
		h.rCode = getBits(b[3], 3, 4);
		h.qdCount = byte2short(b[4], b[5]);
		h.anCount = byte2short(b[6],b[7]);
		h.nsCount = byte2short(b[8], b[9]);
		h.arCount = byte2short(b[10], b[11]);
		
		//Parsing question(s)
		Question[] qs = new Question[h.qdCount];
		int i = 12;
		for(int j = 0; j < qs.length; j++) {
			Question q = new Question();
			String s = byte2String(b, i);
			String[] tks = s.split("\\.");
			i = Integer.parseInt(tks[tks.length - 1]);
			q.name = s.replaceFirst("\\.[0-9]?[0-9]?[0-9]?[0-9]$", "");
			q.type = byte2short(b[i], b[i+1]);
			i += 4;
			qs[j] = q;
		}
		
		//Parsing the answer records
		Answer[] as = new Answer[h.anCount];
		
		for(int j = 0; j < as.length; j++) {
			Answer a = new Answer();
			
			String s = byte2String(b, i);
			String[] tks = s.split("\\.");
			i = Integer.parseInt(tks[tks.length - 1]);
			a.name = s.replaceFirst("\\.[0-9]?[0-9]?[0-9]?[0-9]$", "");
			
			a.type = byte2short(b[i], b[i+1]);
			i += 4;
			a.tTL = byte2int(b[i], b[i+1], b[i+2], b[i+3]);
			i += 4;
			a.rdLength = byte2short(b[i], b[i+1]);
			i += 2;
			
			if(a.type == 0x0001) {
				a.rData = Integer.toString(b[i] & 0xFF) + "." 
			+ Integer.toString(b[i+1] & 0xFF) + "." 
				+ Integer.toString(b[i+2] & 0xFF) + "." +
				Integer.toString(b[i+3] & 0xFF);
				i += 4;
			}
			else {
				if(a.type == 0x000f) {
					a.pref = byte2short(b[i], b[i+1]);
					i += 2;
				}
				
				String s2 = byte2String(b, i);
				String[] tks2 = s2.split("\\.");
				i = Integer.parseInt(tks2[tks2.length - 1]);
				a.rData = s2.replaceFirst("\\.[0-9]?[0-9]?[0-9]?[0-9]$", "");
				
			}
			as[j] = a;
		}
		//Skip Authority section by not storing any readings. The index is incremented to read the Additional section.
		for(int j = 0; j < h.nsCount; j++) {
			String s = byte2String(b, i);
			String[] tks = s.split("\\.");
			i = Integer.parseInt(tks[tks.length - 1]);
			int type = byte2short(b[i], b[i+1]);
			i += 4;
			i += 4;
			i += 2;
			if(type == 0x0001) {
				i += 4;
			}
			else {
				if(type == 0x000f) {
					i += 2;
				}
				String s2 = byte2String(b, i);
				String[] tks2 = s2.split("\\.");
				i = Integer.parseInt(tks2[tks2.length - 1]);
			}
		}
		//Parsing any Additional records
		Answer[] ads = new Answer[h.arCount];
		
		for(int j = 0; j < ads.length; j++) {
			Answer ad = new Answer();
			
			String s = byte2String(b, i);
			String[] tks = s.split("\\.");
			i = Integer.parseInt(tks[tks.length - 1]);
			ad.name = s.replaceFirst("\\.[0-9]?[0-9]?[0-9]?[0-9]$", "");
			
			ad.type = byte2short(b[i], b[i+1]);
			i += 4;
			ad.tTL = byte2int(b[i], b[i+1], b[i+2], b[i+3]);
			i += 4;
			ad.rdLength = byte2short(b[i], b[i+1]);
			i += 2;
			
			if(ad.type == 0x0001) {
				ad.rData = Integer.toString(b[i] & 0xFF) + "." 
			+ Integer.toString(b[i+1] & 0xFF) + "." 
				+ Integer.toString(b[i+2] & 0xFF) + "." +
				Integer.toString(b[i+3] & 0xFF);
				i += 4;
			}
			else {
				if(ad.type == 0x000f) {
					ad.pref = byte2short(b[i], b[i+1]);
					i += 2;
				}
				
				String s2 = byte2String(b, i);
				String[] tks2 = s2.split("\\.");
				i = Integer.parseInt(tks2[tks2.length - 1]);
				ad.rData = s2.replaceFirst("\\.[0-9]?[0-9]?[0-9]?[0-9]$", "");
				
			}
			ads[j] = ad;
		}
		
		//Setting up the packet using parsed data
		this.header = h;
		this.questions = qs;
		this.answers = as;
		this.additional = ads;
	}
	
	//Creates an empty packet.
	public Packet() {
		
	}
	//Concatenates 2 successive bytes to return a 16-bit positive number.
	public static int byte2short(byte b1, byte b2) {
		return (((b1 & 0xFF) << 8) + (b2 & 0xFF));
	}
	//Concatenates 4 successive bytes to return a 32-bit positive number.
	public static long byte2int(byte b1, byte b2, byte b3, byte b4) {
		return ((b1 & 0xFF) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF);
	}
	public static String byte2String(byte[] b, int i) {
		String s = "";
		int seqSize = b[i] & 0xFF;
		while(seqSize != 0) {
			if(seqSize >>> 6 == 3) {
				int ind = byte2short(getBits(b[i], 5, 6),b[i+1]);
				String str = byte2String(b, ind);
				str = str.replaceFirst("\\.[0-9]?[0-9]?[0-9]?[0-9]$", "");
				s += str + ".";
				i += 1;
				break;
			}
			else {
				i += 1;
				for(int j = 0; j < seqSize; j++) {
					s += (char)(b[i+j]);
				}
				i += seqSize;
				seqSize = b[i] & 0xFF;
				s += ".";
			}
		}
		s += Integer.toString(i+1);
		return s;
	}
	//Given a byte, a number p (7 - 0) and a number n (p+1 - 1), reads n bits at position p.
	public static byte getBits(byte b, int position, int num)
	{
	   return (byte) ((b >>> position + 1 - num) & (0b11111111 >>> (8-num)));
	}
	//Converts an ArrayList of Byte to an array of byte.
	public static byte[] tobyteArray(ArrayList<Byte> b) {
		
		Byte[] bbs = new Byte[b.size()];
		bbs = b.toArray(bbs);
		byte[] bs = new byte[b.size()];
		for(int i = 0; i < bbs.length; i++) {
			bs[i] = (bbs[i]).byteValue();
		}
		return bs;
	}
	//Interprets an Answer record (prints to console).
	public static String interpret(Answer ans, int auth) {
		String s = "";
		if(ans.type == 0x0001) {
			s += "IP \t " + ans.rData + " \t " + Long.toString(ans.tTL);
		}
		else if (ans.type == 0x0002) {
			s += "NS \t " + ans.rData + " \t " + Long.toString(ans.tTL);
		}
		else if(ans.type == 0x000f) {
			s += "MX \t " + ans.rData + " \t " + Integer.toString(ans.pref)
		+ " \t " + Long.toString(ans.tTL);
		}
		else if(ans.type == 0x0005) {
			s += "CNAME \t " + ans.rData + " \t " + Long.toString(ans.tTL);
		}
		else {
			return "ERROR \t Cannot parse record.";
		}
		if(auth == 0) {
			s += " \t nonauth";
		}
		else {
			s += " \t auth";
		}
		return s;
	}
}

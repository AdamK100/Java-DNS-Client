import java.util.ArrayList;
import java.util.Random;
public class PacketHeader {
	public int id;
	public int qr;
	public int opcode;
	public int AA;
	public int TC;
	public int RD;
	public int RA;
	public int Z;
	public int rCode;
	public int qdCount = 0x0001;
	public int anCount;
	public int nsCount;
	public int arCount;
	ArrayList<Byte> hBytes = new ArrayList<Byte>();
	Random random = new Random();
	public PacketHeader() {
		id = (short)random.nextInt(Short.MAX_VALUE + 1);
		//ID
		hBytes.add((byte)(id >>> 8));
		hBytes.add((byte)id);
		//QR - OPCODE - AA - TC - RD
		hBytes.add((byte)1);
		//RA - Z - RCODE
		hBytes.add((byte)0);
		//QDCOUNT
		hBytes.add((byte)0);
		hBytes.add((byte)1);
		//ANCOUNT
		hBytes.add((byte)0);
		hBytes.add((byte)0);
		//NSCOUNT
		hBytes.add((byte)0);
		hBytes.add((byte)0);
		//ARCOUNT
		hBytes.add((byte)0);
		hBytes.add((byte)0);
	}
}

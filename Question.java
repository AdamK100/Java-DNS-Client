import java.util.ArrayList;

public class Question {
	//byte array representing the Question.
	public ArrayList<Byte> qBytes = new ArrayList<Byte>();
	//Domain name to inquire about, such as mcgill.ca
	String name;
	//Type of query: 1 for an A query, 2 for an MX query, 3 for an NS query.
	Integer type;
	//Question class field, which is constant.
	Integer clas = 0x0001;
	
	//Given a domain name and a query type, returns a Question object with the appropriate byte array.
	public Question(String name, int reqType) {
		this.name = name;
		String[] words = name.split("\\.");
		for(String word : words) {
			qBytes.add((byte)word.length());
			for(char c : word.toCharArray()) {
				qBytes.add((byte)c);
			}
		}
		qBytes.add((byte)0);
		qBytes.add((byte)0);
		if(reqType == 1) {
			qBytes.add((byte)1);
			this.type = 1;
		}else if(reqType == 3) {
			qBytes.add((byte)2);
			this.type = 2;
		}else {
			qBytes.add((byte)15);
			this.type = 15;
		}
		qBytes.add((byte)0);
		qBytes.add((byte)1);
	}
	public Question() {
		
		
	}
}

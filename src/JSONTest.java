import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JSONTest {
	
	public static class TestClass{
		Date fDate;
		int fp1Score;
		int fp2Score;
		int fTurnCnt;
		public TestClass(Date d, int p1, int p2, int tc) {
			this.fDate=d;
			this.fp1Score=p1;
			this.fp2Score=p2;
			this.fTurnCnt=tc;
		}
		public Date getfDate() {
			return fDate;
		}
		public void setfDate(Date fDate) {
			this.fDate = fDate;
		}
		public int getFp1Score() {
			return fp1Score;
		}
		public void setFp1Score(int fp1Score) {
			this.fp1Score = fp1Score;
		}
		public int getFp2Score() {
			return fp2Score;
		}
		public void setFp2Score(int fp2Score) {
			this.fp2Score = fp2Score;
		}
		public int getfTurnCnt() {
			return fTurnCnt;
		}
		public void setfTurnCnt(int fTurnCnt) {
			this.fTurnCnt = fTurnCnt;
		}
	}
	
	
	
	
	

	public static void main(String[] args) throws StreamWriteException, DatabindException, IOException {
		ObjectMapper om = new ObjectMapper();
		ArrayList<JsonNode> jnarr = new ArrayList<JsonNode>();
		TestClass testc1 = new TestClass(new Date(),1,2,3);
		File f = new File("./jsontest.json");
		if(f.createNewFile())
			om.writeValue(f, testc1);
			
		else {
			JsonNode testN = om.readTree(f);
			System.out.println(testN.get(0).getClass());
		}
		
		

	}

}




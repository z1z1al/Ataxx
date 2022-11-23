import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;





public class AppendTest {
	public static class GameHistoryData{
		int histTurn;
		int histp1Score;
		int histp2Score;
		Date histDate;
		public GameHistoryData(@JsonProperty("histTurn") int ht, @JsonProperty("histp1Score") int h1,
				@JsonProperty("histp2Score") int h2, @JsonProperty("histDate") Date hd) {
			this.histTurn=ht;
			this.histp1Score=h1;
			this.histp2Score=h2;
			this.histDate=hd;
		}
		public int getHistTurn() {
			return histTurn;
		}
		public void setHistTurn(int histTurn) {
			this.histTurn = histTurn;
		}
		public int getHistp1Score() {
			return histp1Score;
		}
		public void setHistp1Score(int histp1Score) {
			this.histp1Score = histp1Score;
		}
		public int getHistp2Score() {
			return histp2Score;
		}
		public void setHistp2Score(int histp2Score) {
			this.histp2Score = histp2Score;
		}
		public Date getHistDate() {
			return histDate;
		}
		public void setHistDate(Date histDate) {
			this.histDate = histDate;
		}
	}
	
	
	static ArrayList<GameHistoryData> arrTest = new ArrayList<GameHistoryData>();
	static ObjectMapper om = new ObjectMapper();
	
	

	public static void main(String[] args) {
		
		GameHistoryData dat1 = new GameHistoryData(1, 2, 3, new Date());
		GameHistoryData dat2 = new GameHistoryData(5, 5, 5, new Date());
		
		File f = new File("./htest.json");
		try {
			if(f.createNewFile()) {
				arrTest.add(dat1);
				om.writeValue(f,arrTest);
			}
			else {
				arrTest = om.readValue(f, new TypeReference<ArrayList<GameHistoryData>>(){});
				arrTest.add(dat2);
				om.writeValue(f, arrTest);
			}
		} catch (IOException e) {
			System.out.println("fail.");
			System.exit(0);
		}

		System.out.println("done.");
	}

}

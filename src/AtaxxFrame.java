import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;
//git test
public class AtaxxFrame extends JFrame{
	// 현재 커서의 좌표값
	static int idxX = 0;
	static int idxY = 0;
	
	// 감염 로직용 인덱스 변수
	static int startIdxX;
	static int endIdxX;
	static int startIdxY;
	static int endIdxY;
	
	// 선택한 말의 좌표가 저장되는 변수
	static int selectX;
	static int selectY;
	
	// 게임의 실질적인 정보를 담은 2차원 배열
	static int matrix[][] = {	{1,0,0,0,0,0,2},
								{0,0,0,0,0,0,0},
								{0,0,0,0,0,0,0},
								{0,0,0,0,0,0,0},
								{0,0,0,0,0,0,0},
								{0,0,0,0,0,0,0},
								{2,0,0,0,0,0,1}		};
	
	// 컴포넌트를 담을 2차원 배열들
	static AtaxxPiece[][] pieceArr = new AtaxxPiece[7][7];
	static JButton[] titleBtnArr = new JButton[4];
	static JButton[] pauseBtnArr = new JButton[3];
	
	// 각 플레이어의 말 개수(점수)
	static int p1Score = 0;
	static int p2Score = 0;
	
	// 플레이어의 차례: 1일시 1p, 2일시 2p
	static int playerNum = 1;
	
	// 누적 턴
	static int turnCount = 1;
	
	// 말 선택 여부
	static boolean selected = false;
	
	
	// 각 플레이어의 점수, 차레, 턴 수, 승자를 표시할 컴포넌트
	static JLabel p1Label = new JLabel("1P:   " + p1Score);
	static JLabel p2Label = new JLabel("2P:   " + p2Score);
	static JLabel pLabel = new JLabel(playerNum + "P의 차례!");
	static JLabel turnLabel = new JLabel("턴 수:   " + turnCount);
	static JLabel winnerLabel = new JLabel();
	
	// 게임 메인 패널
	static JPanel mainGamePanel;
	
	// 패널로 만들 게임 화면의 개수
	static final int NUMBER_PANELS = 5;
	
	// 패널을 구분지어 화면 전환과 포커스를 주기 위한 변수들
	// 패널 종류: 0.타이틀 1.메인게임 2.일시정지 3.게임결과 4.게임기록
	static int panelNum = 0;
	static JPanel[] panelArr = new JPanel[NUMBER_PANELS];
	
	// 색상에 쓰일 투명도(알파값)
	static final int ALPHA_VAL = 200;
	
	
	// json 변환을 위한 jackson 라이브러리의 ObjectMapper 객체, 재사용 가능하기에 static 선언
	static ObjectMapper om = new ObjectMapper();
	
	// 게임 기록을 동적으로 할당하기 위한 ArrayList, json 직렬화와 역직렬화에 이용
	static ArrayList<GameHistoryData> histArr = new ArrayList<GameHistoryData>();
	
		
	// TODO 게임 기록 데이터 >> txt 파일로 턴 수, 1P 점수, 2P 점수, 날짜 순으로 한 레코드씩 저장
	
	// TODO 게임 기록 확인 만들기 (메인 화면에 추가)
	// 1. 턴 수 변수를 따로 또 만들어서 이걸 기록하면 될듯 점수랑 같이 >>>>>>>>  
	// TODO 정렬 기준은 날짜로(어케하지)
	// 2. 이걸 기록할 때 오름차순 정렬로 기록 >> 자료구조 활용 (자바 내 기능 이용X)
	
	// TODO 정렬 > 쓸 때 하는 것보다 읽을 때 하는 게 더 빠를듯(쓸 때 정렬하려면 매번 파일을 읽어들여야함)
	// TODO 기록과 세이브 파일을 JSON	 포맷 스터디를 통해 JSON으로 구현, 정렬도
	// TODO 기록, 정렬 수행 후 JDBC 연동 구현 >> 정렬은 미루고 DB 통해서 정렬하는 쪽으로
	
	// JDBC까지 완료하면 추가적으로 구현할 기능을 골라서 구현 (네트워킹 or AI대전 or 졸작준비)
	// 이건 필수인 진 모르겠지만 키 입력을 받는 부분에서 OS를 판별하여 처리할 수 있게끔(맥os에서 방향키 인식을 못함;)
	
	
	/*		단축키
	 * 
	 * 컨쉬 넘/	모두 접기
	 * 컨쉬 넘*	모두 펼치기
	 * 알트 좌	이전 위치
	 * 알트 우	다음 위치
	 * 컨알 상하	한 줄 복붙
	 * 컨 D		한 줄 삭제
	 * 컨 Q		마지막 수정 위치
	 * 알트 상하	한 줄 이동
	 * 컨쉬 _		상하 스플릿
	 * 컨쉬 {		좌우 스플릿
	 * 
	 * 		디버그
	 * 
	 * F5		함수 들어가기
	 * F7		함수 나오기
	 * F6		한 줄 실행
	 * 컨 R		현재 라인까지 실행
	 * 
	 */
	
	
	
	public AtaxxFrame() {
		// 메인 프레임 설정
		setTitle("Ataxx");
		setSize(700,800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);

		
		// 타이틀 화면을 담는 패널
		JPanel titlePanel_0 = new JPanel(null);
		panelArr[0] = titlePanel_0;
		titlePanel_0.setBounds(0,0,700,800);
		titlePanel_0.addKeyListener(new TitleKeyListner());
		titlePanel_0.setFocusable(true);
		titlePanel_0.setVisible(true);

		
		
		// 타이틀 로고와 버튼들
		JLabel titleLogo = new JLabel("세 균 전");
		titleLogo.setBounds(160,40,400,150);
		titleLogo.setFont(new Font("sans",Font.BOLD,100));
		titlePanel_0.add(titleLogo);
		titleBtnArr[0] = new JButton("게임 시작"); 
		titleBtnArr[0].setBounds(140,300,400,70);
		titleBtnArr[1] = new JButton("게임 불러오기");
		titleBtnArr[1].setBounds(140,410,400,70);
		titleBtnArr[2] = new JButton("게임 기록 확인(미구현ㅠ)");
		titleBtnArr[2].setBounds(140,520,400,70);
		titleBtnArr[3] = new JButton("게임 종료");
		titleBtnArr[3].setBounds(140,630,400,70);
		setBtnColor(titleBtnArr,0);
		for(int i=0;i<titleBtnArr.length;i++) {
			titleBtnArr[i].setFont(new Font("sans",Font.BOLD,25));
			titleBtnArr[i].setFocusable(false);
			titlePanel_0.add(titleBtnArr[i]);
		}		
			
		
		
		// 게임 화면을 담는 패널
		JPanel gamePanel_1 = new JPanel(null);
		panelArr[1] = gamePanel_1;
		gamePanel_1.setBounds(1,1,690,790);
		gamePanel_1.addKeyListener(new MainKeyListner());
		gamePanel_1.setFocusable(true);
		gamePanel_1.setVisible(false);

		
		// 게임 화면을 표시할 패널
		GridLayout grid = new GridLayout(7,7);
		grid.setVgap(1);
		grid.setHgap(1);
		mainGamePanel = new JPanel(grid);
		gamePanel_1.add(mainGamePanel);
		mainGamePanel.setBounds(45,50,600,600);
		mainGamePanel.setBackground(new Color(000));
		// 게임 점수를 표시할 패널
		JPanel scorePanel = new JPanel();
		gamePanel_1.add(scorePanel);
		scorePanel.setLayout(null);
		scorePanel.setBounds(45,665,600,80);
		scorePanel.setBackground(new Color(190,190,190));
		// 레이블 컴포넌트 속성 설정
		p1Label.setBounds(95,30,100,20);
		p1Label.setFont(new Font("sans",Font.BOLD,23));
		p1Label.setForeground(new Color(190,51,51));
		p2Label.setBounds(440,30,100,20);
		p2Label.setFont(new Font("sans",Font.BOLD,23));
		p2Label.setForeground(new Color(51,51,190));
		pLabel.setBounds(263,10,100,20);
		pLabel.setFont(new Font("sans",Font.PLAIN,17));
		turnLabel.setBounds(275,50,100,20);
		turnLabel.setFont(new Font("sans",Font.PLAIN,13));
		scorePanel.add(p1Label);
		scorePanel.add(p2Label);
		scorePanel.add(pLabel);
		scorePanel.add(turnLabel);
		
		
		// 일시정지 패널
		JPanel pausePanel_2 = new JPanel(null);
		panelArr[2] = pausePanel_2;
		pausePanel_2.setBounds(145,150,400,400);
		pausePanel_2.setBackground(new Color(80,80,80,ALPHA_VAL));
		pausePanel_2.addKeyListener(new PauseKeyListner());
		pausePanel_2.setFocusable(true);
		pausePanel_2.setVisible(false);
		
		
		// 일시정지 문구와 버튼들
		JLabel pauseLogo = new JLabel("PAUSE");
		pauseLogo.setBounds(115,20,200,70);
		pauseLogo.setFont(new Font("sans",Font.BOLD,50));
		pauseLogo.setForeground(new Color(255,255,255,ALPHA_VAL));
		pausePanel_2.add(pauseLogo);
		pauseBtnArr[0] = new JButton("게임 재개"); 
		pauseBtnArr[0].setBounds(100,140,200,50);
		pauseBtnArr[1] = new JButton("게임 저장");
		pauseBtnArr[1].setBounds(100,220,200,50);
		pauseBtnArr[2] = new JButton("게임 종료");
		pauseBtnArr[2].setBounds(100,300,200,50);
		setBtnColor(pauseBtnArr,0);
		for(int i=0;i<pauseBtnArr.length;i++) {
			pauseBtnArr[i].setFont(new Font("sans",Font.PLAIN,18));
			pauseBtnArr[i].setFocusable(false);
			pausePanel_2.add(pauseBtnArr[i]);
		}	
		
		
		// 게임 결과 패널
		JPanel resultPanel_3 = new JPanel(null);
		panelArr[3] = resultPanel_3;
		resultPanel_3.setBounds(145,250,400,200);
		resultPanel_3.setBackground(new Color(230,230,0,ALPHA_VAL));
		// TODO 추후 타이틀로 돌아가기 추가할 것
		resultPanel_3.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				System.exit(0);
			}
		});
		resultPanel_3.setFocusable(true);
		resultPanel_3.setVisible(false);
		// 결과창에 보일 레이블들
		winnerLabel.setBounds(90,20,250,50);
		winnerLabel.setFont(new Font("sans",Font.BOLD,40));
		JLabel pressAnyKey = new JLabel("아무 키나 누르면 종료됩니다...");
		pressAnyKey.setBounds(115,150,200,20);
		pressAnyKey.setFont(new Font("sans",Font.PLAIN,13));
		resultPanel_3.add(winnerLabel);
		resultPanel_3.add(pressAnyKey);
		
		
		// TODO 게임 기록 확인 패널(실 기능 추후 구현)
		JPanel historyPanel_4 = new JPanel(null);
		panelArr[4] = historyPanel_4;
		historyPanel_4.setBounds(0,0,700,800);
		historyPanel_4.setBackground(new Color(80,80,80));
//		historyPanel_4.addKeyListener(new PauseKeyListner());
		historyPanel_4.setFocusable(true);
		historyPanel_4.setVisible(false);
		
		
		// 투명도가 있는 컴포넌트를 먼저 add시켜줌 필요가 있어 역순으로 add (먼저 add한 컴포넌트가 앞쪽에 표시됨)
		this.add(historyPanel_4);
		this.add(resultPanel_3);
		this.add(pausePanel_2);
		this.add(gamePanel_1);
		this.add(titlePanel_0);
		
		
		// 메인 프레임 표시
		setSize(700,800);
		setVisible(true);
		
	}
	

	// JPanel으로 구현한 게임의 말
	public static class AtaxxPiece extends JLabel{
		
		// 이미지 출력 시 기준이 될 변수 (0,1,2)
		int statusNumber;
		// 버튼의 좌표
		String position;
		
		// 이미지 객체 생성
		ImageIcon p1Img = new ImageIcon("src\\img\\Ataxx_1P.png");
		ImageIcon p2Img = new ImageIcon("src\\img\\Ataxx_2P.png");
		ImageIcon emptyImg = new ImageIcon("src\\img\\Ataxx_empty.png");
		ImageIcon p1Img_Sel = new ImageIcon("src\\img\\Ataxx_1P_selected.png");
		ImageIcon p2Img_Sel = new ImageIcon("src\\img\\Ataxx_2P_selected.png");
		ImageIcon emptyImg_Sel = new ImageIcon("src\\img\\Ataxx_empty_selected.png");
		
		
		// 최초 생성 시에 좌표를 받아 그에 맞게 버튼을 생성
		public AtaxxPiece(int pIdxY, int pIdxX) {
			statusNumber = matrix[pIdxY][pIdxX];
			position = String.format("%d%d",pIdxY,pIdxX);
		}
		
		
		// statusNumber로 각각에 맞는 이미지 출력
		public void setImage() {
			
			// 선택된 말이나 현재 커서 위치의 이미지는 다르게 출력
			if( position.equals(String.format("%d%d",idxY,idxX)) ||
				(selected && position.equals(String.format("%d%d",selectY,selectX))) ) {
				switch(statusNumber) {
				case 0:
					setIcon(emptyImg_Sel);
					break;
				case 1:
					setIcon(p1Img_Sel);
					break;
				case 2:
					setIcon(p2Img_Sel);
					break;
				}
			}
			else {
				switch(statusNumber) {
				case 0:
					setIcon(emptyImg);
					break;
				case 1:
					setIcon(p1Img);
					break;
				case 2:
					setIcon(p2Img);
					break;
				}
			}
				 
		}
		
	}
	
	
	// 세이브 데이터 클래스
	public static class GameSaveData{
		int saveMatrix[][];
		int saveTurn;
		int savePnum;
		int saveIdxX;
		int saveIdxY;
		
		public GameSaveData(int sm[][],int st, int sp, int sx, int sy) {
			this.saveMatrix = sm;
			this.saveTurn = st;
			this.savePnum = sp;
			this.saveIdxX = sx;
			this.saveIdxY = sy;
		}

		public int[][] getSaveMatrix() {
			return saveMatrix;
		}

		public void setSaveMatrix(int[][] saveMatrix) {
			this.saveMatrix = saveMatrix;
		}

		public int getSaveTurn() {
			return saveTurn;
		}

		public void setSaveTurn(int saveTurn) {
			this.saveTurn = saveTurn;
		}

		public int getSavePnum() {
			return savePnum;
		}

		public void setSavePnum(int savePnum) {
			this.savePnum = savePnum;
		}

		public int getSaveIdxX() {
			return saveIdxX;
		}

		public void setSaveIdxX(int saveIdxX) {
			this.saveIdxX = saveIdxX;
		}

		public int getSaveIdxY() {
			return saveIdxY;
		}

		public void setSaveIdxY(int saveIdxY) {
			this.saveIdxY = saveIdxY;
		}
	}
	
	
	// 게임 기록 클래스
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
	
	
	// 게임 조작
	class MainKeyListner extends KeyAdapter{
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			// 상하좌우 입력 시 이동
			switch(keyCode) {
			case KeyEvent.VK_0:
				//테스트용 강제 승리
				gameResult(1);
			case KeyEvent.VK_UP:
				if(idxY != 0)  
					idxY -= 1; 
				break;
			case KeyEvent.VK_DOWN:
				if(idxY != 6)  
					idxY += 1; 
				break;
			case KeyEvent.VK_LEFT:
				if(idxX != 0)  
					idxX -= 1;  
				break;
			case KeyEvent.VK_RIGHT:
				if(idxX != 6)  
					idxX += 1;  
				break;
			case KeyEvent.VK_ENTER:
				logic();
				break;
			case KeyEvent.VK_ESCAPE:
				// 일시정지 메뉴
				panelChange(2,true);
				break;
			}

			drawing();
		}
	}
	
	
	// 타이틀 메뉴 조작
	class TitleKeyListner extends KeyAdapter{
		// 0: 게임시작 1: 불러오기 2: 게임기록 3: 종료
		int menuIdx = 0;
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch(keyCode) {
			case KeyEvent.VK_UP:
				if(menuIdx > 0) 
					menuIdx--;				
				break;
			case KeyEvent.VK_DOWN:
				if(menuIdx < 3) 
					menuIdx++;
				break;
			case KeyEvent.VK_ENTER:
				
				switch(menuIdx) {
				// 게임 시작
				case 0: 				
					// 말 생성, 초기화
					initPieces(mainGamePanel);
					panelChange(1,false);
					break;
					
				// 게임 불러오기
				case 1: doLoad();
					// 말 생성, 초기화
					initPieces(mainGamePanel);
					panelChange(1,false);
					break;
					
				// 게임 기록 확인
				case 2: panelChange(4,false);
					break;
					
				// 게임 종료
				case 3: System.exit(0);
				}
				
				break;
			}
			setBtnColor(titleBtnArr,menuIdx);
			
		}
	}
	
	
	// 일시정지 메뉴 조작
	class PauseKeyListner extends KeyAdapter{
		// 0: 게임재개 1: 저장하기 2: 종료
		int menuIdx = 0;
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			
			switch(keyCode) {
			case KeyEvent.VK_UP:
				if(menuIdx > 0)
					menuIdx--;
				break;
			case KeyEvent.VK_DOWN:
				if(menuIdx < 2)
					menuIdx++;
				break;
			case KeyEvent.VK_ENTER:
				
				switch(menuIdx) {
				// 게임 재개
				case 0: panelChange(1,false);
					break;
					
				// 게임 저장
				case 1: doSave();
					break;
					
				// 게임 종료
				case 2: System.exit(0);
				}
				
				break;
			case KeyEvent.VK_ESCAPE:
				panelChange(1,false);
				break;
			}
			setBtnColor(pauseBtnArr,menuIdx);
	
		}
	}
	
	
	// 메뉴 선택 포커싱을 시각적으로 표현하기 위한 색 변경
	public static void setBtnColor(Component[] cArr, int focus) {
		for(int i=0;i<cArr.length;i++) {
			cArr[i].setBackground(new Color(150,150,150));
		}
		cArr[focus].setBackground(new Color(220,220,220));
		
	}
	
	
	// 패널 전환
	public static void panelChange(int direction, boolean transParent) {
		if(!transParent)
			panelArr[panelNum].setVisible(false);
		panelNum = direction;
		panelArr[panelNum].setVisible(true);
		panelArr[panelNum].requestFocus();
	}
	
	
	// 말 생성, 드로잉
	public static void initPieces(JPanel jp){
		for(int i=0;i<7;i++) {
			for(int j=0;j<7;j++) {
				pieceArr[i][j] = new AtaxxPiece(i,j);
				pieceArr[i][j].setFocusable(false);
				jp.add(pieceArr[i][j]);
			}
		}
		drawing();
	}
	
	
	// 게임 메인 로직
	public static void logic() {
		// 선택한 말의 좌표를 기억
		if(!selected) {
			if(matrix[idxY][idxX] == playerNum) {
				selectX = idxX;
				selectY = idxY;
				selected = true;
			}
		}
		else {
			// 선택한 위치를 재선택 시 선택 취소
			if(idxX == selectX && idxY == selectY)
				selected = false;
			
			else if(moveCheck()) {
				// 선택한 위치로 말 이동
				matrix[idxY][idxX] = playerNum;
				
				// 주변 감염 메소드 실행
				infect();

				// 1P 차례였다면 2P로, 2P 차례였다면 1P로 차례 전환
				if(playerNum == 1) 
					playerNum = 2;
				else 
					playerNum = 1;
				
				selected = false;
				turnCount++;
			}
			
		}

	}
	
	
	// 상하좌우 2칸 이내로 움직였는지, 그 자리에 말이 없는지 판별
	public static boolean moveCheck() {
		return 	(idxY <= selectY + 2) && (idxY >= selectY - 2) && 
				(idxX <= selectX + 2) && (idxX >= selectX - 2) && 
				 matrix[idxY][idxX] == 0 ;
	}
	
	
	// 감염 로직 실행에 필요한 인덱싱 (모서리를 고려하여 주변 1칸 범위 설정)
	public static void infectIndexing() {
		startIdxY = idxY - 1;
		endIdxY = idxY + 1;
		startIdxX = idxX - 1;
		endIdxX = idxX + 1;

		if(idxY == 0) 
		{	startIdxY = 0;
			endIdxY = 1; 	}
		
		else if(idxY == 6) 
		{	startIdxY = 5;
			endIdxY = 6; 	}
		
		if(idxX == 0) 
		{	startIdxX = 0;
			endIdxX = 1; 	}
		
		else if(idxX == 6) 
		{	startIdxX = 5;
			endIdxX = 6; 	}
	}
	
	
	// 주변 1칸을 감염시키는 로직
	public static void infect() {
		
		infectIndexing();
		
		// 이동 전 위치로부터 1칸만 움직였는지 판단할 플래그 변수
		boolean selfInfect = false; 
		for(int i = startIdxY ; i <= endIdxY ; i++) {
			for(int j = startIdxX ; j <= endIdxX ; j++) {
				if(matrix[i][j] != 0) {
					matrix[i][j] = playerNum;
					if((i == selectY) && (j == selectX))
						selfInfect = true;
				}
			}
		}
		// 만약 2칸을 움직였다면 이동 전 위치의 말은 제거
		if(!selfInfect)
			matrix[selectY][selectX] = 0;
	}
	
	
	// matrix를 쭉 읽으며 점수 집계
	public static void scoring() {
		p1Score = 0;
		p2Score = 0;
		for(int i=0;i<7;i++) {
			for(int j=0;j<7;j++) {
				if(matrix[i][j] == 1) p1Score++;
				else if(matrix[i][j] == 2) p2Score++;
			}
		}
		// 컴포넌트 갱신과 승패 판별
		p1Label.setText("1P:   " + p1Score);
		p2Label.setText("2P:   " + p2Score);
		pLabel.setText(playerNum + "P의 차례!");
		turnLabel.setText("턴 수:   " + turnCount);
		
		gameJudge();
	}
	
	
	// 승패 판별
	public static void gameJudge() {
		// 어느 한 쪽 말을 다 잃으면
		if(p1Score == 0) {
			gameResult(2);
		}
		else if(p2Score == 0) {
			gameResult(1);
		}
		// 더 이상 둘 자리가 없다면
		else {
			int empty = 0;
			// 게임판에 빈 공간이 있다면 empty 값 증가
			for(int i=0;i<7;i++) {
				for(int j=0;j<7;j++) {
					if(matrix[i][j] == 0) empty++;					
				}
			}
			// 게임판에 빈 공간이 없다면 두 점수를 비교
			if(empty == 0) {
				if(p1Score > p2Score) {
					gameResult(1);
				}
				else if(p2Score > p1Score) {
					gameResult(2);
				}
				else {
					gameResult(3);
				}
			}
		}
	}

	
	// 결과 화면으로 전환
	public static void gameResult(int winner) {
		switch(winner) {
		case 1:
			//1p승리
			winnerLabel.setText("1P의 승리!!!");
			break;
		case 2:
			//2p승리
			winnerLabel.setText("2P의 승리!!!");
			break;
		case 3:
			//무승부
			winnerLabel.setText("헉 무승부!!!");
			break;
		}
		panelChange(3,true);
		saveHistory();
	}
	
	
	// 말 이미지 그려내기
	public static void drawing() {
		for(int i=0;i<7;i++) {
			for(int j=0;j<7;j++) {
				pieceArr[i][j].statusNumber = matrix[i][j];
				pieceArr[i][j].setImage();
			}
		}
		scoring();
	}
	
	
	// 게임 저장
	public static void doSave() {
		try {
			JFileChooser fch = new JFileChooser("./");
			fch.setMultiSelectionEnabled(false);
			fch.setFileFilter(new FileNameExtensionFilter("json","json"));
			int retval = fch.showSaveDialog(panelArr[2]);
			if(retval == JFileChooser.APPROVE_OPTION) {
				GameSaveData sav = new GameSaveData(matrix, turnCount, playerNum, idxX, idxY);
				om.writeValue(fch.getSelectedFile(),sav);
				JOptionPane.showMessageDialog(panelArr[2], "게임을 성공적으로 저장했습니다.", "저장", JOptionPane.INFORMATION_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(panelArr[0], "저장 실패", "경고", JOptionPane.WARNING_MESSAGE);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(panelArr[2], "예기치 못한 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	
	// 게임 불러오기
	public static void doLoad() {
		try {
			JFileChooser fch = new JFileChooser("./");
			fch.setMultiSelectionEnabled(false);
			fch.setFileFilter(new FileNameExtensionFilter("json","json"));
			int retval = fch.showOpenDialog(panelArr[0]);
			if(retval == JFileChooser.APPROVE_OPTION) {
				JsonNode jn = om.readTree(fch.getSelectedFile());
				// 게임 판 데이터 불러오기
				for(int i=0;i<7;i++) {
					for(int j=0;j<7;j++) {
						matrix[i][j] = jn.get("saveMatrix").get(i).get(j).asInt();
					}
				}
				// 현재 턴 불러오기
				turnCount = jn.get("saveTurn").asInt();
				// 플레이어 차례 불러오기
				playerNum = jn.get("savePnum").asInt();
				// 현재 커서 불러오기
				idxX = jn.get("saveIdxX").asInt();
				idxY = jn.get("saveIdxY").asInt();
				JOptionPane.showMessageDialog(panelArr[0], "게임을 성공적으로 불러왔습니다.", "불러오기", JOptionPane.INFORMATION_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(panelArr[0], "불러오기 실패", "경고", JOptionPane.WARNING_MESSAGE);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(panelArr[0], "예기치 못한 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	
	// 게임 기록하기
	public static void saveHistory() {
		GameHistoryData dat = new GameHistoryData(turnCount, p1Score, p2Score, new Date());
		File f = new File("./gamehistory.json");
		try {
			if(f.createNewFile()) {
				histArr.add(dat);
				om.writeValue(f,histArr);
			}
			else {
				histArr = om.readValue(f, new TypeReference<ArrayList<GameHistoryData>>(){});
				histArr.add(dat);
				om.writeValue(f, histArr);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(panelArr[3], "예기치 못한 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	
	// TODO 기록 확인하기
	public static void viewHistory() {
		File f = new File("./gamehistory.json");
		try {
			if(f.exists()) {
				histArr = om.readValue(f, new TypeReference<ArrayList<GameHistoryData>>(){});
			}
			else {
				JOptionPane.showMessageDialog(panelArr[0], "게임 기록이 존재하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(panelArr[4], "예기치 못한 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
		
	public static void main(String[] args) {
		new AtaxxFrame();	
	}
}


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;




public class ArcadeClientWaitRoom extends JFrame {
	
	ArcadeClientView clientView;
	ArcadeClientGameView gameView;
	int roomId;
	String roomTitle;
	String[] roomUserList;
	int userCnt; // 총 유저 수
	boolean p1; // player1 or player2
	boolean ready;
	String name;
	
	private JPanel contentPane;
	JScrollPane scrollPane;
	
	JLabel playerName1;
	JLabel playerName2;
	
	ImageIcon readyTrue = new ImageIcon("./roomIMG/waitRoom/readied.png");
	ImageIcon readyFalse = new ImageIcon("./roomIMG/waitRoom/ready.png");
	
	JButton ready1;
	JButton ready2;

	public ArcadeClientWaitRoom(int roomId, String roomTitle, String[] UserList, ArcadeClientView clientView) { //생성자
		
		ready=false;
		
		ImageIcon bg = new ImageIcon("./roomIMG/waitRoom/waitRoomBG.png");//배경화면
		ImageIcon player1 = new ImageIcon("./roomIMG/waitRoom/player1.png");
		ImageIcon player2 = new ImageIcon("./roomIMG/waitRoom/player2.png");
		ImageIcon readyBTN = new ImageIcon("./roomIMG/waitRoom/ready.png");
		ImageIcon readyBTN2 = new ImageIcon("./roomIMG/waitRoom/ready2.png");
		
		
		ImageIcon startBTN = new ImageIcon("./connectIMG/start.png");
		ImageIcon startBTN2 = new ImageIcon("./connectIMG/start2.png");
		
		Font font = new Font("맑은 고딕", Font.BOLD, 19);//폰트만들기
		Font font2 = new Font("맑은 고딕", Font.BOLD, 17);//폰트만들기
		//생성자
		
		this.roomId=roomId;
		this.roomTitle=roomTitle;
		this.clientView = clientView;
		
		System.out.println(this.roomId);
		System.out.println(this.roomTitle);
		
		
		this.roomUserList = new String[UserList.length];
		userCnt=UserList.length;
		for(int i=0;i<userCnt;i++) {
			this.roomUserList[i]=UserList[i]; //유저 정보를 저장
			
		}
		
		System.out.println("++++++++++++++++");
		System.out.println(clientView.UserName);
		System.out.println(roomUserList[0]);
		
		if(roomUserList[0].equals(clientView.UserName)) //먼저 들어온 user 에게 1p를 주기 위해
			p1=true;
		else
			p1=false;
		
		System.out.println(p1);
		
		//JFrame-------------------------------
			String buff = roomId + "번 방";
			setTitle(buff); //프레임 타이틀 지정
			setResizable(false); //창크기 변경불가
			setLocationRelativeTo(null);//창 가운데 뜨게
			getContentPane().setLayout(null);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //창닫기 못하게 막음
			
			contentPane = new JPanel() {//배경화면 설정
				 public void paintComponent(Graphics g) {
		                g.drawImage(bg.getImage(), 0, 0, null);
		            }
			};
			scrollPane = new JScrollPane(contentPane);
			setContentPane(scrollPane);
			
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			setVisible(true);
			setBounds(600, 300, 700, 500);
			//setVisible(false);
		//-----------------------------------------	
			
			//방제목
			JLabel title = new JLabel();
			title.setHorizontalAlignment(JLabel.CENTER);//피봇을 중앙으로
			title.setFont(font);//폰트적용
			title.setForeground(Color.WHITE);//폰트색상
			title.setLocation(250,10);
			title.setSize(195, 50);
			title.setText(roomTitle);
			getContentPane().add(title);
			
			
			
			//user1 이름
			this.playerName1 = new JLabel();
			playerName1.setHorizontalAlignment(JLabel.CENTER);//피봇을 중앙으로
			playerName1.setFont(font2);//폰트적용
			playerName1.setForeground(Color.WHITE);//폰트색상
			playerName1.setLocation(32,200);
			playerName1.setSize(167, 50);
			playerName1.setText(roomUserList[0]);
			getContentPane().add(playerName1);
			
			//user2이름
			this.playerName2 = new JLabel();
			playerName2.setHorizontalAlignment(JLabel.CENTER);//피봇을 중앙으로
			playerName2.setFont(font2);//폰트적용
			playerName2.setForeground(Color.WHITE);//폰트색상
			playerName2.setLocation(487,200);
			playerName2.setSize(167, 50);
			if(roomUserList.length==2) { //2명이 다 들어와있다면
				playerName2.setText(roomUserList[1]);
			}
			else
				playerName2.setText("유저를 기다리는 중.......");
			getContentPane().add(playerName2);
			

			
			//player1 버튼
			ready1 = new JButton(readyBTN);
			ready1.setBorderPainted(false);// 버튼 테두리 설정해제
			ready1.setLocation(60,300);
			ready1.setSize(110,36);
			//ready1.setText("ready1");
			getContentPane().add(ready1);
			
			//player2 버튼
			ready2 = new JButton(readyBTN);
			ready2.setBorderPainted(false);// 버튼 테두리 설정해제
			ready2.setLocation(515,300);
			ready2.setSize(110,36);
			//ready2.setText("ready2");
			getContentPane().add(ready2);
			
			
			//user2 이미지
			JLabel playerImg2 = new JLabel();
			playerImg2.setIcon(player2);//이미지
			playerImg2.setLocation(485,80);
			playerImg2.setSize(170, 300);
			//playerImg2.setBackground(Color.magenta);
			getContentPane().add(playerImg2);
			
			
			//user1 이미지
			JLabel playerImg1 = new JLabel();
			playerImg1.setIcon(player1);//이미지
			playerImg1.setLocation(30,80);
			playerImg1.setSize(170, 300);
			//playerImg1.setBackground(Color.magenta);
			getContentPane().add(playerImg1);
			
			
			
			//시작 버튼
			JButton gameStartButton = new JButton(startBTN);
			gameStartButton.setHorizontalAlignment(JLabel.CENTER);//피봇을 중앙으로
			gameStartButton.setRolloverIcon(startBTN2);//버튼에 마우스 올라가면 이미지 변경
			gameStartButton.setBorderPainted(false);// 버튼 테두리 설정해제
			gameStartButton.setLocation(250,100);
			gameStartButton.setSize(190,55);
			//gameStartButton.setText("게임 시작");
			getContentPane().add(gameStartButton);
			

			//이벤트 처리
			ActionReady1 actionReady1 = new ActionReady1();
			ActionReady2 actionReady2 = new ActionReady2();
			GameStart gameStart = new GameStart();
			ready1.addActionListener(actionReady1);
			ready2.addActionListener(actionReady2);
			gameStartButton.addActionListener(gameStart);
			
			
	}
	public void updateName(int roomId, String roomTitle, String[] roomUserList, ArcadeClientView clientView) {
		//System.out.println("update");
		this.roomUserList = roomUserList;
		//playerName1.setText(roomUserList[0]);
		playerName2.setText(roomUserList[1]);
	}
	public void updateReady(char player, String howReady) {
		System.out.println("update ready");
		
		if(player == '1') { // player1이 누른 경우
			if(howReady.equals("true")) {
				ready1.setIcon(readyTrue);
			}
			else {
				ready1.setIcon(readyFalse);
			}
		}
		else {// player2이 누른 경우
			if(howReady.equals("true")) {
				ready2.setIcon(readyTrue);
			}
			else {
				ready2.setIcon(readyFalse);
			}
		}
	}
	
	class ActionReady1 implements ActionListener  //player1의 레디버튼
	{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(p1) { //1번플레이어인경우
				
				ready=!ready;
				
				String protocol = "61" + roomId;
				String data = (String.valueOf(ready)); //ready가 true면 "true"로, false면 "false"로
				ChatMsg msg = new ChatMsg(clientView.UserName, protocol ,data); 
				clientView.SendObject(msg);
				
			
			}
			
		}
	}
	class ActionReady2 implements ActionListener  //player1의 레디버튼
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!p1) { //2번플레이어인경우
				
				ready=!ready;
				
				String protocol = "62" + roomId;
				String data = (String.valueOf(ready)); //ready가 true면 "true"로, false면 "false"로
				ChatMsg msg = new ChatMsg(clientView.UserName, protocol ,data); 
				clientView.SendObject(msg);

			}
		}
	}
	class GameStart implements ActionListener 
	{
		@Override 
		public void actionPerformed(ActionEvent e) { //시작버튼을 누름
			
			
			String protocol = "70" + Integer.toString(roomId); //700, 701, 702, 703
			
			
			if(p1) {
				name = roomUserList[0];
			}else {
				name = roomUserList[1];
			}
				
			
			String data = (clientView.UserName + "이가" + roomId +" 번 방에서 게임시작을 하려고 합니다.");
			
			
			ChatMsg msg2 = new ChatMsg(clientView.UserName, protocol ,data); 
			clientView.SendObject(msg2);
			
		}
	}

	public ArcadeClientGameView gameStart() {
		// TODO Auto-generated method stub
		ArcadeClientGameView gameView = new ArcadeClientGameView(clientView, roomId, p1);
		return gameView;
	}
	
	




}

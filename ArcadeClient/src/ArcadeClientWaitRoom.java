import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;




public class ArcadeClientWaitRoom extends JFrame {
	
	ArcadeClientView clientView;
	ArcadeClientGameView gameView;
	String roomId;
	String roomTitle;
	String[] roomUserList;
	int userCnt; // 총 유저 수
	boolean p1; // player1 or player2
	boolean ready;
	

	public ArcadeClientWaitRoom(String roomId, String roomTitle, String[] UserList, ArcadeClientView clientView) { //생성자
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
			setVisible(true);
			setBounds(600, 300, 700, 500);
			//setVisible(false);
		//-----------------------------------------	
			
			//방제목
			JLabel title = new JLabel();
			title.setLocation(30,30);
			title.setSize(300, 50);
			title.setText(roomTitle);
			getContentPane().add(title);
			
			//user1 이미지
			JLabel playerImg1 = new JLabel();
			playerImg1.setLocation(30,100);
			playerImg1.setSize(300, 300);
			playerImg1.setBackground(Color.magenta);
			getContentPane().add(playerImg1);
			
			//user2 이미지
			JLabel playerImg2 = new JLabel();
			playerImg2.setLocation(200,100);
			playerImg2.setSize(300, 300);
			playerImg2.setBackground(Color.magenta);
			getContentPane().add(playerImg2);
			
			//user1 이름
			JLabel playerName1 = new JLabel();
			playerName1.setLocation(30,200);
			playerName1.setSize(200, 50);
			playerName1.setText(roomUserList[0]);
			getContentPane().add(playerName1);
			
			//user2이름
			JLabel playerName2 = new JLabel();
			playerName2.setLocation(200,200);
			playerName2.setSize(250, 50);
			if(roomUserList.length==2) { //2명이 다 들어와있다면
				playerName2.setText(roomUserList[1]);
			}
			else
				playerName2.setText("유저를 기다리는 중");
			getContentPane().add(playerName2);
			
			//player1 버튼
			JButton ready1 = new JButton();
			ready1.setLocation(30,300);
			ready1.setSize(200,50);
			ready1.setText("ready1");
			getContentPane().add(ready1);
			
			//player2 버튼
			JButton ready2 = new JButton();
			ready2.setLocation(400,300);
			ready2.setSize(200,50);
			ready2.setText("ready2");
			getContentPane().add(ready2);
			
			//시작 버튼
			JButton gameStartButton = new JButton();
			gameStartButton.setLocation(400,400);
			gameStartButton.setSize(200,50);
			gameStartButton.setText("게임 시작");
			getContentPane().add(gameStartButton);
			
			//나가기 버튼
			JButton gameExitButton = new JButton();
			gameExitButton.setLocation(100,400);
			gameExitButton.setSize(100,50);
			gameExitButton.setText("나가기");
			getContentPane().add(gameExitButton);
			
			
			//이벤트 처리
			ActionReady1 actionReady1 = new ActionReady1();
			ActionReady2 actionReady2 = new ActionReady2();
			GameStart gameStart = new GameStart();
			GameExit gameExit = new GameExit();
			ready1.addActionListener(actionReady1);
			ready2.addActionListener(actionReady2);
			gameStartButton.addActionListener(gameStart);
			gameExitButton.addActionListener(gameExit);
			
			
			
			
			
	}
	
	class ActionReady1 implements ActionListener  //player1의 레디버튼
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(p1) { //1번플레이어인경우
				String protocol = "61" + roomId;
				String data = ("player1이" + roomId +" 번 방에서 레디버튼을 눌렀습니다.");
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
				String protocol = "62" + roomId;
				String data = ("player2가" + roomId +" 번 방에서 레디버튼을 눌렀습니다.");
				ChatMsg msg = new ChatMsg(clientView.UserName, protocol ,data); 
				clientView.SendObject(msg);
			}
		}
	}
	class GameStart implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//테스트
			//gameView = new ArcadeClientGameView(clientView, roomId, p1);
			gameView = new ArcadeClientGameView(clientView, roomId, true);  
			//gameView = new ArcadeClientGameView(clientView, roomId, false);  
			
			//String protocol = "62" + roomId;
			
			
		}
	}
	class GameExit implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(p1) { //1번플레이어인경우
				String protocol = "81" + roomId;
				String data = ("player1이" + roomId +" 번 방에서 나갔습니다.");
				ChatMsg msg = new ChatMsg(clientView.UserName, protocol ,data); 
				clientView.SendObject(msg);
			}
			else if(!p1) { //2번플레이어인경우
				String protocol = "82" + roomId;
				String data = ("player2가" + roomId +" 번 방에서 나갔습니다.");
				ChatMsg msg = new ChatMsg(clientView.UserName, protocol ,data); 
				clientView.SendObject(msg);
			}
		}
	}

	
//	public void updatePlayer2() { //쓰려나
//		//user1 이름
//		JLabel playerName2 = new JLabel();
//		playerName2.setLocation(300,200);
//		playerName2.setSize(100, 50);
//		playerName2.setText(roomUserList[1]);
//		getContentPane().add(playerName2);
//	}
//	
//	
//	public void update() {
//		
//	}
	

}

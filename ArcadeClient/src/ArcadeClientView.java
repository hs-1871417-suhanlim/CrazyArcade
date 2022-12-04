import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;



public class ArcadeClientView extends JFrame {
	
	ArcadeClientView clientView = this;
	
	int maxRoomCnt=4;
	RoomBox roombox[] = new RoomBox[maxRoomCnt];
	
	int roomId; //client가 입장하는 방 번호
	String roomUserList[]; //해당 client가 입장한 방 유저 리스트 
	String roomTitle; //client가 입장하는 방 제목
	
	
	
	//private JPanel contentPane;
	String UserName;
	
	ArcadeClientWaitRoom waitRoom;
	
	//네트워크 관련 변수
	private static final long serialVersionUID = 1L;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private JPanel contentPane;
	JScrollPane scrollPane;

	public ListenNetwork listen; // 클래스들끼리 서로 참조
	
	
	public ArcadeClientView(String username, String ip_addr, String port_no) { //생성자
		ImageIcon bg = new ImageIcon("./roomIMG/roomBG.png");//배경화면
		ImageIcon createRoomBTN = new ImageIcon("./roomIMG/createRoomBTN.png");
		ImageIcon createRoomBTN2 = new ImageIcon("./roomIMG/createRoomBTN2.png");
		ImageIcon RoomBG = new ImageIcon("./roomIMG/roomIMG.png");//방 패널 이미지
		
		this.UserName = username;
		//System.out.println("ArcadeClientView");
		
		//JFrame-------------------------------
		setTitle("대기실"); //프레임 타이틀 지정
		//setSize(450,700);//프레임 크기
		setResizable(true); //창크기 변경불가
		setLocationRelativeTo(null);//창 가운데 뜨게
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
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
		
		
		//방 만들기 버튼------------------------
		JButton makeRoomButton = new JButton(createRoomBTN);
		makeRoomButton.setRolloverIcon(createRoomBTN2);//버튼에 마우스 올라가면 이미지 변경
		makeRoomButton.setBorderPainted(false);// 버튼 테두리 설정해제
		makeRoomButton.setBounds(145, 380, 130, 45);
		contentPane.add(makeRoomButton);
		
		
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 435, 500);//창크기, 위치 조절
		
		//JPanel---------------------------------
		
		
		Myaction action = new Myaction();
		makeRoomButton.addActionListener(action);
		
		//입장 이벤트리스너
		EnterAction0 enterAction0 = new EnterAction0(); 
		EnterAction1 enterAction1 = new EnterAction1(); 
		EnterAction2 enterAction2 = new EnterAction2(); 
		EnterAction3 enterAction3 = new EnterAction3(); 
		
		
		for(int i=0;i<maxRoomCnt;i++) {

			roombox[i] = new RoomBox(i);
			
			
			
			switch(i) {
			case 0:
				roombox[i].panel.setLocation(30,30);
		        roombox[i].enter.addActionListener(enterAction0);
		        break;
			case 1:
				roombox[i].panel.setLocation(220,30);
				roombox[i].enter.addActionListener(enterAction1);
				 break;
				
			case 2:
				roombox[i].panel.setLocation(30,200);
				roombox[i].enter.addActionListener(enterAction2);
				 break;
			case 3:
				roombox[i].panel.setLocation(220,200);
				roombox[i].enter.addActionListener(enterAction3);
				 break;
			}
			
		}
		
		UserName = username;
		
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			
			
			// ChatMsg parameter - UserName, 프로토콜, 메세지
			// 로그인
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendObject(obcm); //전송
			
			ListenNetwork net = new ListenNetwork(this);
			net.start();


		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
				
		
		
		
	} //생성자 끝---------------------------------------------------
	
	
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			//AppendText("SendObject Error");
		}
	}
	
	// Server Message를 수신
	public class ListenNetwork extends Thread {
		
		//ListenNetwork listen;
		
		ArcadeClientView clientView;
		ListenNetwork(ArcadeClientView clientView){ //생성자
			this.clientView=clientView;
			clientView.listen = this;
		}
		
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					
					
					
					switch (cm.code) {
					
					case "300": //방 정보 출력

						//공백을 기준으로 나눔 - "방제목" "RoomId" 형태
						String[] roomInfo = cm.data.split("\\+     \\+");
						int roomIds = Integer.parseInt(roomInfo[1]);
						roombox[roomIds].roomTitle.setText(roomInfo[0]);
						
						break;
					case "404":	  //방이 없음
						System.out.println("404");
						break;
					case "500", "501", "502", "503": //방 입장 허가 프로토콜
						
						roomId = Integer.parseInt(cm.code.substring(2)); //방번호 떼옴
						
						//들어오는 msg-data는 "유저1++유저2++방제목" 이런 형태
					
						String[] buff= cm.data.split("\\++");
						
						if(buff[1].equals(UserName)) { //2p 인 경우 - - - - - - - - 
							int userCnt = buff.length-1; //유저 수
							
							roomUserList=new String[userCnt];
							
							
							//roomUserList = cm.data.split(" ");
							for(int i=0;i<buff.length;i++) { //test
								
								if(i==buff.length-1) //마지막은 방제목
									roomTitle = buff[i];
								else
									roomUserList[i]=buff[i];
							}
							waitRoom = new ArcadeClientWaitRoom(roomId, roomTitle, roomUserList, clientView);
							break;
						}
						else if(buff[0].equals(UserName)) { // 1p 인경우 - - 업데이트 필요
							int roomIdBuff = Integer.parseInt(cm.code.substring(2)); //방번호 떼옴
							
							//방번호 검사할라고 햇던건데 나가기기능 빼면 사실상 필요 없을듯..
//							if(roomId != roomIdBuff)
//								break;
						
							//들어오는 msg-data는 "유저1++유저2++방제목" 이런 형태
						
//							String[] buff1= cm.data.split("\\++");
//
//							System.out.println("========");
//							System.out.println(UserName);
							
							int userCnt = buff.length-1; //유저 수
							
							roomUserList=new String[userCnt];
							
							
							//roomUserList = cm.data.split(" ");
							for(int i=0;i<buff.length;i++) { //test
								
								if(i==buff.length-1) //마지막은 방제목
									roomTitle = buff[i];
								else
									roomUserList[i]=buff[i];
							}
							waitRoom.update(roomIdBuff, roomTitle, roomUserList, clientView);
						}

//						System.out.println("========");
//						System.out.println(UserName);
						
						
					
					case "510", "511", "512", "513": //방 정보 (방하나) 업데이트 프로토콜
						
					
//						int roomIdBuff = Integer.parseInt(cm.code.substring(2)); //방번호 떼옴
//					
//						if(roomId != roomIdBuff)
//							break;
//					
//						//들어오는 msg-data는 "유저1++유저2++방제목" 이런 형태
//					
//						String[] buff1= cm.data.split("\\++");
//
//						System.out.println("========");
//						System.out.println(UserName);
//						
//						int userCnt1 = buff1.length-1; //유저 수
//						
//						roomUserList=new String[userCnt1];
//						
//						
//						//roomUserList = cm.data.split(" ");
//						for(int i=0;i<buff1.length;i++) { //test
//							
//							if(i==buff1.length-1) //마지막은 방제목
//								roomTitle = buff1[i];
//							else
//								roomUserList[i]=buff1[i];
//						}
//						waitRoom.update(roomIdBuff, roomTitle, roomUserList, clientView);
						//waitRoom = new ArcadeClientWaitRoom(roomId, roomTitle, roomUserList, clientView);
						
						break;
						case "700", "701", "702", "703": //방 정보 (방하나) 업데이트 프로토콜
							
							String buff2[] = cm.code.split("");
							int roomIdBuff2 = Integer.parseInt(buff2[2]); //방번호
							
							if(roomId == roomIdBuff2)
								waitRoom.gameStart();
							
							
							break;
						
					case "900": //키보드 누를때
						waitRoom.gameView.keyPressedEvent(cm);
						break;
					case "1000": //키보드 뗄 때
						waitRoom.gameView.keyReleasedEvent(cm);
						
						break;
					
					
					}
				} catch (IOException e) {
					System.out.println("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			MakeRoomView view = new MakeRoomView(clientView, UserName);

		}
	}

	
	
	class RoomBox{ //사용자에게 보여지는 방 목록
		
		//int roomId;
		
		ImageIcon roomBG = new ImageIcon("./roomIMG/roomIMG.png"); 
		ImageIcon connectRoomBTN = new ImageIcon("./roomIMG/connectRoomBTN.png");
		ImageIcon connectRoomBTN2 = new ImageIcon("./roomIMG/connectRoomBTN2.png");
		
		public JPanel panel = new JPanel();
		JLabel imgLabel = new JLabel();
		JLabel roomTitle = new JLabel(); //방제목
		
		JButton enter;
		
		Font font = new Font("맑은 고딕", Font.BOLD, 19);//폰트만들기
		
	    public RoomBox(int roomId) {
	    	
	    	//this.roomId = roomId;
	    	
	        panel.setSize(170,150);//방 패널 사이즈 (이미지 사이즈와 동일)
	        panel.setLayout(null);
	        
	        //방제목을 보여줌
	        roomTitle.setSize(130,45); 
	        roomTitle.setText("빈 방");//기본 빈방
	        roomTitle.setFont(font);//폰트적용
	        roomTitle.setForeground(Color.WHITE);//폰트색상
	        roomTitle.setLocation(20, 20); //제목 위치
	        roomTitle.setHorizontalAlignment(JLabel.CENTER);//가운데 정렬
	        panel.add(roomTitle);
	        
	        
	        
	        enter = new JButton(connectRoomBTN);//입장버튼
	        enter.setRolloverIcon(connectRoomBTN2);//버튼에 마우스 올라가면 이미지 변경
	        enter.setBorderPainted(false);// 버튼 테두리 설정해제
	        enter.setSize(130,45);//버튼사이즈 조절
	        enter.setLocation(21,75);//버튼 위치 조절

	        panel.add(enter);
	        
	        
	        
	        imgLabel.setIcon(roomBG);//이미지
	        imgLabel.setSize(170,150);//방 배경 이미지 사이즈
	        imgLabel.setLocation(0,0);
	        panel.add(imgLabel);
	         
	        getContentPane().add(panel);
	        //roomBox[i].setVisible(true);
	     }
	}
	
	class EnterAction0 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			JButton roomBox = (JButton) e.getSource();
			System.out.println(roomBox.getClass());
			
			String protocol = "400";
			
			String data = (UserName + "이가" + 0 +" 번 방에 입장합니다.");
			ChatMsg msg2 = new ChatMsg(clientView.UserName, protocol ,data); 
			clientView.SendObject(msg2);
		}
		
	}
	class EnterAction1 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			JButton roomBox = (JButton) e.getSource();
			
			String protocol = "401";
			
			String data = (UserName + "이가" + 1 +" 번 방에 입장합니다.");
			ChatMsg msg2 = new ChatMsg(clientView.UserName, protocol ,data); 
			clientView.SendObject(msg2);
		}
		
	}
	class EnterAction2 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			JButton roomBox = (JButton) e.getSource();
			System.out.println(roomBox.getClass());
			
			String protocol = "402";
			
			String data = (UserName + "이가" + 2 +" 번 방에 입장합니다.");
			ChatMsg msg2 = new ChatMsg(clientView.UserName, protocol ,data); 
			clientView.SendObject(msg2);
		}
		
	}
	class EnterAction3 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			JButton roomBox = (JButton) e.getSource();
			System.out.println(roomBox.getClass());
			
			String protocol = "403";
			
			String data = (UserName + "이가" + 3 +" 번 방에 입장합니다.");
			ChatMsg msg2 = new ChatMsg(clientView.UserName, protocol ,data); 
			clientView.SendObject(msg2);
		}
		
	}
}

import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class ArcadeClientView extends JFrame {
	
	ArcadeClientView clientView = this;
	
	int maxRoomCnt=4;
	RoomBox roombox[] = new RoomBox[maxRoomCnt];
	
	
	
	//private JPanel contentPane;
	private String UserName;
	
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
	
	
	public ArcadeClientView(String username, String ip_addr, String port_no) { //생성자
		
		this.UserName = username;
		System.out.println("ArcadeClientView");
		
		//JFrame-------------------------------
		setTitle("대기실"); //프레임 타이틀 지정
		//setSize(910,700);//프레임 크기
		setResizable(false); //창크기 변경불가
		setLocationRelativeTo(null);//창 가운데 뜨게
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 300, 700, 500);
		//JPanel---------------------------------
		
		for(int i=0;i<maxRoomCnt;i++) {

			roombox[i] = new RoomBox();
			
			switch(i) {
			case 0:
				roombox[i].panel.setLocation(30,30);break;
			case 1:
				roombox[i].panel.setLocation(350,30);break;
			case 2:
				roombox[i].panel.setLocation(30,200);break;
			case 3:
				roombox[i].panel.setLocation(350,200);break;
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
			
			ListenNetwork net = new ListenNetwork();
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
	class ListenNetwork extends Thread {
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
						int roomId = Integer.parseInt(roomInfo[1]);
						
						roombox[roomId].roomTitle.setText(roomInfo[0]);
						break;
					case "500", "501", "502", "503": //방 입장 허가 프로토콜
						
						
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

			
//			String username = txtUserName.getText().trim();
//			String ip_addr = txtIpAddress.getText().trim();
//			String port_no = txtPortNumber.getText().trim();
//			ArcadeClientView view = new ArcadeClientView(username, ip_addr, port_no);
			
			//ArcadeClientGameView view = new ArcadeClientGameView(username, ip_addr, port_no);
			//setVisible(false);
		}
	}
	
	class RoomBox{ //사용자에게 보여지는 방 목록
		
		JPanel panel = new JPanel();
		JLabel roomTitle = new JLabel(); //방제목
		
		public RoomBox() {
			
			panel.setSize(300,150);
			
			panel.setBackground(Color.GREEN);
			
			JButton enter = new JButton();
			panel.add(enter);
			
			//방제목을 보여줌
			
			roomTitle.setSize(100,50); 
			roomTitle.setText("빈 방");
			panel.add(roomTitle);
			
			getContentPane().add(panel);
			//roomBox[i].setVisible(true);
			
			JButton makeRoomButton = new JButton();
			makeRoomButton.setSize(100,50);
			makeRoomButton.setLocation(550,380);
			Myaction action = new Myaction();
			makeRoomButton.addActionListener(action);
			getContentPane().add(makeRoomButton);
		}
	}
	
	
	

}

//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class ArcadeServer extends JFrame {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	/**
	 * Launch the application.
	 */
	private RoomManager roomManager;

	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ArcadeServer frame = new ArcadeServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ArcadeServer() { //생성자
		
		ArcadeServer server = this;
		roomManager = new RoomManager(server); //한 번만 호출되어야 함
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		
		//---------------------------------------------------------
		
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {//UserService--------------------------------
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;

		public UserService(Socket client_socket) { //생성자
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {

				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
			String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
		}

		public void Logout() {
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O")
					user.WriteOne(str);
			}
		}

		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) { //, int protocol
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}


		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else
						continue;
					
               //client로부터 들어온 프로토콜에 따라 처리하는 구간 ---------------------------------------------
					
					if(cm.code.matches("999")) { //테스트 프로토콜
						AppendText(cm.data);
					}

					if (cm.code.matches("100")) { //로그인
						UserName = cm.UserName;
						UserStatus = "O"; // Online 상태
						Login();
					
						
						//if(roomManager.rooms.length>0) { //방이 있다면
							for(int i=0;i<4;i++) { //방정보 보내줌
								if(roomManager.roomExist[i]) {
									String data = (roomManager.rooms[i].RoomTitle + "+     +"+
											roomManager.rooms[i].roomId); //+공백다섯개+ 로 구분
									cm = new ChatMsg("Server", "300", data);
									WriteAllObject(cm);
								}
								}
						//}
					} else if (cm.code.matches("200")) { //방생성시
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server 화면에 출력
						
						int roomId = roomManager.makeRoom(cm.UserName, cm.data, client_socket);
						
						if(roomId != -1){ //방 만들기 성공
							
							
							
							for(int i=0;i<4;i++) { // 새로운 방 업데이트
								
								if(roomManager.roomExist[i]==true) {
									String data = (roomManager.rooms[i].RoomTitle + "+     +"+
											roomManager.rooms[i].roomId); //+공백다섯개+ 로 구분
									cm = new ChatMsg("Server", "300", data);
									WriteAllObject(cm);	
								}
								
										
							}
							

							
							//방을 만든 유저에겐 바로 입장 허가 프로토콜을 보냄 ---------------------------
							
							String protocol = Integer.toString(500+roomId); //500, 501, 502, 503
							
							String buff="";
							
							//방금 만든 방이라 룸 유저 수는 무조건 1이겠지만 그래도
							for(int i=0;i<roomManager.rooms[roomId].roomUsers.size();i++) {
								buff+= roomManager.rooms[roomId].roomUsers.get(i).userName;
								buff+="++"; 
							}
							
							buff+=roomManager.rooms[roomId].RoomTitle; //마지막에 방제목 붙여줌

							cm = new ChatMsg("Server", protocol, buff);
							WriteOneObject(cm);
							
						}
					}else if(cm.code.matches("40(.)")) { // 40n - 버튼으로 입장 관련
						String buff[] = cm.code.split("");
						int roomId = Integer.parseInt(buff[2]); // 넘어온 방번호
						
						if(roomManager.roomExist[roomId]==false) {
							cm = new ChatMsg("Server", "404", "방없음");
				        	WriteOneObject(cm);		
				        	AppendText(roomId+" 번 방이 없습니다.");
						}
						
//						 try {
//							 roomManager.rooms.get(roomId); //해당 번호의 방이 있는지 확인
//					            
//					        } catch (Exception e) {
//					        	cm = new ChatMsg("Server", "404", "방없음");
//					        	WriteOneObject(cm);		
//					        	AppendText(roomId+" 번 방이 없습니다.");
////					            break;
//					        }
						 
						 try {
							 if(roomManager.rooms[roomId].roomUsers.size()>=2) { 
								 cm = new ChatMsg("Server", "404", "방 꽉 찼음");
						        	WriteOneObject(cm);		
									AppendText(roomId+" 번 방이 꽉 찼습니다.");
								}
							 else{//방입장 허가 
								 
								 //방입장 시도한 client에게 보냄--------------------------------------
								 
									roomManager.rooms[roomId].addUser(UserName,client_socket); //유저 추가
									
									String protocol = Integer.toString(500+roomId); //500, 501, 502, 503
									
									String buff1="";


									for(int i=0;i<roomManager.rooms[roomId].roomUsers.size();i++) {
										buff1+= roomManager.rooms[roomId].roomUsers.get(i).userName;
										buff1+="++"; 
									}
									
									buff1+=roomManager.rooms[roomId].RoomTitle; //마지막에 방제목 붙여줌
									
									
									cm = new ChatMsg("Server", protocol, buff1);
									WriteOneObject(cm);
									
									//----------------------------------------
									
									//기존 1p에게 방정보 업데이트
									//새로운 user가 들어왔으니 기존 user에게 정보 보내줌
									protocol = Integer.toString(510+roomId); //510, 511, 512, 513
									cm = new ChatMsg("Server", protocol, buff1);
									WriteAllObject(cm);
									
									
								}
					            
					        } catch (Exception e) {
					        	
					        }
						
						
						
					}
					
					else if(cm.code.matches("6(.*)")) { //정규표현식 6nn - 레디 관련 
						
						// 621  <- player2가 1번방에서 레디버튼을 누름
						
						String buff[] = cm.code.split("");
						int roomId = Integer.parseInt(buff[2]);
						int userId = Integer.parseInt(buff[1])-1;
						
						//레디상태 전환
						roomManager.rooms[roomId].roomUsers.get(userId).ready
						=!roomManager.rooms[roomId].roomUsers.get(userId).ready;
						
						String data = (String.valueOf(roomManager.rooms[roomId].roomUsers.get(userId).ready));
						
						cm = new ChatMsg("Server", cm.code, data);
						WriteAllObject(cm);
						
						
						}
					
					else if(cm.code.matches("70(.)")) { //시작 관련
						String buff[] = cm.code.split("");
						int roomId = Integer.parseInt(buff[2]); // 넘어온 방번호
						
						//레디여부를 따짐 - 둘다 레디상태라면 프로토콜 그대로 보내줌
						if(roomManager.rooms[roomId].roomUsers.get(0).ready &&
								roomManager.rooms[roomId].roomUsers.get(1).ready) {
							String protocol = "70" + Integer.toString(roomId);
							cm = new ChatMsg("Server", protocol, "게임시작");
							WriteAllObject(cm);
							
						}
					}
					else if(cm.code.matches("80(.)")) { //keyPressed

						AppendText(cm.UserName +"가" + cm.data);
						WriteAllObject(cm); //그대로 보내줌
						
					}
					else if(cm.code.matches("90(.)")) { //keyReleased

						AppendText(cm.UserName +"가" + cm.data);
						WriteAllObject(cm); //그대로 보내줌
						
					}
					else if(cm.code.matches("100(.)")) { //keyReleased

						AppendText(cm.UserName +"가" + cm.data);
						WriteAllObject(cm); //그대로 보내줌
						
					}
					else if(cm.code.matches("120(.)")) { // 게임 종료
						
						
						
						String buff[] = cm.code.split("");
						int roomId = Integer.parseInt(buff[3]); // 넘어온 방번호
						
						if((roomManager.roomExist[roomId])==false)
							break;
							
						System.out.println("게임종료");
						System.out.println(roomId);
						
						for(int i=0;i<4;i++) {
							System.out.println(roomManager.roomExist[i]);
						}
						
						roomManager.rooms[roomId]=null; //방 없애버림
						roomManager.roomExist[roomId]=false;
						
						WriteAllObject(cm);	//그대로 다시 보내줌
						
						for(int i=0;i<4;i++) { // 새로운 방 업데이트
							System.out.println(i);
							try {
								if(roomManager.roomExist[i]==true) {
									AppendText("방있음"+i);
									String data = (roomManager.rooms[roomId].RoomTitle + "+     +"+
											roomManager.rooms[roomId].roomId); //+공백다섯개+ 로 구분
									cm = new ChatMsg("Server", "300", data);
									WriteAllObject(cm);	
								}
								else {
									AppendText("방없음"+i);
									String data = ("빈 방"+ "+     +"+
											i);
									cm = new ChatMsg("Server", "300", data);
									WriteAllObject(cm);	
								}
							}catch (Exception e) {
							}
							
							
							
									
						}
						
						
					}
					

//					else { // 300, 500, ... 기타 object는 모두 방송한다.
//						WriteAllObject(cm);
//					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
		
		
	}//UserService--------------------------------
	

}

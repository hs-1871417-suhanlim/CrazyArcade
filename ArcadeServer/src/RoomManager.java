import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class RoomManager { 
	
	private ArcadeServer server; 
	
	//int roomNum; //방 갯수
	ArrayList<Room> rooms = new ArrayList();
	boolean roomExist[] = new boolean[4];
	int roomId=0; 
	int roomMax;
	
	
	public RoomManager(ArcadeServer server) { //생성자
		//this.roomNum=0;
		this.server=server;
		this.roomMax = 4;
	}
	
	public boolean makeRoom(String userName, String RoomTitle, Socket client_socket) {
		if(rooms.size()>=roomMax) //방은 네개까지 
			return false;
		
		for(int i=0;i<roomMax;i++) { //방번호 부여
			if(roomExist[i]==false) {
				roomId=i;
				roomExist[i]=true;
				break;
			}
				
		}
		Room room = new Room(userName, RoomTitle, roomId , client_socket);
		
		rooms.add(room); // room ArrayList에 생성한 방 추가
		
		return true;
	}
	

	
	
	
	public class Room{ // 방 클래스
		
		String RoomTitle;
		int roomId;
		int maxUser=2;
		
		ArrayList<RoomUser> roomUsers = new ArrayList();
		Vector socketUser = new Vector(); // 연결된 사용자를 저장할 벡터
		
		public Room(String userName, String RoomTitle, int RoomId, Socket client_socket) { //방 생성
			this.RoomTitle = RoomTitle;
			this.roomId = RoomId;
			//Socket newUser = client_socket;
			socketUser.add(client_socket);
			
			RoomUser roomUser = new RoomUser(userName, roomId);
			roomUsers.add(roomUser);
		}
		

		
		public void userExit(int userId) { //유저가 나감
			//player1이면 1이, player2면 2가 들어옴
			//arrayList와 벡터에 순서대로 들어가있으므로 -1해줌
			roomUsers.remove(userId-1);
			socketUser.remove(userId-1);
		}
		

		public void roomUpdate(ArcadeServer.UserService userService) { //룸정보를 업데이트
			// 버튼을 통해 입장하거나 누군가가 나갈때 이용
			String protocol = Integer.toString(300+roomId); //300, 301, 302, 303
			
			String buff="";
			
			////유저1++(유저2)++방이름 이렇게 날아감
			
			for(int i=0;i<roomUsers.size();i++) {
				buff+= roomUsers.get(i).userName;
				buff+="++"; 
			}
			
			buff+=RoomTitle; //마지막에 방제목 붙여줌
			
			ChatMsg cm = new ChatMsg("Server", protocol, buff);
			userService.WriteOneObject(cm);
			
		}

		public void addUser(String userName, Socket client_socket) { //user가 버튼으로 들어옴
			socketUser.add(client_socket);
			
			RoomUser roomUser = new RoomUser(userName, roomId);
			roomUsers.add(roomUser);
		}



//		public void addUser(String userName, Socket client_socket) {
//			// TODO Auto-generated method stub
//			
//		}

		
	}



}

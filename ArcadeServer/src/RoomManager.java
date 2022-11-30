import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class RoomManager { 
	
	private ArcadeServer server; 
	
	//int roomNum; //방 갯수
	ArrayList<Room> rooms = new ArrayList();
	int roomMax;
	
	
	public RoomManager(ArcadeServer server) { //생성자
		//this.roomNum=0;
		this.server=server;
		this.roomMax = 4;
	}
	
	public boolean makeRoom(String userName, String RoomTitle, Socket client_socket) {
		if(rooms.size()>=roomMax) //방은 네개까지 
			return false;
		
		//방 생성시마다 ArrayList에 들어가있는 방 개수를 통해 
		//roomId지정해줌 - 0번, 1번, 2번, 3번방
		Room room = new Room(userName, RoomTitle, rooms.size(), client_socket);
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
			Socket newUser = client_socket;
			socketUser.add(newUser);
			
			RoomUser roomUser = new RoomUser(userName, roomId);
			roomUsers.add(roomUser);
		}
		
	}



}

import java.util.ArrayList;

public class RoomManager { 
	
	//int roomNum; //방 갯수
	ArrayList<Room> rooms = new ArrayList();
	int roomMax;
	
	
	
	public RoomManager() { //생성자
		//this.roomNum=0;
		this.roomMax = 4;
	}
	
	public boolean makeRoom(String userName, String RoomTitle) {
		if(rooms.size()>=roomMax) //방은 네개까지 
			return false;
		
		//방 생성시마다 ArrayList에 들어가있는 방 개수를 통해 
		//roomId지정해줌 - 0번, 1번, 2번, 3번방
		Room room = new Room(userName, RoomTitle, rooms.size());
		rooms.add(room); // room ArrayList에 생성한 방 추가
		
		return true;
	}
	
	
	
	public class Room{ // 방 클래스
		
		String RoomTitle;
		int roomId;
		int maxUser=2;
		
		ArrayList<RoomUser> roomUsers = new ArrayList();
		
		
		public Room(String userName, String RoomTitle, int RoomId) { //방 생성
			this.RoomTitle = RoomTitle;
			this.roomId = RoomId;
			RoomUser roomUser = new RoomUser(userName, roomId);
			roomUsers.add(roomUser);
		}
		
	}

}

import java.awt.Point;

public class Water {
	
	int cnt;
	int from; //누구한테서 왔는지 - 나 : 1 / 상대방: -1?
	
	Point dis;
	

	
	public Water(int x, int y, int waterLength, int from) {
		cnt=0;
		this.from=from;
		
		//x,y는 물풍선이 터진곳의 왼쪽상단 좌표
		dis=new Point(x,y);
//		System.out.println("water");
//		System.out.println(dis);
	}

}

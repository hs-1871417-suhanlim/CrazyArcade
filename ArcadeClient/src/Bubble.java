import java.awt.Point;

public class Bubble {

		
	Point dis;//물풍선의 표시 좌표. 실제 좌표보다 *100 상태이다.
	Point pos;//물풍선의 계산 좌표. 실제 좌표보다 *100 상태이다.
	
	//int img_num;//물풍선의 이미지 번호
	int from;//물풍선을 누가 발사했는가 - 일단 나 자신은 1
	
	int status; //1이면 그냥 놓여져 있는 상태 터지면 0 <<필요없을듯..?
	int cnt; //n초 흐르면 터지게 하기 위해

	
	public Bubble(int myx, int myy, int from){
		
		pos=new Point(myx,myy);
		cnt = 0;
		
		myx/=100;
		myy/=100;
		//버블 위치 조정
		myx = myx - (myx%52);
		myy = myy - (myy%52)+16;
		if(myx<52)
			myx=52;
		
		dis=new Point(myx,myy);
//		System.out.println("bubble");
//		System.out.println(dis);
	
//		
//		
//		
////		dis=new Point(myx/100,myy/100);
//		dis=new Point(myx,myy);
//		
//		
//		//System.out.println(a);
//		System.out.println(pos);
//		//System.out.println(pos);
//		
//		
//		

		this.from=from;
		
		this.status=1;
		//this.cnt=0;
	}

	
}

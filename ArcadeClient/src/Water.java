import java.awt.Point;
import java.util.Arrays;

public class Water {
	
	int cnt;
	int from; //누구한테서 왔는지 - 나 : 1 / 상대방: -1?
	
	Point dis;
	
	boolean crash[] = new boolean[20];
	
	
	int waterLength; //물줄기 길이

	
	public Water(int x, int y, int waterLength) {
		cnt=0;
		
		//x,y는 물풍선이 터진곳의 왼쪽상단 좌표
		dis=new Point(x,y);
		this.waterLength = waterLength;
		
		/*      
		 * 
		 *        		 16
		 *         		 12
		 *    	   		 8 
		 *         		 4
		 *        	     0
		 * 18 14 10 6 2 (X) 3 7 11 15 19   
		 *               1   
		 *               5   
		 *               9   
		 *        		 13
		 *         		 17
		 *         
		 *         
		 * */
		Arrays.fill(crash, false);
		
//		System.out.println("water");
//		System.out.println(dis);
	}

}








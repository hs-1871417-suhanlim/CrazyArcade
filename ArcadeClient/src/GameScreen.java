import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

public class GameScreen extends Canvas {
	//실제 게임의 메인 제어를 행하는 클래스
	//가급적 화면 출력에서는 게임에서 공통으로 쓰이는 변수값의 변경 등을 행하지 않는 것이 좋다.
	
	ArcadeClientGameView  main;
	int cnt;
	int gamecnt; //게임 흐름
	
	
	//화면 그리기용 변수
	int x,y;//플레이어 캐릭터의 좌표
	
	Image dblbuff;//더블버퍼링용 백버퍼
	Graphics gc;//더블버퍼링용 그래픽 컨텍스트
	
	Image bg; //배경화면
	Image block; //블럭
	//Image house[]=new Image[5]; //장애물
	Image bubble[] = new Image[4]; //물풍선
	Image pop[] = new Image[10]; //터진 물풍선(일단 나중에)
	//item도 할 수 있다면..?
	Image chr;
	Image chrDown[] = new Image[8];
	Image chrUp[] = new Image[8];
	Image chrLeft[] = new Image[6];
	Image chrRight[] = new Image[6];
	Image chrTrap[] = new Image [13];
	
	Font font;
	

    GameScreen(ArcadeClientGameView main) {
        this.main = main;
        this.font = new Font("Default", 0, 9);
    }
    
    public void paint(Graphics g) {
        if (this.gc == null) {
        	//더블 버퍼링용 오프스크린 버퍼 생성. 필히 paint 함수 내에서 해 줘야 한다. 그렇지 않으면 null이 반환된다.
            this.dblbuff = this.createImage(this.main.gScreenWidth, this.main.gScreenHeight);
            
            if (this.dblbuff == null) {
                System.out.println("오프스크린 버퍼 생성 실패");
            } else {
                this.gc = this.dblbuff.getGraphics();//오프스크린 버퍼에 그리기 위한 그래픽 컨텍스트 획득
            }

        } else {
            this.update(g);
        }
    }
    
    
    public void update(Graphics g) {//화면 깜박거림을 줄이기 위해, 
    								//paint에서 화면을 바로 묘화하지 않고 update 메소드를 호출하게 한다.
        this.cnt = this.main.cnt;
        this.gamecnt = this.main.gamecnt;
        if (this.gc != null) {
            this.dblpaint();//오프스크린 버퍼에 그리기
            g.drawImage(this.dblbuff, 0, 0, this);
        }
    }
    
	public void dblpaint(){
		//실제 그리는 동작은 이 함수에서 모두 행한다.
		switch(main.status){
		case 0:
//			Draw_TITLE();
//			gc.setColor(new Color(0));
//			gc.drawString("Education ver.", 10,40);
			break;
		case 2: //playing상태
			Draw_BG(); //배경화면 그리기
			Draw_MY(); //캐릭터 그리기
			Draw_BUBBLE(); //물풍선 그리기
			
			//Draw_ITEM(); //아이템 그리기
			//Draw_BG2();
			//Draw_Block(); //블럭 그리기 
			//Draw_House(); //집 그리기
			
			
			
//			Draw_BG();
//			Draw_MY();
//			Draw_BG2();
//			drawImageAnc(_start, 0,270, 3);
			break;
		case 4://게임오버
			
			break;
		case 3:
			
		default:
			break;
		}
	}
	public void Draw_BG(){ //배경화면 그리기
		gc.drawImage(bg,0,0,this);
	}
	public void Draw_MY(){ //플레이어 그리기
		
		//mymode : 플레이어 상태 
		// 2 : playing <<로 시작
		
		//myImg : 플레이어 이미지
		//0 wait 1-상 2-하 3-좌 4-우
		
		int myx,myy;
		myx=main.myx/100;
		myy=main.myy/100;
		switch(main.mymode){
		case 0://무적
		case 1://무적이면서 등장
			//if(main.cnt%20<10) drawImageAnc(chr[2+(main.cnt/5)%2], myx, myy, 4);
			break;
		case 2://온플레이
			if(main.myimg==0) drawImageAnc(chr, myx, myy, 4);
			else if(main.myimg==1) drawImageAnc(chrUp[cnt/4%8], myx, myy, 4);
			else if(main.myimg==2) drawImageAnc(chrDown[cnt/4%8], myx, myy, 4);
			else if(main.myimg==3) drawImageAnc(chrLeft[cnt/4%6], myx, myy, 4);
			else if(main.myimg==4) drawImageAnc(chrRight[cnt/4%6], myx, myy, 4);
			break;
//		case 3://데미지
//			if(main.cnt%6<3) drawImageAnc(chr[8], myx, myy, 4);
//			break;
		}
	}
	public void Draw_BUBBLE() {
//		 if(this.main.bubble.size()>=1)
//		 System.out.println((Bubble)this.main.bubble.elementAt(0));
			int myx,myy;
			myx=main.myx/100;
			myy=main.myy/100;

		for(int i = 0; i < this.main.bubble.size(); ++i) {
			
		   Bubble buff = (Bubble)this.main.bubble.elementAt(i);
		   //System.out.println((Bubble)this.main.bubble.elementAt(i));
		   drawImageAnc(bubble[main.gamecnt/50%4], buff.dis.x, buff.dis.y, 4);
		       
	    }
	}
	
	public void drawImageAnc(Image img, int x, int y, int anc){
		//앵커값을 참조해 이미지 출력 위치를 보정한다.
		//예) anc==0 : 좌상단이 기준, anc==4 : 이미지 중앙이 기준
		int imgw, imgh;
		imgw=img.getWidth(this);
		imgh=img.getHeight(this);
		x=x-(anc%3)*(imgw/2);
		y=y-(anc/3)*(imgh/2);
		
		gc.drawImage(img, x,y, this);
	}
	public void drawImageAnc(Image img, int x, int y, int sx,int sy, int wd,int ht, int anc){
		//sx,sy부터 wd,ht만큼 클리핑해서 그린다.
		if(x<0) {
			wd+=x;
			sx-=x;
			x=0;
		}
		if(y<0) {
			ht+=y;
			sy-=y;
			y=0;
		}
		if(wd<0||ht<0) return;
		x=x-(anc%3)*(wd/2);
		y=y-(anc/3)*(ht/2);
		gc.setClip(x, y, wd, ht);
		gc.drawImage(img, x-sx, y-sy, this);
		gc.setClip(0,0, main.gScreenWidth+10,main.gScreenHeight+30);
	}

}

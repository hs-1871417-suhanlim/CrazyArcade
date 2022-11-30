import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MakeRoomView extends JFrame {
	
	private ArcadeClientView clientView; 
	private JTextField txtRoomName;
	private String UserName;
	private JPanel contentPane;
	JScrollPane scrollPane;
	
	public MakeRoomView(ArcadeClientView clientView, String UserName) {
		ImageIcon bg = new ImageIcon("./roomIMG/createRoomBG.png");//배경화면
		ImageIcon createRoomBTN = new ImageIcon("./roomIMG/createRoomBTN.png");
		ImageIcon createRoomBTN2 = new ImageIcon("./roomIMG/createRoomBTN2.png");
		
		this.clientView=clientView;
		this.UserName = UserName;
		
		//JFrame-------------------------------
			setTitle("방 생성하기"); //프레임 타이틀 지정
			setResizable(false); //창크기 변경불가
			setLocationRelativeTo(null);//창 가운데 뜨게
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
			
			setVisible(true);
			setBounds(900, 100, 345, 500);//창위치, 창크기
		
			//-------------------------------
			
			
			//방 이름 설정 필드-------------------------------
			txtRoomName = new JTextField();
			txtRoomName.setText("빨리 들어와~");//입력예제 (자동완성)
			txtRoomName.setBounds(69, 130, 205, 33);
			getContentPane().add(txtRoomName);
			
			//-------------------------------
			
			
			//방 생성 버튼-------------------------------
			JButton makeRoomButton = new JButton(createRoomBTN);
			makeRoomButton.setRolloverIcon(createRoomBTN2);//버튼에 마우스 올라가면 이미지 변경
			makeRoomButton.setBorderPainted(false);// 버튼 테두리 설정해제
			makeRoomButton.setSize(130,45);
			makeRoomButton.setLocation(100,329);
			Myaction action = new Myaction();
			makeRoomButton.addActionListener(action);
			getContentPane().add(makeRoomButton);
			
			//-------------------------------
			
	}
	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String RoomTitle = txtRoomName.getText().trim(); //Label에게서 값 알아내기
			
			
			ChatMsg msg = new ChatMsg(UserName, "200", RoomTitle); //방 생성
			clientView.SendObject(msg);
			
			
			setVisible(false);
			
		}
	}
}

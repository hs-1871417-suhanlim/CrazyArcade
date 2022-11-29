import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MakeRoomView extends JFrame {
	
	private ArcadeClientView clientView; 
	private JTextField txtRoomName;
	private String UserName;
	

	public MakeRoomView(ArcadeClientView clientView, String UserName) {
		
		this.clientView=clientView;
		this.UserName = UserName;
		
		//JFrame-------------------------------
			setTitle("방 생성"); //프레임 타이틀 지정
			setResizable(false); //창크기 변경불가
			setLocationRelativeTo(null);//창 가운데 뜨게
			getContentPane().setLayout(null);
			setVisible(true);
			setBounds(600, 300, 700, 500);
			
			txtRoomName = new JTextField();
			txtRoomName.setBounds(220, 264, 82, 33);
			getContentPane().add(txtRoomName);
			
			JButton makeRoomButton = new JButton();
			makeRoomButton.setSize(100,50);
			makeRoomButton.setLocation(550,380);
			Myaction action = new Myaction();
			makeRoomButton.addActionListener(action);
			getContentPane().add(makeRoomButton);
			
			
	}
	
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String RoomTitle = txtRoomName.getText().trim();
			System.out.println(RoomTitle);
			
			ChatMsg msg = new ChatMsg(UserName, "200", RoomTitle); //방 생성
			clientView.SendObject(msg);
			
			
			setVisible(false);
			
		}
	}
}

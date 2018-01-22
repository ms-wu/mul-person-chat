import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


public class ServerLoginFrame {
	private JFrame jf_login;
	private JButton login;
	
	ServerLoginFrame(){
		init_JFrame();
	}
	public void init_JFrame() {
		// set the frame
		jf_login = new JFrame("WuChat服务器");
		jf_login.setBackground(Color.GRAY);
		jf_login.pack();
		jf_login.setBounds(100, 100, 700, 500);
		jf_login.setResizable(true);
		jf_login.setLocation(600, 250);
		
		ImageIcon icon = new ImageIcon("IMG_20170125_170050.jpg");
		jf_login.setIconImage(icon.getImage());
		jf_login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		login = new JButton("启动服务器"); 
		login.setBounds(jf_login.getWidth()/2-60, jf_login.getHeight()/2-50, 100, 40);
		jf_login.setLayout(null);
		jf_login.add(login);

		jf_login.setVisible(true);
		
		//push the button and execute this action
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				jf_login.setVisible(false);
				new Server();
				
			}
		});
	}
	
}

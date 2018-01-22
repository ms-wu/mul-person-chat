import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ClientLoginFrame {
	private JFrame jf_login;
	private JButton login;
	private JTextField name;
	private JTextField password;
	private JLabel nl;
	private JLabel pl;
	
	ClientLoginFrame(){
		init_JFrame();
	}
	public void init_JFrame() {
		// set the frame
		jf_login = new JFrame("WuChat客户端");
		jf_login.setBackground(Color.GRAY);
		jf_login.pack();
		jf_login.setBounds(100, 100, 700, 500);
		jf_login.setResizable(true);
		jf_login.setLocation(600, 250);
		
		ImageIcon icon = new ImageIcon("IMG_20170125_170050.jpg");
		jf_login.setIconImage(icon.getImage());
		jf_login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		name = new JTextField();
		name.setFont(new Font("黑体", Font.PLAIN, 16));
		name.setBounds(jf_login.getWidth()/2-60, jf_login.getHeight()/2-150, 200, 40);
		
		password = new JTextField();
		password.setFont(new Font("黑体", Font.PLAIN, 16));
		password.setBounds(jf_login.getWidth()/2-60, jf_login.getHeight()/2-70, 200, 40);
		
		nl = new JLabel("用户名");
		nl.setFont(new Font("黑体", Font.PLAIN, 16));
		nl.setBounds(jf_login.getWidth()/2-160, jf_login.getHeight()/2-150, 100, 40);
		
		pl = new JLabel("密码");
		pl.setFont(new Font("黑体", Font.PLAIN, 16));
		pl.setBounds(jf_login.getWidth()/2-160, jf_login.getHeight()/2-70, 100, 40);
		
		login = new JButton("登录"); 
		login.setFont(new Font("黑体", Font.PLAIN, 16));
		login.setBounds(jf_login.getWidth()/2-100, jf_login.getHeight()/2+30, 160, 40);
		jf_login.setLayout(null);
		jf_login.add(login);
		jf_login.add(name);
		jf_login.add(password);
		jf_login.add(nl);
		jf_login.add(pl);

		jf_login.setVisible(true);
		
		//push the button and execute this action
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String n = name.getText();
				String p = name.getText();
				if (n.equals("") || p.equals("")) {
					JOptionPane.showMessageDialog(jf_login, "用户名或密码不能为空", "警告", JOptionPane.WARNING_MESSAGE);
				}
				else {
					jf_login.setVisible(false);
					new Client(n, p);
				}
				
			}
		});
	}
}

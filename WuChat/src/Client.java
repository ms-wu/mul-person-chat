import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client {
	private JFrame jfClient;
	private JButton send;
	private JButton sendAll;
	private JButton off;
	private JTextArea field;
	private JPanel east;
	private JTextArea center;
	private JPanel south;
	private ClientThread client;
	private ArrayList<String> list;
	
	public Client(String name, String id) {
		initFrame();
		start(name, id);
	}
	
	public void initFrame() {
		list = new ArrayList<String>();
		JTextField now = new JTextField("          当前连接           ");
		now.setHorizontalAlignment(SwingConstants.CENTER);
		now.setEditable(false);
		now.setBackground(Color.WHITE);
		
		jfClient = new JFrame("客户端程序");
		
		field = new JTextArea(3, 20);
		field.setFont(new Font("宋体", Font.PLAIN, 16));
		east = new JPanel();
		center = new JTextArea();
		south = new JPanel();
		send = new JButton("发送");
		sendAll = new JButton("群发");
		off = new JButton("注销");
		
		center.setFont(new Font("宋体", Font.PLAIN, 16));
		
		east.setLayout(new GridLayout(11, 1));
		east.add(now);
		
		
		center.setEditable(false);
		
		south.setLayout(new GridLayout(2, 2, 10, 10));
		south.add(field);
		south.add(send);
		south.add(off);
		south.add(sendAll);
		
		jfClient.setLayout(new BorderLayout());
		jfClient.add(south, BorderLayout.SOUTH);
		jfClient.add(east, BorderLayout.EAST);
		jfClient.add(center, BorderLayout.CENTER);
		jfClient.pack();
		
		jfClient.setSize(1000, 600);
		jfClient.setBackground(Color.WHITE);
		jfClient.setResizable(true);
		jfClient.setLocation(460, 210);
		
		//设置界面图标
		ImageIcon icon = new ImageIcon("IMG_20170125_170050.jpg");
		jfClient.setIconImage(icon.getImage());
		jfClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jfClient.setVisible(true);
		
		//发送按钮设置
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					PrintWriter write = client.getWrite();
					UserInfo user = client.getUser();
					String content = field.getText();
					StringTokenizer st = new StringTokenizer(content, "@");
					String aim = st.nextToken();
					boolean flag = false;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).equals(aim)) {
							flag = true;
						}
					}
					if (flag == false) {
						JOptionPane.showMessageDialog(jfClient, "没有此用户！！", "警告", JOptionPane.WARNING_MESSAGE);
					}
					else {
						write.println(user.getName() + "@" + content);
						write.flush();
						field.setText("");
					}
				}catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		//群发按钮设置
		sendAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					PrintWriter write = client.getWrite();
					UserInfo user = client.getUser();
					String content = field.getText();
					write.println(user.getName() + "@" + "ALL" + "@" + content);
					write.flush();
					field.setText("");
				}catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		//注销按钮
		off.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					PrintWriter write = client.getWrite();
					BufferedReader read = client.getRead();
					write.println("CLOSE");
					write.flush();
					write.close();
					read.close();
					client.stop();
					JOptionPane.showMessageDialog(jfClient, "感谢您的使用", "拜拜", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}catch(IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		//关闭按钮
		jfClient.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				try{
					PrintWriter write = client.getWrite();
					BufferedReader read = client.getRead();
					write.println("CLOSE");
					write.flush();
					write.close();
					read.close();
					client.stop();
					JOptionPane.showMessageDialog(jfClient, "感谢您的使用", "拜拜", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}catch(IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public void start(String name, String id) {
		client = new ClientThread(name);
		client.start(); // 客户端线程启动
	}

	class ClientThread extends Thread{
		private Socket socket;
		private BufferedReader read;
		private PrintWriter write;
		private UserInfo user;
		
		public BufferedReader getRead() {
			return read;
		}

		public PrintWriter getWrite() {
			return write;
		}

		public UserInfo getUser() {
			return user;
		}

		ClientThread(String name){
			try{
				int port = 1234;
				String hostId = "127.0.0.1";
				socket = new Socket(hostId, port);
				user = new UserInfo(name, socket.getLocalAddress().toString());
				write = new PrintWriter(socket.getOutputStream());
				read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				// 连接成功时发送用户名/id
				write.println(name + "/" + socket.getLocalAddress().toString());
				write.flush();
			}catch(UnknownHostException e1) {
				center.append("主机IP地址未知错误\t\n");
				e1.printStackTrace();
			}catch(IOException e1) {
				center.append(e1.getMessage() + "\t\n");
				e1.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			String message = "";
			while (true) {
				try {
					message = read.readLine();
					StringTokenizer st = new StringTokenizer(message, "@");
					String commend = st.nextToken();
					if (commend.equals("ADD")) {
						//刷新用户列表
						String name = st.nextToken();
						list.add(name);
						JButton now = new JButton(name);
						east.add(now);
						now.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								//发送给单个人的话按按钮会自动在前面加上接收方和@
								String message = field.getText();
								message = name + "@" + message;
								field.setText(message);
							}
							
						});
						center.append(name + "上线\t\n");
					}
					else if (commend.equals("DELETE")) {
						String name = st.nextToken();
						
						//删除用户列表中该用户和存储块中的该用户
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).equals(name)) {
								east.remove(i + 1);
								east.repaint();
								east.validate();
								jfClient.repaint();
								jfClient.validate();
								list.remove(i);
							}
						}
						center.append(name + "下线\t\n");
					}
					else if (commend.equals("LIST")) {
						//首先获取需要加载用户列表的大小
						int size = Integer.parseInt(st.nextToken());
						for (int i = 0; i < size; i++) {
							String name = st.nextToken();
							list.add(name);
							JButton now = new JButton(name);
							east.add(now);
							now.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {
									//发送给单个人的话按按钮会自动在前面加上接收方和@
									String message = field.getText();
									message = name + "@" + message;
									field.setText(message);
								}
								
							});
						}
					}
					else if (commend.equals("MAX")) {
						JOptionPane.showMessageDialog(jfClient, "连接人数到达上限", "警告", JOptionPane.WARNING_MESSAGE);
						System.exit(0);
					}
					else if (commend.equals("CLOSE")) {
						center.append("服务器已关闭，请注销本客户端\t\n");
						field.setEditable(false);
						write.close();
						read.close();
						this.stop();
					}
					else {
						center.append(message + "\t\n");
					}
					
				}catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
}

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

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


//消息格式:(特殊语句@)源用户名称(/源用户ID)@特殊语句或目的用户名称（/目的用户ID）@内容
//关键词以@隔开，用户名称和ID以/隔开
public class Server {
	private JFrame jfServer;
	private JButton send;
	private JTextArea field;
	private JPanel east;
	private JTextArea center;
	private JPanel south;
	private ArrayList<ServerThread> clients; // 用户连接线程存储结构
	
	public Server() {
		initFrame();
		start();
	}
	
	//初始化界面函数
	public void initFrame() {
		JTextField now = new JTextField("          当前连接           ");
		now.setHorizontalAlignment(SwingConstants.CENTER);
		now.setEditable(false);
		now.setBackground(Color.WHITE);
		
		jfServer = new JFrame("服务器程序");
		
		field = new JTextArea(3, 20);
		field.setFont(new Font("宋体", Font.PLAIN, 16));
		east = new JPanel();
		center = new JTextArea();
		south = new JPanel();
		send = new JButton("发送");
		
		center.setFont(new Font("宋体", Font.PLAIN, 16));
		
		east.setLayout(new GridLayout(11, 1));
		east.add(now);
		
		
		center.setEditable(false);
		
		south.setLayout(new GridLayout(1, 2, 10, 10));
		south.add(field);
		south.add(send);
		
		jfServer.setLayout(new BorderLayout());
		jfServer.add(south, BorderLayout.SOUTH);
		jfServer.add(east, BorderLayout.EAST);
		jfServer.add(center, BorderLayout.CENTER);
		jfServer.pack();
		
		jfServer.setSize(1000, 600);
		jfServer.setBackground(Color.WHITE);
		jfServer.setResizable(true);
		jfServer.setLocation(460, 210);
		
		//设置界面图标
		ImageIcon icon = new ImageIcon("IMG_20170125_170050.jpg");
		jfServer.setIconImage(icon.getImage());
		jfServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jfServer.setVisible(true);
		
		//服务器群发按钮
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					String message = field.getText();
					for (int i = 0; i < clients.size();i++) {
						clients.get(i).getWrite().println(message + "(服务器群发)");
						clients.get(i).getWrite().flush();
					}
				}catch(Exception e1) {
					center.append(e1.getMessage() + "\t\n");
				}
			}
		});
		
		jfServer.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				for (int i = 0; i < clients.size(); i++) {
					try{
						PrintWriter write = clients.get(i).getWrite();
						BufferedReader read = clients.get(i).getRead();
						write.println("CLOSE");
						write.flush();
						write.close();
						read.close();
						ServerThread temp = clients.get(i);
						temp.stop();
					}catch(IOException e1) {
						e1.printStackTrace();
					}
				}
				
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
	
	//初始化服务器函数
	public void start() {
		clients = new ArrayList<ServerThread>();
		startServer start = new startServer(10);
		Thread startThread = new Thread(start);
		startThread.start();; // 服务器线程
	}
	
	//服务器总线程
	class startServer implements Runnable{
		private ServerSocket serverSocket;
		
		private int max; // 最大连接人数
		startServer(int max){
			this.max = max;
		}
		
		@Override
		public void run() {
			try {
				int port = 1234;
				serverSocket = new ServerSocket(port);
				center.append("服务器已启动，服务器最大连接人数为:" + max + "人，当前服务器端口号为:" + port + "\t\n");
				while (true) {
					Socket s = serverSocket.accept();
					//服务器已满则发送MAX消息，同时为该客户端服务
					if (max == clients.size()) {
						PrintWriter write = new PrintWriter(s.getOutputStream());
						write.println("MAX");
						write.close();
						s.close();
						continue;
					}
					
					//创建单独线程服务每个客户端
					ServerThread server = new ServerThread(s);
					server.start();
					clients.add(server);
					
					//刷新在线用户列表（右侧）
					JTextField now = new JTextField(server.getUser().getName());
					now.setHorizontalAlignment(SwingConstants.CENTER);
					now.setEditable(false);
					east.add(now);
					center.append(server.getUser().getName() + "/" + server.getUser().getId() + "上线\t\n");
				}
			}catch(IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//为每个客户端服务的线程
	class ServerThread extends Thread{
		private Socket socket;
		private BufferedReader read;
		private PrintWriter write;
		private UserInfo user;
		
		public ServerThread(Socket socket){
			try{
				this.socket = socket;
				read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				write = new PrintWriter(socket.getOutputStream());
				String info = read.readLine();
				StringTokenizer st = new StringTokenizer(info, "/"); // 发送来的用户连接的用户名和ID以/隔开
				user = new UserInfo(st.nextToken(), st.nextToken());
				write.println(user.getName() + "/" + user.getId() + "连接成功！！！");
				write.flush();
				
				//获取在线用户列表
				if(clients.size() > 0) {
					String list = "";
					for (int i = 0; i < clients.size(); i++) {
						list += clients.get(i).getUser().getName() + "@";
					}
					write.println("LIST@" + clients.size() + "@" + list);
					write.flush();
				}
				
				//向所有用户发送此用户上线信息
				if(clients.size() > 0) {
					for (int i = 0; i < clients.size(); i++) {
						clients.get(i).getWrite().println("ADD@" + user.getName());
						clients.get(i).getWrite().flush();
					}
				}
			}catch(IOException e1) {
				e1.printStackTrace();
			}
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			String message = null;
			while(true) {
				try{
					message = read.readLine();
					if (message.equals("CLOSE")) {
						center.append(user.getName() + user.getId() + "下线\t\n");
						read.close();
						write.close();
						socket.close();
						
						//给每个用户发送该用户下线的信息
						for (int i = 0; i < clients.size();i++) {
							clients.get(i).getWrite().println("DELETE@" + user.getName());
							clients.get(i).getWrite().flush();
						}
						
						//用户列表中删除该用户
						for (int i = 0; i < clients.size();i++) {
							if (clients.get(i).getUser().equals(user)) {
								east.remove(i + 1);
								east.repaint();
								east.validate();
								jfServer.repaint();
								jfServer.validate();
							}
						}
						
						//在存储块中删除该节点并停止该线程
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).getUser().equals(user)) {
								ServerThread temp = clients.get(i);
								clients.remove(i);
								temp.stop();
								return;
							}
						}
					}
					else {
						ForwardMessage(message);
					}
				}catch(IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		public void ForwardMessage(String message) {
			StringTokenizer st = new StringTokenizer(message, "@");
			String source = st.nextToken(); // 发送源
			String aim = st.nextToken(); // 目的源
			String content = st.nextToken(); // 内容
			message = source + "说:" + content;
			
			//判断是否为群发消息
			if(aim.equals("ALL")) {
				center.append(message + "（群发）");
				for (int i = 0; i < clients.size(); i++) {
					if (!clients.get(i).getUser().getName().equals(source)) {
						clients.get(i).getWrite().println(message);
						clients.get(i).getWrite().flush();
					}
				}
			}
			else {
				center.append(message);
				for (int i = 0; i < clients.size(); i++) {
					if (clients.get(i).getUser().getName().equals(aim)) {
						clients.get(i).getWrite().println(message);
						clients.get(i).getWrite().flush();
					}
				}
			}
		}
		
		public BufferedReader getRead() {
			return read;
		}

		public PrintWriter getWrite() {
			return write;
		}

		public UserInfo getUser() {
			return user;
		}
	}
}

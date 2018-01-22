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


//��Ϣ��ʽ:(�������@)Դ�û�����(/Դ�û�ID)@��������Ŀ���û����ƣ�/Ŀ���û�ID��@����
//�ؼ�����@�������û����ƺ�ID��/����
public class Server {
	private JFrame jfServer;
	private JButton send;
	private JTextArea field;
	private JPanel east;
	private JTextArea center;
	private JPanel south;
	private ArrayList<ServerThread> clients; // �û������̴߳洢�ṹ
	
	public Server() {
		initFrame();
		start();
	}
	
	//��ʼ�����溯��
	public void initFrame() {
		JTextField now = new JTextField("          ��ǰ����           ");
		now.setHorizontalAlignment(SwingConstants.CENTER);
		now.setEditable(false);
		now.setBackground(Color.WHITE);
		
		jfServer = new JFrame("����������");
		
		field = new JTextArea(3, 20);
		field.setFont(new Font("����", Font.PLAIN, 16));
		east = new JPanel();
		center = new JTextArea();
		south = new JPanel();
		send = new JButton("����");
		
		center.setFont(new Font("����", Font.PLAIN, 16));
		
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
		
		//���ý���ͼ��
		ImageIcon icon = new ImageIcon("IMG_20170125_170050.jpg");
		jfServer.setIconImage(icon.getImage());
		jfServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jfServer.setVisible(true);
		
		//������Ⱥ����ť
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					String message = field.getText();
					for (int i = 0; i < clients.size();i++) {
						clients.get(i).getWrite().println(message + "(������Ⱥ��)");
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
	
	//��ʼ������������
	public void start() {
		clients = new ArrayList<ServerThread>();
		startServer start = new startServer(10);
		Thread startThread = new Thread(start);
		startThread.start();; // �������߳�
	}
	
	//���������߳�
	class startServer implements Runnable{
		private ServerSocket serverSocket;
		
		private int max; // �����������
		startServer(int max){
			this.max = max;
		}
		
		@Override
		public void run() {
			try {
				int port = 1234;
				serverSocket = new ServerSocket(port);
				center.append("�������������������������������Ϊ:" + max + "�ˣ���ǰ�������˿ں�Ϊ:" + port + "\t\n");
				while (true) {
					Socket s = serverSocket.accept();
					//��������������MAX��Ϣ��ͬʱΪ�ÿͻ��˷���
					if (max == clients.size()) {
						PrintWriter write = new PrintWriter(s.getOutputStream());
						write.println("MAX");
						write.close();
						s.close();
						continue;
					}
					
					//���������̷߳���ÿ���ͻ���
					ServerThread server = new ServerThread(s);
					server.start();
					clients.add(server);
					
					//ˢ�������û��б��Ҳࣩ
					JTextField now = new JTextField(server.getUser().getName());
					now.setHorizontalAlignment(SwingConstants.CENTER);
					now.setEditable(false);
					east.add(now);
					center.append(server.getUser().getName() + "/" + server.getUser().getId() + "����\t\n");
				}
			}catch(IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//Ϊÿ���ͻ��˷�����߳�
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
				StringTokenizer st = new StringTokenizer(info, "/"); // ���������û����ӵ��û�����ID��/����
				user = new UserInfo(st.nextToken(), st.nextToken());
				write.println(user.getName() + "/" + user.getId() + "���ӳɹ�������");
				write.flush();
				
				//��ȡ�����û��б�
				if(clients.size() > 0) {
					String list = "";
					for (int i = 0; i < clients.size(); i++) {
						list += clients.get(i).getUser().getName() + "@";
					}
					write.println("LIST@" + clients.size() + "@" + list);
					write.flush();
				}
				
				//�������û����ʹ��û�������Ϣ
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
						center.append(user.getName() + user.getId() + "����\t\n");
						read.close();
						write.close();
						socket.close();
						
						//��ÿ���û����͸��û����ߵ���Ϣ
						for (int i = 0; i < clients.size();i++) {
							clients.get(i).getWrite().println("DELETE@" + user.getName());
							clients.get(i).getWrite().flush();
						}
						
						//�û��б���ɾ�����û�
						for (int i = 0; i < clients.size();i++) {
							if (clients.get(i).getUser().equals(user)) {
								east.remove(i + 1);
								east.repaint();
								east.validate();
								jfServer.repaint();
								jfServer.validate();
							}
						}
						
						//�ڴ洢����ɾ���ýڵ㲢ֹͣ���߳�
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
			String source = st.nextToken(); // ����Դ
			String aim = st.nextToken(); // Ŀ��Դ
			String content = st.nextToken(); // ����
			message = source + "˵:" + content;
			
			//�ж��Ƿ�ΪȺ����Ϣ
			if(aim.equals("ALL")) {
				center.append(message + "��Ⱥ����");
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

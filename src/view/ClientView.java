package view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import client.ClientObserver;
import controller.Controller;

public class ClientView extends JFrame implements ClientObserver{
	private static final long serialVersionUID = 2361740682291028404L;
	
	private Controller controller;
	private JButton login;
	private JButton requestUsers;
	private JButton logoff;
	private JButton requestFile;
	
	public ClientView(Controller c){
		super("Client");
		controller = c;
		controller.addObserver(this);
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BorderLayout());

		login = new JButton("Login");
		login.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Escribe tu id");
				controller.initClient(name);
				controller.connect();
			}
		});
		
		requestUsers = new JButton("RequestUsers");
		requestUsers.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.requestUsers();
			}
		});
		requestUsers.setVisible(false);
		
		requestFile = new JButton("RequestFile");
		requestFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Escribe el nombre del archivo");
				controller.requestFile(name);
			}
		});

		logoff = new JButton("Logoff");
		logoff.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.disconnect();
			}
		});
		logoff.setVisible(false);


		add(login, BorderLayout.WEST);
		add(requestUsers, BorderLayout.CENTER);
		add(logoff, BorderLayout.EAST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				controller.disconnect();
	            System.exit(0);
	        }
		});
	}

	@Override
	public void onError(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	@Override
	public void onConnect(String host, int port) {
		JOptionPane.showMessageDialog(this, "Connected to " + host + ":" + port);
		login.setVisible(false);
		requestUsers.setVisible(true);
		logoff.setVisible(true);
		ClientView.this.repaint();
	}

	@Override
	public void onUsersRequested(Set<String> users) {
		JOptionPane.showMessageDialog(this, users);
	}
	
	@Override
	public void onDisconnect(String host, int port) {
		JOptionPane.showMessageDialog(this, "Disconnected from " + host + ":" + port);
		requestUsers.setVisible(false);
		logoff.setVisible(false);
		login.setVisible(true);
		ClientView.this.repaint();
	}
}

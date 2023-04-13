package view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.ClientObserver;
import client.Client;

public class ClientView extends JFrame implements ClientObserver{
	
	Client client;

	public ClientView(Client client){
		super("Client");
		this.client = client;
		client.addObserver(this);
		client.setName(getUserName());
		client.loadSharedInfo();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
		setVisible(true);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				client.connectToServer();
			}
		});

		add(btnConnect);
	}

	private String getUserName() {
		System.out.println("Enter your user name: ");
		return System.console().readLine();
	}



	@Override
	public void onError(String message) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(this, message);
	}

	@Override
	public void onConnect(String host, int port) {
		//Cambiar de vista
		JOptionPane.showMessageDialog(this, "Connected to " + host + ":" + port);
	}
}

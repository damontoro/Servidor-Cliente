package view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import client.ClientObserver;
import controller.Controller;

@SuppressWarnings("serial")
public class ClientView extends JFrame implements ClientObserver{
	Controller controller;
	
	public ClientView(Controller c){
		super("Client");
		controller = c;
		controller.addObserver(this);
		initGUI();
	}
	
	private void initGUI() {
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showInputDialog("Escribe tu id");
				controller.connect();
			}
		});

		add(btnRegister);
		
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
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(this, message);
	}

	@Override
	public void onConnect(String host, int port) {
		//Cambiar de vista
		JOptionPane.showMessageDialog(this, "Connected to " + host + ":" + port);
	}
}

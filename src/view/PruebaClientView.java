package view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Set;

import client.ClientObserver;
import controller.Controller;

public class PruebaClientView extends JFrame implements ClientObserver{
	private static final long serialVersionUID = 2361740682291028404L;
	
	private Controller controller;
	private JButton login;
	private JPanel filesPanel;
	private JPanel leftPanel;
	
	public PruebaClientView(Controller c){
		super("Client");
		controller = c;
		controller.addObserver(this);
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new FlowLayout());

		//TODO Cambiar lo de abajo y hacer una llamada a controller.getFiles()
		JScrollPane scroll = new JScrollPane(new FilesView(controller, Arrays.asList("Libro1.txt","Libro2.txt"
		,"Libro2aaaaaaaaaaaaaaaa.txt","Libro2.txt","Libro2.txt","Libro2.txt","Libro2.txt","Libro2.txt","Libro2.txt","Libro2.txt")));

		scroll.setPreferredSize(new Dimension(150, 250));
		scroll.getVerticalScrollBar().setUnitIncrement(10);

		filesPanel = createViewPanel(scroll, "Files");

		leftPanel = createViewPanel(new LeftPanel(controller), "Actions");

		filesPanel.setVisible(false);
		leftPanel.setVisible(false);

		login = new JButton("Login");
		login.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Escribe tu id");
				if(name != null) {
					controller.initClient(name);
					controller.connect();
				}
			}
		});

		add(login);
		add(filesPanel);
		add(leftPanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 400);
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				controller.disconnect();
	            System.exit(0);
	        }
		});
	}

	private JPanel createViewPanel(JComponent c, String title) {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));
		Border b = BorderFactory.createLineBorder(Color.black, 2);
		p.setBorder(BorderFactory.createTitledBorder(b, title));
		p.add(c);
		return p;
	}

	@Override
	public void onError(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	@Override
	public void onConnect(String host, int port) {
		JOptionPane.showMessageDialog(this, "Connected to " + host + ":" + port);
		login.setVisible(false);
		filesPanel.setVisible(true);
		leftPanel.setVisible(true);
		PruebaClientView.this.repaint();
	}

	@Override
	public void onUsersRequested(Set<String> users) {
		JOptionPane.showMessageDialog(this, users);
	}
	
	@Override
	public void onDisconnect(String host, int port) {
		JOptionPane.showMessageDialog(this, "Disconnected from " + host + ":" + port);
		filesPanel.setVisible(false);
		leftPanel.setVisible(false);
		login.setVisible(true);
		PruebaClientView.this.repaint();
	}

	
}

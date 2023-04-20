package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.Controller;

public class LeftPanel extends JPanel{
	

	private Controller controller;
	private JButton login;
	private JButton requestUsers;
	private JButton logoff;

	protected LeftPanel(Controller con){
		controller = con;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		iniGui();
	}

	private void iniGui() {

		requestUsers = new JButton("RequestUsers");
		requestUsers.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.requestUsers();
			}
		});
		requestUsers.setVisible(true);
		
		logoff = new JButton("Logoff");
		logoff.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.disconnect();
			}
		});
		logoff.setVisible(true);


		add(requestUsers);
		add(logoff);
	}

}

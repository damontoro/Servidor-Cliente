package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import controller.Controller;

public class LeftPanel extends JPanel{
	private static final long serialVersionUID = -2192892941931864101L;
	
	private Controller controller;
	private JButton requestUsers;
	private JButton reloadFiles;
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
		
		reloadFiles = new JButton("ReloadFiles");
		reloadFiles.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.getFileList();
			}
		});
		reloadFiles.setVisible(true);

		logoff = new JButton("Logoff");
		logoff.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.disconnect();
			}
		});
		logoff.setVisible(true);


		add(requestUsers);
		add(reloadFiles);
		add(logoff);
	}

}

package view;

import controller.Controller;

import javax.swing.JPanel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class MyFilesView extends JPanel{
	private static final long serialVersionUID = -740741922068189597L;
	
	Set<String> files;
	Controller con;

	public MyFilesView(Controller con, Set<String> files){
		this.files = files;
		this.con = con;

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		iniButtons();
	}

	public void updateFiles(Set<String> files){
		this.files = files;
		this.removeAll();
		iniButtons();
		revalidate();
		repaint();
	}

	private void iniButtons(){
		for(String file : files){
			createPanel(file, addButton(file));
		}
	}

	private void createPanel(String file, JButton button){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(new JLabel(file));
		panel.add(button);
		add(panel);
	}

	private JButton addButton(String file){
		JButton button = new JButton("Borrar");
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(null, "Seguro que quieres eliminar el archivo?");
				if(res == JOptionPane.YES_OPTION){
					con.deleteFile(file);
				}
			}
			
		});
		return button;
	}
}

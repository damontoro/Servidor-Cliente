package view;

import client.Client;
import controller.Controller;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesView extends JPanel{

	List<String> files;
	List<JButton> buttons;
	Map<String, JButton> buttonsMap;
	Controller con;

	public FilesView(Controller con, List<String> files){
		this.files = files;
		this.buttons = new ArrayList<JButton>();
		this.buttonsMap = new HashMap<String, JButton>();
		this.con = con;

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		iniButtons();
	}


	public void addFile(String file){
		files.add(file);
		addButton(file);
	}

	private void iniButtons(){
		for(String file : files){
			addButton(file);
		}
	}

	private void addButton(String file){
		JButton button = new JButton(file);
		buttonsMap.put(file, button);
		buttons.add(button);
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(null, "Seguro que te quieres descargar el archivo " + file + "?");
				if(res == JOptionPane.YES_OPTION){
					System.out.println("Descargando archivo " + file);
					con.requestFile(file);
				}
			}
			
		});
		add(button);
	}
}

package launcher;

import javax.swing.SwingUtilities;

import client.Client;
import controller.Controller;
import view.ClientView;

public class MainClient {

	public static void main(String[] args) {
		Client client = new Client();
		Controller controller = new Controller(client);
		SwingUtilities.invokeLater(() -> new ClientView(controller));
	}

}
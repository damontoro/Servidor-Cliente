package launcher;

import javax.swing.SwingUtilities;

import client.Client;
import view.ClientView;

public class MainClient {

	public static void main(String[] args) {
		Client client = new Client();
		SwingUtilities.invokeLater(() -> new ClientView(client));
	}

}
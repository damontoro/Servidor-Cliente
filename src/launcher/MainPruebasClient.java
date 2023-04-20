package launcher;

import javax.swing.SwingUtilities;

import client.Client;
import controller.Controller;
import view.PruebaClientView;

public class MainPruebasClient {
	public static void main(String[] args) {
		try {
			Client client = new Client();
			Controller controller = new Controller(client);
			SwingUtilities.invokeLater(() -> new PruebaClientView(controller));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

package server;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import client.User;

public class ServerListener implements Runnable{

	private Socket socket;
	private Server server;

	public ServerListener(Socket s, Server server) {
		socket = s;
		this.server = server;
	}

	@Override
	public void run() {
		try{
            System.out.println("Socket Extablished...");
            // Create input and output streams to client
            ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());

			User aux = (User)inFromClient.readObject();
			server.addUser(aux);


			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

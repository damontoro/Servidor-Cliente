package server;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import message.LogoffMessage;
import message.Message;
import server.commands.ServerCommand;

public class ClientHandler implements Runnable{
	private Socket socket;
	private Server server;

	public ClientHandler(Socket s, Server server) {
		socket = s;
		this.server = server;
	}

	@Override
	public void run() {
		try{
			Message<?> message;
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			do {
				message = (Message<?>) inStream.readObject();
	
				ServerCommand c = ServerCommand.getCommand(message);
				c.execute(server, outStream);
			} while(!message.getType().equals(LogoffMessage.TYPE));
			socket.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

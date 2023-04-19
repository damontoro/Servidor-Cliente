package server;

import java.net.Socket;
import java.io.ObjectInputStream;

import message.LogoffMessage;
import message.Message;
import server.commands.Command;

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
			do {
	            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				message = (Message<?>) inStream.readObject();
	
				Command c = Command.getCommand(message);
				c.execute(server, socket);
			}
			while(!message.getType().equals(LogoffMessage.TYPE));
			socket.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

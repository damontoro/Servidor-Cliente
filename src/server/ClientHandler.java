package server;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import client.User;
import message.LoginMessage;
import message.LogoffMessage;
import message.ExceptionMessage;
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
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			Message<?> message = (Message<?>) inStream.readObject();

			Command c = Command.getCommand(message);
			c.execute(server, socket);
			
			inStream.close();
			socket.close();
			
			server.printUsersInfo();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

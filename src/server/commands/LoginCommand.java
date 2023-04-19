package server.commands;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.User;
import message.ConnectedMessage;
import message.LoginMessage;
import message.Message;
import server.Server;

public class LoginCommand extends Command {
	private User u;
	
	@Override
	public void execute(Server server, Socket s) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(new ConnectedMessage(server.addUser(u)));
		out.close();
	}

	@Override
	protected Command parse(Message<?> message) {
		if(message.getType().equals(LoginMessage.TYPE)) {
			u = ((LoginMessage) message).retrieveInfo();
			return this;
		}
		else
			return null;
	}
}

package server.commands;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.User;
import message.ConnexionMessage;
import message.LoginMessage;
import message.Message;
import server.Server;

public class LoginCommand extends Command {
	private User u;
	
	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws IOException{
		outStream.writeObject(new ConnexionMessage(server.addUser(u, outStream)));
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

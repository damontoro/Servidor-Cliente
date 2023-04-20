package server.commands;

import java.io.ObjectOutputStream;

import client.User;
import message.ConnexionMessage;
import message.LoginMessage;
import message.Message;
import server.Server;

public class LoginCommand extends ServerCommand {
	private String origin;
	private User user;
	
	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception{
		outStream.writeObject(new ConnexionMessage(
				"server",
				origin,
				server.addUser(user, outStream)
		));
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(LoginMessage.TYPE)) {
			origin = message.getOrigin();
			user = ((LoginMessage) message).retrieveInfo();
			return this;
		}
		else
			return null;
	}
}

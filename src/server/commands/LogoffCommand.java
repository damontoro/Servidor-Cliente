package server.commands;

import java.io.ObjectOutputStream;

import client.User;
import message.DisconnectedMessage;
import message.LogoffMessage;
import message.Message;
import server.Server;

public class LogoffCommand extends ServerCommand {
	private String origin;
	private User user;
	
	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		server.removeUser(user);
		outStream.writeObject(new DisconnectedMessage("server", origin));
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(LogoffMessage.TYPE)) {
			origin = message.getOrigin();
			user = ((LogoffMessage) message).retrieveInfo();
			return this;
		}
		else
			return null;
	}
}

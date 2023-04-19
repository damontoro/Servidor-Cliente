package server.commands;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.User;
import message.DisconnectedMessage;
import message.LogoffMessage;
import message.Message;
import server.Server;

public class LogoffCommand extends Command {
	private User u;
	
	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws IOException {
		server.removeUser(u);
		outStream.writeObject(new DisconnectedMessage());
	}

	@Override
	protected Command parse(Message<?> message) {
		if(message.getType().equals(LogoffMessage.TYPE)) {
			u = ((LogoffMessage) message).retrieveInfo();
			return this;
		}
		else
			return null;
	}

}

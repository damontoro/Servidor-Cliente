package server.commands;

import java.net.Socket;

import client.User;
import message.LogoffMessage;
import message.Message;
import server.Server;

public class LogoffCommand extends Command {
	private User u;
	
	@Override
	public void execute(Server server, Socket s) {
		server.removeUser(u);
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

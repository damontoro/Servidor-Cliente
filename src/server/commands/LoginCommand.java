package server.commands;

import java.net.Socket;

import client.User;
import message.LoginMessage;
import message.Message;
import server.Server;

public class LoginCommand extends Command {
	private User u;
	
	@Override
	public void execute(Server server, Socket s) {
		server.addUser(u);
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

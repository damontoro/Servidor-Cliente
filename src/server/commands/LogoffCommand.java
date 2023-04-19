package server.commands;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.User;
import message.ConnectedMessage;
import message.LogoffMessage;
import message.Message;
import server.Server;

public class LogoffCommand extends Command {
	private User u;
	
	@Override
	public void execute(Server server, Socket s) throws IOException {
		server.removeUser(u);
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(new ConnectedMessage(false));
		out.close();
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

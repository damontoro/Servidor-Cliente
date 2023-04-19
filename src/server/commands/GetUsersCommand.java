package server.commands;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.GetUsersMessage;
import message.Message;
import server.Server;

public class GetUsersCommand extends Command{
	@Override
	public void execute(Server server, Socket s) throws IOException{
		ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());
		server.sendUsersInfo(outStream);
	}

	@Override
	protected Command parse(Message<?> message) {
		if(message.getType().equals(GetUsersMessage.TYPE)) {
			return this;
		}
		else
			return null;
	}
}

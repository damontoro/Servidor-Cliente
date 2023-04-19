package server.commands;

import java.io.IOException;
import java.io.ObjectOutputStream;

import message.GetUsersMessage;
import message.Message;
import server.Server;

public class GetUsersCommand extends Command{
	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws IOException{
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

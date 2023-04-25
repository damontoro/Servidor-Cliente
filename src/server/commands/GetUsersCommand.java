package server.commands;

import java.io.ObjectOutputStream;

import message.GetUsersMessage;
import message.Message;
import server.Server;

public class GetUsersCommand extends ServerCommand{
	private String origin;
	
	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception{
		server.sendUsersInfo(outStream, origin);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(GetUsersMessage.TYPE)) {
			origin = message.getOrigin();
			return this;
		}
		else
			return null;
	}
}

package server.commands;

import java.io.ObjectOutputStream;

import message.Message;
import message.RequestFileListMessage;
import server.Server;

public class RequestFileListCommand extends ServerCommand{

	private String origin;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		server.sendFileList(origin);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(RequestFileListMessage.TYPE)){
			origin = message.getOrigin();
			return this;
		}
		return null;
	}
	
}

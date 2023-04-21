package server.commands;

import java.io.ObjectOutputStream;

import message.Message;
import message.NewFileMessage;
import server.Server;

public class NewFileCommand extends ServerCommand{
	private String file, origin;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		server.addFile(origin, file);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(NewFileMessage.TYPE)){
			file = (String)message.retrieveInfo();
			origin = message.getOrigin();
			return this;
		}
		return null;
	}
	
}

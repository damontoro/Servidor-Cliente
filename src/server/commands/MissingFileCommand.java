package server.commands;

import java.io.ObjectOutputStream;

import message.FileNotFoundMessage;
import message.Message;
import server.Server;

public class MissingFileCommand extends ServerCommand{

	private String origin, destination, file;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		String userName = server.manageMissingFile(origin,destination, file);
		
		if(userName == null)
			server.fileNotFound("server", origin, file);
		else	
			server.requestFile(origin, userName, file);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(FileNotFoundMessage.TYPE)){
			origin = message.getOrigin();
			destination = message.getDestination();
			file = ((FileNotFoundMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
	
}

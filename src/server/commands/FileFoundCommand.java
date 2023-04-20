package server.commands;

import java.io.ObjectOutputStream;

import message.FileFoundMessage;
import message.Message;
import server.Server;

public class FileFoundCommand extends ServerCommand{
	private String origin;
	private String destination;
	private String file;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		server.fileFound(origin, destination, file);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(FileFoundMessage.TYPE)) {
			origin = message.getOrigin();
			destination = message.getDestination();
			file = ((FileFoundMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

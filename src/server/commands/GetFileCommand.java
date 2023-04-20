package server.commands;

import java.io.ObjectOutputStream;

import message.GetFileMessage;
import message.Message;
import server.Server;

public class GetFileCommand extends ServerCommand{
	private String origin;
	private String file;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		String userName = server.findUserWithFile(file);
		if(userName == null)
			server.fileNotFounded("server", origin, file);
		else	
			server.requestFile(origin, userName, file);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals(GetFileMessage.TYPE)) {
			origin = message.getOrigin();
			file = ((GetFileMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
	
}

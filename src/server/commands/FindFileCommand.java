package server.commands;

import java.io.ObjectOutputStream;

import message.GetFileMessage;
import message.Message;
import server.Server;

public class FindFileCommand extends Command{

	private String name;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		String userName = server.findUserWithFile(name);
		server.requestFile(userName, name);
	}

	@Override
	protected Command parse(Message<?> message) {
		if(message.getType().equals(GetFileMessage.TYPE)) {
			name = (String) message.retrieveInfo();
			return this;
		}
		return null;
	}
	
}

package client.commands;

import client.Client;
import message.FileNotFoundMessage;
import message.Message;

public class FileNotFoundCommand extends ClientCommand{
	private String file;

	@Override
	public void execute(Client cli) {
		cli.onFileNotFound(file);
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(FileNotFoundMessage.TYPE)) {
			file = ((FileNotFoundMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

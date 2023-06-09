package client.commands;

import client.Client;
import message.FileFoundMessage;
import message.Message;

public class FileFoundCommand extends ClientCommand{
	private String peer, file;

	@Override
	public void execute(Client cli) {
		cli.onPeerFound(peer, file);
	}

	@Override
	
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(FileFoundMessage.TYPE)) {
			peer = message.getOrigin();
			file = ((FileFoundMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

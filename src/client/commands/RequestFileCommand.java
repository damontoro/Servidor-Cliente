package client.commands;

import message.Message;
import message.RequestFileMessage;
import client.Client;

public class RequestFileCommand extends ClientCommand{

	private String name;

	@Override
	public void execute(Client cli) {
		cli.searchFile(name);
	}

	@Override
	public ClientCommand parse(Message<?> message) {
		if(message.getType().equals(RequestFileMessage.TYPE)){
			name = ((RequestFileMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
	
}

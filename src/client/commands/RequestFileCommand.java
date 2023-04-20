package client.commands;

import java.io.ObjectOutputStream;

import message.Message;
import client.Client;

public class RequestFileCommand extends ClientCommand{

	private String name;

	@Override
	public void execute(Client cli) {
		cli.searchFile(name);
	}

	@Override
	public ClientCommand parse(Message<?> message) {
		if(message.getType().equals("RequestFile")){
			name = (String) message.retrieveInfo();
			return this;
		}
		return null;
	}
	
}

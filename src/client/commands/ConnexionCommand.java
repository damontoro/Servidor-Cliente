package client.commands;

import client.Client;
import message.ConnexionMessage;
import message.Message;

public class ConnexionCommand extends ClientCommand{
	private boolean connected;
	
	@Override
	public void execute(Client cli) {
		cli.onConnexionEstablished(connected);
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(ConnexionMessage.TYPE)){
			connected = ((ConnexionMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}

}

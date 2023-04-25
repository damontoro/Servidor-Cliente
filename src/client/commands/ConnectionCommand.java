package client.commands;

import client.Client;
import message.ConnectionMessage;
import message.Message;

public class ConnectionCommand extends ClientCommand{
	private boolean connected;
	
	@Override
	public void execute(Client cli) {
		cli.onConnectionEstablished(connected);
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(ConnectionMessage.TYPE)){
			connected = ((ConnectionMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

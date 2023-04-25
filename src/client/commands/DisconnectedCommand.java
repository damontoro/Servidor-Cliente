package client.commands;

import client.Client;
import message.DisconnectedMessage;
import message.Message;

public class DisconnectedCommand extends ClientCommand{
	@Override
	public void execute(Client cli) {
		cli.onDisconnect();
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(DisconnectedMessage.TYPE)){
			return this;
		}
		return null;
	}
}

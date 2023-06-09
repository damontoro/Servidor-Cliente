package client.commands;

import client.Client;
import message.Message;
import message.P2PInfo;
import message.StartConnectionMessage;

public class StartConnectionCommand extends ClientCommand{
	private P2PInfo info;

	@Override
	public void execute(Client cli) {
		cli.startP2PConnection(info);
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(StartConnectionMessage.TYPE)){
			info = ((StartConnectionMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

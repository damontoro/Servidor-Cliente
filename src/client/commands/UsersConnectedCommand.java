package client.commands;

import java.util.Set;

import client.Client;
import message.Message;
import message.UsersConnectedMessage;

public class UsersConnectedCommand extends ClientCommand {
	private Set<String> users;
	
	@Override
	public void execute(Client cli) {
		cli.onUsersRequested(users);
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(UsersConnectedMessage.TYPE)){
			users = ((UsersConnectedMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

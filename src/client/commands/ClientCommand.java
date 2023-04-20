package client.commands;

import java.io.ObjectOutputStream;
import client.Client;

import message.Message;

public abstract class ClientCommand {
	protected static final String UNKNOWN_COMMAND_MSG = "Unknown command";
	
	protected static final ClientCommand[] AVAILABLE_COMMANDS = {
		new RequestFileCommand()
	};

	public static ClientCommand getCommand(Message<?> message) throws IllegalArgumentException {
		for(ClientCommand command : AVAILABLE_COMMANDS) {
			if(command.parse(message) != null)
				return command;
		}
		throw new IllegalArgumentException ("[ERROR]: "+ UNKNOWN_COMMAND_MSG + "\n");
	}

	public abstract void execute(Client cli) throws Exception;

	protected abstract ClientCommand parse(Message<?> message);
}

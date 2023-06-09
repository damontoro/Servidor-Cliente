package client.commands;

import client.Client;

import message.Message;

public abstract class ClientCommand {
	protected static final String UNKNOWN_COMMAND_MSG = "Unknown command";
	
	protected static final ClientCommand[] AVAILABLE_COMMANDS = {
		new ConnectionCommand(),
		new DisconnectedCommand(),
		new UsersConnectedCommand(),
		new RetrieveFileListCommand(),
		new RequestFileCommand(),
		new FileFoundCommand(),
		new FileNotFoundCommand(),
		new StartConnectionCommand()
	};

	public static ClientCommand getCommand(Message<?> message) throws IllegalArgumentException {
		for(ClientCommand command : AVAILABLE_COMMANDS) {
			if(command.parse(message) != null)
				return command;
		}
		throw new IllegalArgumentException ("[ERROR]: "+ UNKNOWN_COMMAND_MSG + "\n");
	}

	public abstract void execute(Client cli);

	protected abstract ClientCommand parse(Message<?> message);
}

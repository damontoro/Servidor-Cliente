package server.commands;

import java.io.ObjectOutputStream;

import message.Message;
import server.Server;

public abstract class ServerCommand {
	protected static final String UNKNOWN_COMMAND_MSG = "Unknown command";
	
	protected static final ServerCommand[] AVAILABLE_COMMANDS = {
		new LoginCommand(),
		new LogoffCommand(),
		new GetUsersCommand(),
		new GetFileCommand(),
		new FileFoundCommand()
	};

	public static ServerCommand getCommand(Message<?> message) throws IllegalArgumentException {
		for(ServerCommand command : AVAILABLE_COMMANDS) {
			if(command.parse(message) != null)
				return command;
		}
		throw new IllegalArgumentException ("[ERROR]: "+ UNKNOWN_COMMAND_MSG + "\n");
	}

	public abstract void execute(Server server, ObjectOutputStream outStream) throws Exception;

	protected abstract ServerCommand parse(Message<?> message);
}

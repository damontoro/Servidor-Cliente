package server.commands;

import java.io.ObjectOutputStream;

import message.Message;
import server.Server;

public abstract class Command {
	protected static final String UNKNOWN_COMMAND_MSG = "Unknown command";
	
	protected static final Command[] AVAILABLE_COMMANDS = {
		new LoginCommand(),
		new LogoffCommand(),
		new GetUsersCommand()
	};

	public static Command getCommand(Message<?> message) throws IllegalArgumentException {
		for(Command command : AVAILABLE_COMMANDS) {
			if(command.parse(message) != null)
				return command;
		}
		throw new IllegalArgumentException ("[ERROR]: "+ UNKNOWN_COMMAND_MSG + "\n");
	}

	public abstract void execute(Server server, ObjectOutputStream outStream) throws Exception;

	protected abstract Command parse(Message<?> message);
}

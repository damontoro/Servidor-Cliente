package server.commands;

import java.io.ObjectOutputStream;

import message.Message;
import message.P2PInfo;
import server.Server;

public class StartConnectionCommand extends ServerCommand{

	private P2PInfo info;
	private String destination;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		server.sendP2PInfo(destination, info);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals("StartConnection")){
			info = (P2PInfo) message.retrieveInfo();
			return this;
		}
		return null;
	}
	
}

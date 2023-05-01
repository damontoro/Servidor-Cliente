package server.commands;

import java.io.ObjectOutputStream;

import message.Message;
import message.P2PInfo;
import server.Server;

public class StartConnectionCommand extends ServerCommand{

	private P2PInfo info;
	private String destination, origin;

	@Override
	public void execute(Server server, ObjectOutputStream outStream) throws Exception {
		server.sendP2PInfo(origin, destination, info);
	}

	@Override
	protected ServerCommand parse(Message<?> message) {
		if(message.getType().equals("StartConnection")){
			origin = message.getOrigin();
			destination = message.getDestination();
			info = (P2PInfo) message.retrieveInfo();
			return this;
		}
		return null;
	}
	
}

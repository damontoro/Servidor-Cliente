package client;

import java.io.ObjectInputStream;

import client.commands.ClientCommand;
import message.DisconnectedMessage;
import message.Message;


public class ServerHandler implements Runnable{
	private ObjectInputStream inSS;
	private Client client;

	public ServerHandler(ObjectInputStream inSS, Client c) {
		this.inSS = inSS;
		this.client = c;
	}

	@Override
	public void run() {
		try{
			Message<?> message;
			do {
				message = (Message<?>) inSS.readObject();
	
				ClientCommand c = ClientCommand.getCommand(message);
				c.execute(client);
			} while(!message.getType().equals(DisconnectedMessage.TYPE));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

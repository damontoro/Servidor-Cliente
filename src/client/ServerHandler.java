package client;

import java.io.ObjectInputStream;

import message.ConnexionMessage;
import message.DisconnectedMessage;
import message.Message;
import message.UsersConnectedMessage;


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
	
				if(message.getType().equals(ConnexionMessage.TYPE)) {
					client.onConnexionEstablished(((ConnexionMessage) message).retrieveInfo());	
				}
				else if(message.getType().equals(UsersConnectedMessage.TYPE)) {
					client.onUsersRequested(((UsersConnectedMessage) message).retrieveInfo());
				}
				else {
					client.onDisconnect();
				}
			}
			while(!message.getType().equals(DisconnectedMessage.TYPE));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

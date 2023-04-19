package client;

import java.io.File;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import message.ConnectedMessage;
import message.GetUsersMessage;
import message.LoginMessage;
import message.LogoffMessage;
import message.Message;
import message.UsersConnectedMessage;
import server.Server;

import java.util.HashSet;

public class Client implements Observable<ClientObserver>{
	private User user;
	private List<ClientObserver> observers;
	private boolean connected;
	private Semaphore requestUsersSem;

	public Client() throws UnknownHostException {
		user = new User(getIp());
		observers = new ArrayList<ClientObserver>();
		connected = false;
		requestUsersSem = new Semaphore(1);
	}
	
	private Message<?> communicateWithServer(Message<?> request) throws Exception {
		Socket s = new Socket(Server.HOST, Server.PORT);
		ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

		outStream.writeObject(request);
		outStream.flush();
		
		ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

		Message<?> reply = (Message<?>) inStream.readObject();
		
		inStream.close();
		outStream.close();
		s.close();
		
		return reply;
	}

	public void connect() {
		try{
			ConnectedMessage reply = (ConnectedMessage) communicateWithServer(new LoginMessage(user));
			
			connected = reply.retrieveInfo();
			
			if(!connected)
				throw new Exception("User already connected");

			for(ClientObserver o : observers){
				o.onConnect(Server.HOST, Server.PORT);
			}
		}
		catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}
	
	public void requestUsers() {
		try{
			UsersConnectedMessage reply = (UsersConnectedMessage) communicateWithServer(new GetUsersMessage());
			
			Set<String> users = reply.retrieveInfo();
			
			requestUsersSem.acquire();
			for(ClientObserver o : observers){
				o.onUsersRequested(users);
			}
			requestUsersSem.release();
		}
		catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}
	
	public void disconnect() {
		if(connected) {
			try{
				communicateWithServer(new LogoffMessage(user));
				
				connected = false;
				
				for(ClientObserver o : observers){
					o.onDisconnect(Server.HOST, Server.PORT);
				}
			}
			catch(Exception e){
				for(ClientObserver o : observers){
					o.onError(e.getMessage());
				}
			}
		}
	}

	public void loadSharedInfo() {
		Set<String> sharedInfo = new HashSet<String>();
		File folder = new File("data" + File.separator + user.getId());
		if(!folder.exists())
			folder.mkdir();
		
		for(var file : folder.listFiles()){
			sharedInfo.add(file.getName());
		}
		user.setSharedInfo(sharedInfo);
	}

	public void setName(String name){ user.setId(name); }

	private InetAddress getIp() throws UnknownHostException {
		return InetAddress.getLocalHost();
	}

	@Override
	public void addObserver(ClientObserver o) {observers.add(o);}
	
	@Override
	public void removeObserver(ClientObserver o) {observers.remove(o);}
}

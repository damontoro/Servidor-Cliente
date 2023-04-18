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

import message.ConnexionMessage;
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
	
	private void sendMessage(Socket s, Message<?> message) throws Exception {
		ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

		outStream.writeObject(message);
		outStream.flush();
	}
	
	private Message<?> receiveMessage(Socket s) throws Exception{
		ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
		Message<?> m = (Message<?>) inStream.readObject();
		
		inStream.close();
		s.close();
		
		return m;
	}
	

	public void connect() {
		try{
			Socket serverSocket = new Socket(Server.HOST, Server.PORT);
			sendMessage(serverSocket, new LoginMessage(user));
			ConnexionMessage reply = (ConnexionMessage) receiveMessage(serverSocket);
			
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
			Socket serverSocket = new Socket(Server.HOST, Server.PORT);
			sendMessage(serverSocket, new GetUsersMessage());
			UsersConnectedMessage reply = (UsersConnectedMessage) receiveMessage(serverSocket);
			
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
				Socket serverSocket = new Socket(Server.HOST,Server.PORT);
				
				ObjectOutputStream outStream = new ObjectOutputStream(serverSocket.getOutputStream());
	
				outStream.writeObject(new LogoffMessage(user));
				outStream.flush();
				
				outStream.close();
				serverSocket.close();
				
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

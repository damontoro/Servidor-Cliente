package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import message.GetUsersMessage;
import message.LoginMessage;
import message.LogoffMessage;
import server.Server;

import java.util.HashSet;

public class Client implements Observable<ClientObserver>{
	private User user;
	private List<ClientObserver> observers;
	private boolean connected;
	
	private Socket serverSocket;
	private ObjectOutputStream outSS;
	private ObjectInputStream inSS;
	
	private Thread serverListener;
	public Semaphore readFromServer;

	public Client() throws UnknownHostException {
		user = new User(getIp());
		observers = new ArrayList<ClientObserver>();
		connected = false;
		readFromServer = new Semaphore(0);
	}

	public void connect() {
		try{
			serverSocket = new Socket(Server.HOST, Server.PORT);
			outSS = new ObjectOutputStream(serverSocket.getOutputStream());
			
			outSS.writeObject(new LoginMessage(user));
			readFromServer.release();
			
			inSS = new ObjectInputStream(serverSocket.getInputStream());
			
			serverListener = new Thread(new ServerHandler(inSS, this));
			serverListener.start();
		}
		catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}
	
	public void requestUsers() {
		try{
			outSS.writeObject(new GetUsersMessage());
			readFromServer.release();
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
				outSS.writeObject(new LogoffMessage(user));
				readFromServer.release();
			}
			catch(Exception e){
				for(ClientObserver o : observers){
					o.onError(e.getMessage());
				}
			}
		}
	}
	
	public void onConnexionEstablished(boolean c) {
		connected = c;
		if(connected) {
			for(ClientObserver o : observers){
				o.onConnect(Server.HOST, Server.PORT);
			}
		}
		else {
			for(ClientObserver o : observers){
				o.onError("User already connected");
			}
		}
	}
	
	public void onUsersRequested(Set<String> users) {
		for(ClientObserver o : observers){
			o.onUsersRequested(users);
		}
	}
	
	public void onDisconnect() {
		try {
			outSS.close();
			inSS.close();
			serverSocket.close();
			connected = false;
			for(ClientObserver o : observers){
				o.onDisconnect(Server.HOST, Server.PORT);
			}
		} catch (IOException e) {
			for(ClientObserver o : observers){
				o.onError("User already connected");
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

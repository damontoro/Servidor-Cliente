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

import message.GetUsersMessage;
import message.LoginMessage;
import message.LogoffMessage;
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

	public void connect() {
		try{
			Socket serverSocket = new Socket(Server.HOST, Server.PORT);
			
			ObjectOutputStream outStream = new ObjectOutputStream(serverSocket.getOutputStream());

			outStream.writeObject(new LoginMessage(user.getIp().getHostName(), Server.HOST, user));
			outStream.flush();

			ObjectInputStream inStream = new ObjectInputStream(serverSocket.getInputStream());
			connected = inStream.readBoolean();

			inStream.close();
			outStream.close();
			serverSocket.close();


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
			
			ObjectOutputStream outStream = new ObjectOutputStream(serverSocket.getOutputStream());
			outStream.writeObject(new GetUsersMessage(user.getIp().getHostName(), Server.HOST));
			outStream.flush();
			
			ObjectInputStream inStream = new ObjectInputStream(serverSocket.getInputStream());
			
			Set<String> users = new HashSet<String>();
			int size = inStream.readInt();
			for(int i = 0; i < size; ++i) {
				users.add((String) inStream.readObject());
			}
			
			outStream.close();
			inStream.close();
			serverSocket.close();
			
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
	
				outStream.writeObject(new LogoffMessage(user.getIp().getHostName(), Server.HOST, user));
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

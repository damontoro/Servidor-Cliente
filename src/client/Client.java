package client;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Client implements Observable<ClientObserver>{
	private static final int PORT = 8000;
	private static final String HOST = "localhost";

	private User user;
	private List<ClientObserver> observers;

	public Client(){
		user = new User(getIp());
		observers = new ArrayList<ClientObserver>();
	}

	public void connect(){
		try{
			Socket serverSocket = new Socket(HOST, PORT);
			for(var o : observers){
				o.onConnect(HOST, PORT);
			}
			sendData(serverSocket);
		}catch(Exception e){
			for(var o : observers){
				o.onError(e.getMessage());
			}
		}
	}

	private void sendData(Socket serverSocket) {
		try{
			ObjectOutputStream outToServer = new ObjectOutputStream(serverSocket.getOutputStream());

			outToServer.writeObject(user);
			outToServer.flush();
		}catch(Exception e){
			for(var o : observers){
				o.onError(e.getMessage());
			}
		}
	}
	
	public void disconnect() {
		
	}

	public void loadSharedInfo() {
		Set<String> sharedInfo = new HashSet<String>();
		File folder = new File("data" + File.separator + user.getId());
		
		for(var file : folder.listFiles()){
			sharedInfo.add(file.getName());
		}
		user.setSharedInfo(sharedInfo);
	}

	public void setName(String name){
		user.setId(name);
	}

	private InetAddress getIp() {
		try{
			return InetAddress.getLocalHost();
		}catch(UnknownHostException e){
			for(var o : observers){
				o.onError(e.getMessage());
			}
			return null;
		}
	}

	@Override
	public void addObserver(ClientObserver o) {observers.add(o);}
	
	@Override
	public void removeObserver(ClientObserver o) {observers.remove(o);}
}

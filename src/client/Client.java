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

import javax.swing.JOptionPane;

import message.FileFoundMessage;
import message.FileNotFoundMessage;
import message.GetFileMessage;
import message.GetUsersMessage;
import message.LoginMessage;
import message.LogoffMessage;
import message.P2PInfo;
import message.StartConnectionMessage;
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

	public Client() throws UnknownHostException {
		user = new User(getIp());
		observers = new ArrayList<ClientObserver>();
		connected = false;
	}

	public void connect() {
		try{
			serverSocket = new Socket(Server.HOST, Server.PORT);
			outSS = new ObjectOutputStream(serverSocket.getOutputStream());
			
			outSS.writeObject(new LoginMessage(user.getId(), "server", user));
			
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

	public void startP2PConnection(P2PInfo info) {
		try{
			Socket peerSocket = new Socket(info.getIp(), info.getPort());
			ObjectOutputStream outPS = new ObjectOutputStream(peerSocket.getOutputStream());
			
			Thread peerListener = new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						outPS.writeObject(new File("data" + File.separator + user.getId() + File.separator + info.getFileName()));
						outPS.close();
						peerSocket.close();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			peerListener.start();
		}
		catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}
	
	public void requestUsers() {
		try{
			outSS.writeObject(new GetUsersMessage(user.getId(), "server"));
		}
		catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}

	public void requestFile(String name){
		try{
			outSS.writeObject(new GetFileMessage(user.getId(), "server", name));
		}
		catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}

	public void searchFile(String file, String requester){
		try{
			if(fileIsPresent(file)){
				outSS.writeObject(new FileFoundMessage(user.getId(), requester, file));
			}
			else{
				outSS.writeObject(new FileNotFoundMessage(user.getId(), requester, file));
			}
		}catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}

	private boolean fileIsPresent(String file) {
		File f = new File("data" + File.separator + user.getId() + File.separator + file);
		return f.exists();
	}
	
	public void sendSocketData(String peer, int port, String file) {
		try{
			outSS.writeObject(new StartConnectionMessage(user.getId(), peer, new P2PInfo(getIp(), port, file)));
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
				outSS.writeObject(new LogoffMessage(user.getId(), "server", user));
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
	
	public void onPeerFound(String peer, String file) {
		try{
			Thread t = new Thread(new FileHandler(this, peer, file));
			t.start();
		}catch(Exception e){
			for(ClientObserver o : observers){
				o.onError(e.getMessage());
			}
		}
	}
	
	public void onFileNotFound(String file) {
		JOptionPane.showMessageDialog(null, file + " no esta en el sistema");
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
	public String getName(){ return user.getId(); }

	private InetAddress getIp() throws UnknownHostException {
		return InetAddress.getLocalHost();
	}

	@Override
	public void addObserver(ClientObserver o) {observers.add(o);}
	
	@Override
	public void removeObserver(ClientObserver o) {observers.remove(o);}
}

package server;

import java.util.HashMap;
import java.util.Map;

import client.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final String HOST = "localhost";
	public static final int PORT = 8800;
	
	private Map<String, User> users;
	private ServerSocket serverSocket;

	public Server() {
		users = new HashMap<String, User>();
		System.out.println("Server started");
		try {
			serverSocket = new ServerSocket(PORT);
			while(true){
				try{
					Socket cliSocket = serverSocket.accept();
					Thread t = new Thread(new ClientHandler(cliSocket, this));
					t.start();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean addUser(User u){
		if(users.containsKey(u.getId()))
			return false;

		users.put(u.getId(), u);
		return true;
	}
	
	public synchronized void removeUser(User u) {
		users.remove(u.getId());
	}
	
	public synchronized void sendUsersInfo(ObjectOutputStream out) throws IOException {
		out.writeInt(users.size());
		for(String s : users.keySet()) {
			out.writeObject(s);
		}
	}
	
	public synchronized void printUsersInfo() {
		if(users.size() == 0) {
			System.out.print("No user connected\n\n");
			return;
		}
		System.out.print("--------------------------\n");
		for(String id : users.keySet()) {
			System.out.print("ID: "  + users.get(id).getId() + "\n");
			System.out.print("IP: "  + users.get(id).getIp() + "\n");
			System.out.print("Archivos:\n");
			for(String s : users.get(id).getSharedInfo()) {
				System.out.print("\t" + s + "\n");
			}
			System.out.print("--------------------------\n");
		}
		System.out.print("\n");
	}
	
	
}

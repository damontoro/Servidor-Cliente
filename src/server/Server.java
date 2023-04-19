package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import client.User;
import message.UsersConnectedMessage;

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
				Socket cliSocket = serverSocket.accept();
				new Thread(new ClientHandler(cliSocket, this)).start();
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
	
	public synchronized void sendUsersInfo(ObjectOutputStream outStream) throws IOException {
		Set<String> ids = new HashSet<String>();
		for(String id : users.keySet()) {
			ids.add(id);
		}
		outStream.writeObject(new UsersConnectedMessage(ids));
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

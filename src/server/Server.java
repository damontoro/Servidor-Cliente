package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;

import client.User;
import utils.Pair;
import message.UsersConnectedMessage;
import message.RequestFileMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final String HOST = "localhost";
	public static final int PORT = 8800;
	
	private Map<String, User> users;
	private Map<String, PriorityQueue<Pair<String, Integer>>> files;
	private Map<String, ObjectOutputStream> userStreams;
	private ServerSocket serverSocket;

	public Server() {
		users = new HashMap<String, User>();
		files = new HashMap<String, PriorityQueue<Pair<String, Integer>>>();
		userStreams = new HashMap<String, ObjectOutputStream>();
		try {
			System.out.println("Server started in port " + PORT + " and ip " + InetAddress.getLocalHost() + "\n");
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

	public synchronized boolean addUser(User u, ObjectOutputStream outStream){
		if(users.containsKey(u.getId()))
			return false;

		userStreams.put(u.getId(), outStream);
		users.put(u.getId(), u);
		for(String file : u.getSharedInfo()){
			if(!files.containsKey(file)){
				files.put(file, new PriorityQueue<Pair<String, Integer>>((a, b) -> a.getSecond() - b.getSecond()));
			}
			files.get(file).add(new Pair<String, Integer>(u.getId(), 0));
		}
		return true;
	}
	
	public synchronized void removeUser(User u) {
		users.remove(u.getId());
	}

	public synchronized String findUserWithFile(String name){
		if(!files.containsKey(name))
			return null;

		Pair<String, Integer> user = files.get(name).poll();
		files.get(name).add(new Pair<String, Integer>(user.getFirst(), user.getSecond() + 1));
		return user.getFirst();
	}

	public void requestFile(String file, String UserID){
		ObjectOutputStream outStream = userStreams.get(UserID);
		try {
			outStream.writeObject(new RequestFileMessage(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
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

package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import client.User;
import message.FileFoundMessage;
import message.FileNotFoundMessage;
import message.P2PInfo;
import message.RequestFileMessage;
import message.RetrieveFileListMessage;
import message.StartConnectionMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final String HOST = "localhost";
	public static final int PORT = 8800;

	private Map<String, ObjectOutputStream> userStreams;
	private ServerSocket serverSocket;
	
	private UsersInfo usersInfo;

	public Server() {
		usersInfo = new UsersInfo();
		userStreams = new ConcurrentHashMap<String, ObjectOutputStream>();
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
	
	public boolean addUser(User u, ObjectOutputStream outStream) throws InterruptedException {
		boolean added = usersInfo.addUser(u);
		if(added)
			userStreams.put(u.getId(), outStream);
		return added;
	}
	
	public void removeUser(User u) throws InterruptedException {
		usersInfo.removeUser(u);
	}

	public void addFile(String user, String file) throws InterruptedException {
		usersInfo.addFile(user, file);
	}

	public void sendFileList(String origin) throws Exception {
		ObjectOutputStream outStream = userStreams.get(origin);
		usersInfo.sendFileList(origin, outStream);
	}
	
	public void sendP2PInfo(String origin, String destination, P2PInfo info) throws IOException {
		ObjectOutputStream outStream = userStreams.get(destination);
		outStream.writeObject(new StartConnectionMessage(origin, destination, info));
	}

	public void sendUsersInfo(ObjectOutputStream out, String destination) throws Exception {
		usersInfo.sendUsersInfo(out, destination);
	}
	
	public String findUserWithFile(String file) throws InterruptedException {
		return usersInfo.findUserWithFile(file);
	}

	public void requestFile(String origin, String destination, String file) throws IOException {
		ObjectOutputStream outStream = userStreams.get(destination);
		outStream.writeObject(new RequestFileMessage(origin, destination, file));
	}
	
	public void fileFound(String origin, String destination, String file) throws IOException {
		ObjectOutputStream outStream = userStreams.get(destination);
		outStream.writeObject(new FileFoundMessage(origin, destination, file));
	}

	public String manageMissingFile(String origin, String destination, String file) throws Exception {
		usersInfo.removeFile(file, origin);
		return findUserWithFile(file);
	}
	
	public void fileNotFound(String origin, String destination, String file) throws IOException{
		ObjectOutputStream outStream = userStreams.get(destination);
		outStream.writeObject(new FileNotFoundMessage(origin, destination, file));
	}
}

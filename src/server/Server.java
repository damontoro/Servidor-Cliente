package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import client.User;
import message.FileFoundMessage;
import message.FileNotFoundMessage;
import message.P2PInfo;
import message.RequestFileMessage;
import message.StartConnectionMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final String HOST = "localhost";
	public static final int PORT = 8800;
	private ServerSocket serverSocket;

	private Map<String, ObjectOutputStream> usersStream;
	private int readCount;
	private Semaphore streamsAccess;
	private Semaphore readCountAccess;
	private Semaphore serviceQueue;
	
	private UsersInfo usersInfo;

	public Server() {
		usersInfo = new UsersInfo();
		
		usersStream = new HashMap<String, ObjectOutputStream>();
		readCount = 0;
		streamsAccess = new Semaphore(1);
		readCountAccess = new Semaphore(1);
		//Como queremos que simule una cola FIFO, ponemos el fairness a true
		serviceQueue = new Semaphore(1, true);
		
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
			addStream(u.getId(), outStream);
		return added;
	}
	
	public void removeUser(User u) throws InterruptedException {
		usersInfo.removeUser(u);
		removeStream(u.getId());
	}

	public void addFile(String user, String file) throws InterruptedException {
		usersInfo.addFile(user, file);
		usersInfo.printUsersInfo();
	}

	public void sendFileList(String origin) throws Exception {
		ObjectOutputStream outStream = getStream(origin);
		usersInfo.sendFileList(origin, outStream);
	}
	
	public void sendP2PInfo(String origin, String destination, P2PInfo info) throws Exception {
		ObjectOutputStream outStream = getStream(destination);
		outStream.writeObject(new StartConnectionMessage(origin, destination, info));
	}

	public void sendUsersInfo(ObjectOutputStream out, String destination) throws Exception {
		usersInfo.sendUsersInfo(out, destination);
	}
	
	public String findUserWithFile(String file) throws InterruptedException {
		return usersInfo.findUserWithFile(file);
	}

	public void requestFile(String origin, String destination, String file) throws Exception {
		ObjectOutputStream outStream = getStream(destination);
		outStream.writeObject(new RequestFileMessage(origin, destination, file));
	}
	
	public void fileFound(String origin, String destination, String file) throws Exception {
		ObjectOutputStream outStream = getStream(destination);
		outStream.writeObject(new FileFoundMessage(origin, destination, file));
	}

	public String manageMissingFile(String origin, String destination, String file) throws Exception {
		usersInfo.removeFile(file, origin);
		return findUserWithFile(file);
	}
	
	public void fileNotFound(String origin, String destination, String file) throws Exception{
		ObjectOutputStream outStream = getStream(destination);
		outStream.writeObject(new FileNotFoundMessage(origin, destination, file));
	}
	
	private void addStream(String id, ObjectOutputStream outStream) throws InterruptedException {
		serviceQueue.acquire();
		
		streamsAccess.acquire();
		
		serviceQueue.release();
		
		usersStream.put(id, outStream);
		
		streamsAccess.release();
	}
	
	private void removeStream(String id) throws InterruptedException {
		serviceQueue.acquire();
		
		streamsAccess.acquire();
		
		serviceQueue.release();
		
		usersStream.remove(id);
		
		streamsAccess.release();
	}
	
	private ObjectOutputStream getStream(String id) throws InterruptedException {
		serviceQueue.acquire();
		
		readCountAccess.acquire();
		
		readCount++;
		if(readCount == 1)
			streamsAccess.acquire();
		
		serviceQueue.release();
		
		readCountAccess.release();
		
		ObjectOutputStream outStream = usersStream.get(id);
		
		readCountAccess.acquire();
		
		readCount--;
		if(readCount == 0)
			streamsAccess.release();
		
		readCountAccess.release();
		
		return outStream;
	}
}

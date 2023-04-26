package server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.User;
import message.RetrieveFileListMessage;
import message.UsersConnectedMessage;

public class UsersInfo {
	private Map<String, User> users;
	private boolean usingUsersMap;
	private Lock usersMapLock;
	private Condition waitForUsersMap ;
	
	private Map<String, PriorityQueue<UserPriority>> files;
	private boolean usingFilesMap;
	private Lock filesMapLock;
	private Condition waitForFilesMap;
	
	public UsersInfo() {
		users = new HashMap<String, User>();
		usingUsersMap = false;
		usersMapLock = new ReentrantLock();
		waitForUsersMap = usersMapLock.newCondition();
		
		files = new HashMap<String, PriorityQueue<UserPriority>>();
		usingFilesMap = false;
		filesMapLock = new ReentrantLock();
		waitForFilesMap = filesMapLock.newCondition();
	}
	
	private void tryAccesingUsersMap() throws InterruptedException{
		usersMapLock.lock();
		try {
			while(usingUsersMap)
				waitForUsersMap.await();
			usingUsersMap = true;
		}
		finally {
			usersMapLock.unlock();
		}
	}
	
	private void freeAccessUsersMap() throws InterruptedException{
		usersMapLock.lock();
		try {
			usingUsersMap = false;
			waitForUsersMap.signal();
		}
		finally {
			usersMapLock.unlock();
		}
	}
	
	private void tryAccesingFilesMap() throws InterruptedException{
		filesMapLock.lock();
		try {
			while(usingFilesMap)
				waitForFilesMap.await();
			usingFilesMap = true;
		}
		finally {
			filesMapLock.unlock();
		}
	}
	
	private void freeAccessFilesMap() throws InterruptedException{
		filesMapLock.lock();
		try {
			usingFilesMap = false;
			waitForFilesMap.signal();
		}
		finally {
			filesMapLock.unlock();
		}
	}
	
	public boolean addUser(User u) throws InterruptedException{
		tryAccesingUsersMap();
		
		if(users.containsKey(u.getId())) {
			freeAccessUsersMap();
			return false;
		}

		users.put(u.getId(), u);
		
		tryAccesingFilesMap();
		
		for(String file : u.getSharedInfo()){
			if(!files.containsKey(file)){
				files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
			}
			files.get(file).add(new UserPriority(u.getId(), 0));
		}
		
		freeAccessFilesMap();
		freeAccessUsersMap();
		
		return true;
	}
	
	public void removeUser(User u) throws InterruptedException {
		tryAccesingUsersMap();
		
		users.remove(u.getId());
		
		freeAccessUsersMap();
	}

	public void sendFileList(String destination, ObjectOutputStream outStream) throws Exception{
		
		List<String> fileList = new ArrayList<String>();
		
		tryAccesingFilesMap();
		
		for(String file : files.keySet()) {
			if(!files.get(file).isEmpty())
				fileList.add(file);
		}
		
		freeAccessFilesMap();
		
		outStream.writeObject(new RetrieveFileListMessage("server", destination, fileList));
	}

	public void addFile(String user, String file) throws InterruptedException {
		tryAccesingUsersMap();
		
		if(!users.containsKey(user)) {
			freeAccessUsersMap();
			return;
		}
		users.get(user).addFile(file);
		
		freeAccessUsersMap();
		tryAccesingFilesMap();
		
		if(!files.containsKey(file)){
			files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
		}
		files.get(file).add(new UserPriority(user, 0));
		
		freeAccessFilesMap();
	}

	public void removeFile(String file, String user) throws InterruptedException {
		tryAccesingUsersMap();
		
		if(!users.containsKey(user)) {
			freeAccessUsersMap();
			return;
		}
		users.get(user).removeFile(file);
		
		freeAccessUsersMap();
		tryAccesingFilesMap();
		
		files.get(file).remove(new UserPriority(user, 0));
		
		freeAccessFilesMap();
	}
	
	public String findUserWithFile(String name) throws InterruptedException{
		tryAccesingFilesMap();
		
		if(!files.containsKey(name)) {
			freeAccessFilesMap();
			return null;
		}
		
		UserPriority user;

		tryAccesingUsersMap();
		
		//Lo hacemos así para comprobar que el supuesto usuario que tiene el archivo está conectado,
		//No lo quitamos de la cola en el log off para que el coste de ese metodo sea menor
		do{
			user = files.get(name).poll();
		}while(user != null && !users.containsKey(user.getId()));
		
		freeAccessUsersMap();

		//Comprobamos si el usuario devuelto está conectado (en el caso de que el ultimo de la cola no lo esté)
		if(user == null){
			freeAccessFilesMap();
			return null;
		}

		files.get(name).add(new UserPriority(user.getId(), user.getPriority() + 1));
		
		freeAccessFilesMap();
		//Comprobamos si el usuario es correcto
		return user.getId() ;
	}
	
	public void sendUsersInfo(ObjectOutputStream outStream, String destination) throws Exception {
		tryAccesingUsersMap();
		
		Set<String> ids = new HashSet<String>();
		for(String id : users.keySet()) {
			ids.add(id);
		}
		outStream.writeObject(new UsersConnectedMessage("server", destination, ids));
		
		freeAccessUsersMap();
	}
	
	public void printUsersInfo() throws InterruptedException {
		tryAccesingUsersMap();
		
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
		
		freeAccessUsersMap();
	}

	private class UserPriority{
		private String id;
		private int priority;
		
		public UserPriority(String id, int priority) {
			this.id = id;
			this.priority = priority;
		}
		
		public String getId() {
			return id;
		}
		
		public int getPriority() {
			return priority;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof UserPriority) {
				return ((UserPriority) obj).getId().equals(id);
			}
			return false;
		}
	}
}

package server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import client.User;
import message.RetrieveFileListMessage;
import message.UsersConnectedMessage;

public class UsersInfo {
	private Map<String, User> users;
	private Map<String, PriorityQueue<UserPriority>> files;
	
	public UsersInfo() {
		users = new HashMap<String, User>();
	}
	
	public synchronized boolean addUser(User u) throws InterruptedException{
		if(users.containsKey(u.getId())) {
			return false;
		}

		users.put(u.getId(), u);
		
		for(String file : u.getSharedInfo()){
			if(!files.containsKey(file)){
				files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
			}
			files.get(file).add(new UserPriority(u.getId(), 0));
		}
		
		return true;
	}
	
	public synchronized void removeUser(User u) throws InterruptedException  {
		users.remove(u.getId());
	}

	public void sendFileList(String destination, ObjectOutputStream outStream) throws Exception{
		List<String> fileList = getFileList();

		outStream.writeObject(new RetrieveFileListMessage("server", destination, fileList));
	}
	
	private synchronized List<String> getFileList() {
		List<String> fileList = new ArrayList<String>();
		for(String file : files.keySet()) {
			if(!files.get(file).isEmpty())
				fileList.add(file);
		}
		return fileList;
	}
	

	public synchronized void addFile(String user, String file) throws InterruptedException {
		if(!users.containsKey(user)) {
			return;
		}
		users.get(user).addFile(file);
		
		if(!files.containsKey(file)){
			files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
		}
		files.get(file).add(new UserPriority(user, 0));
	}

	public synchronized void removeFile(String file, String user) throws InterruptedException {
		if(!users.containsKey(user)) {
			return;
		}
		users.get(user).removeFile(file);
		
		files.get(file).remove(new UserPriority(user, 0));
	}
	
	public synchronized String findUserWithFile(String name) throws InterruptedException{
		if(!files.containsKey(name)) {
			return null;
		}
		
		UserPriority user;

		//Lo hacemos así para comprobar que el supuesto usuario que tiene el archivo está conectado,
		//No lo quitamos de la cola en el log off para que el coste de ese metodo sea menor
		do{
			user = files.get(name).poll();
		}while(user != null && !users.containsKey(user.getId()));

		//Comprobamos si el usuario devuelto está conectado (en el caso de que el ultimo de la cola no lo esté)
		if(user == null){
			return null;
		}

		files.get(name).add(new UserPriority(user.getId(), user.getPriority() + 1));
		
		//Comprobamos si el usuario es correcto
		return user.getId() ;
	}
	
	public void sendUsersInfo(ObjectOutputStream outStream, String destination) throws Exception {
		Set<String> ids = getUsersInfo();
		outStream.writeObject(new UsersConnectedMessage("server", destination, ids));
	}
	
	private synchronized Set<String> getUsersInfo(){
		Set<String> ids = new HashSet<String>();
		for(String id : users.keySet()) {
			ids.add(id);
		}
		return ids;
	}
	
	public synchronized void printUsersInfo() throws InterruptedException {
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

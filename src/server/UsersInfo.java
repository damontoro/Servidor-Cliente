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
	private Semaphore semUsers, semFiles;
	
	public UsersInfo() {
		users = new HashMap<String, User>();
		files = new HashMap<String, PriorityQueue<UserPriority>>();
		semUsers = new Semaphore(1);
		semFiles = new Semaphore(1);
	}
	
	public boolean addUser(User u) throws InterruptedException{
		semUsers.acquire();
		
		if(users.containsKey(u.getId())) {
			semUsers.release();
			return false;
		}

		users.put(u.getId(), u);
		
		semFiles.acquire();
		
		for(String file : u.getSharedInfo()){
			if(!files.containsKey(file)){
				files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
			}
			files.get(file).add(new UserPriority(u.getId(), 0));
		}
		
		semFiles.release();
		semUsers.release();
		
		return true;
	}
	
	public void removeUser(User u) throws InterruptedException  {
		semUsers.acquire();
		
		users.remove(u.getId());
		
		semUsers.release();
	}

	public void sendFileList(String destination, ObjectOutputStream outStream) throws Exception{
		semFiles.acquire();
		List<String> fileList = new ArrayList<String>();
		for(String file : files.keySet()) {
			if(!files.get(file).isEmpty())
				fileList.add(file);
		}

		outStream.writeObject(new RetrieveFileListMessage("server", destination, fileList));

		semFiles.release();
	}

	public void addFile(String user, String file) throws InterruptedException {
		semUsers.acquire();
		
		if(!users.containsKey(user)) {
			semUsers.release();
			return;
		}
		users.get(user).addFile(file);
		
		semFiles.acquire();
		
		if(!files.containsKey(file)){
			files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
		}
		files.get(file).add(new UserPriority(user, 0));
		
		semUsers.release();
		semFiles.release();
	}

	public void removeFile(String file, String user) throws InterruptedException {
		semUsers.acquire();
		
		if(!users.containsKey(user)) {
			semUsers.release();
			return;
		}
		users.get(user).removeFile(file);
		
		semUsers.release();
		semFiles.acquire();
		
		files.get(file).remove(new UserPriority(user, 0));
		
		semFiles.release();
	}
	
	public String findUserWithFile(String name) throws InterruptedException{
		semFiles.acquire();
		
		if(!files.containsKey(name)) {
			semFiles.release();
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
			semFiles.release();
			return null;
		}

		files.get(name).add(new UserPriority(user.getId(), user.getPriority() + 1));
		
		semFiles.release();
		
		//Comprobamos si el usuario es correcto
		return user.getId() ;
	}
	
	public void sendUsersInfo(ObjectOutputStream outStream, String destination) throws Exception {
		semUsers.acquire();
		
		Set<String> ids = new HashSet<String>();
		for(String id : users.keySet()) {
			ids.add(id);
		}
		outStream.writeObject(new UsersConnectedMessage("server", destination, ids));
		
		semUsers.release();
	}
	
	public void printUsersInfo() throws InterruptedException {
		semUsers.acquire();
		
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
		
		semUsers.release();
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

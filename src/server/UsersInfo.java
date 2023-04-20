package server;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import client.User;
import message.UsersConnectedMessage;
import utils.Pair;

public class UsersInfo {
	private Map<String, User> users;
	private Map<String, PriorityQueue<UserPriority>> files;
	private Semaphore mapaUsuarios, mapaFicheros;
	
	public UsersInfo() {
		users = new HashMap<String, User>();
		files = new HashMap<String, PriorityQueue<UserPriority>>();
		mapaUsuarios = new Semaphore(1);
		mapaFicheros = new Semaphore(1);
	}
	
	public boolean addUser(User u) throws InterruptedException{
		mapaUsuarios.acquire();
		
		if(users.containsKey(u.getId())) {
			mapaUsuarios.release();
			return false;
		}

		users.put(u.getId(), u);
		mapaUsuarios.release();
		mapaFicheros.acquire();
		for(String file : u.getSharedInfo()){
			if(!files.containsKey(file)){
				files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
			}
			files.get(file).add(new UserPriority(u.getId(), 0));
		}
		
		mapaFicheros.release();
		
		return true;
	}
	
	public void removeUser(User u) throws InterruptedException  {
		mapaUsuarios.acquire();
		
		users.remove(u.getId());
		
		mapaUsuarios.release();
	}

	public void removeFile(String file, String user) throws InterruptedException {
		mapaUsuarios.acquire();
		if(!users.containsKey(user)) {
			mapaUsuarios.release();
			return;
		}
		users.get(user).removeFile(file);
		mapaUsuarios.release();

		mapaFicheros.acquire();
		files.get(file).remove(new UserPriority(user, 0));
		mapaFicheros.release();
	}
	
	public String findUserWithFile(String name) throws InterruptedException{
		mapaFicheros.acquire();
		
		if(!files.containsKey(name)) {
			mapaFicheros.release();
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
			mapaFicheros.release();
			return null;
		}

		files.get(name).add(new UserPriority(user.getId(), user.getPriority() + 1));
		mapaFicheros.release();
		
		//Comprobamos si el usuario es correcto
		return user.getId() ;
	}
	
	public synchronized void sendUsersInfo(ObjectOutputStream outStream, String destination) throws Exception {
		mapaUsuarios.acquire();
		
		Set<String> ids = new HashSet<String>();
		for(String id : users.keySet()) {
			ids.add(id);
		}
		outStream.writeObject(new UsersConnectedMessage("server", destination, ids));
		
		mapaUsuarios.release();
	}
	
	public synchronized void printUsersInfo() throws InterruptedException {
		mapaUsuarios.acquire();
		
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
		
		mapaUsuarios.release();
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

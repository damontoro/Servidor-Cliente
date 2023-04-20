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
	private Map<String, PriorityQueue<Pair<String, Integer>>> files;
	private Semaphore mapaUsuarios, mapaFicheros;
	
	public UsersInfo() {
		users = new HashMap<String, User>();
		files = new HashMap<String, PriorityQueue<Pair<String, Integer>>>();
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
				files.put(file, new PriorityQueue<Pair<String, Integer>>((a, b) -> a.getSecond() - b.getSecond()));
			}
			files.get(file).add(new Pair<String, Integer>(u.getId(), 0));
		}
		
		mapaFicheros.release();
		
		return true;
	}
	
	public void removeUser(User u) throws InterruptedException  {
		mapaUsuarios.acquire();
		
		users.remove(u.getId());
		
		mapaUsuarios.release();
	}
	
	public String findUserWithFile(String name) throws InterruptedException{
		mapaFicheros.acquire();
		
		if(!files.containsKey(name)) {
			mapaFicheros.release();
			return null;
		}
		
		Pair<String, Integer> user = files.get(name).poll();
		files.get(name).add(new Pair<String, Integer>(user.getFirst(), user.getSecond() + 1));
		
		mapaFicheros.release();
		
		return user.getFirst();
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
}

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

//Para implementar el monitor hemos usado la clase Lock de Java para poder usar
//la clase Condition.
public class UsersInfo {
	private ReadersWritersMap<String, User> users;	
	private ReadersWritersMap<String, PriorityQueue<UserPriority>> files;
	
	public UsersInfo() {
		users = new ReadersWritersMap<String, User>();
		
		files = new ReadersWritersMap<String, PriorityQueue<UserPriority>>();
	}
	
	public boolean addUser(User u) throws InterruptedException{
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
	
	public void removeUser(User u) throws InterruptedException {
		users.remove(u.getId());
	}

	public void sendFileList(String destination, ObjectOutputStream outStream) throws Exception{
		List<String> fileList = new ArrayList<String>();
		
		for(String file : files.keySet()) {
			if(!files.get(file).isEmpty())
				fileList.add(file);
		}
		
		outStream.writeObject(new RetrieveFileListMessage("server", destination, fileList));
	}

	public void addFile(String user, String file) throws InterruptedException {
		if(!users.containsKey(user)) {
			return;
		}
		users.get(user).addFile(file);
		
		if(!files.containsKey(file)){
			files.put(file, new PriorityQueue<UserPriority>((a, b) -> a.getPriority() - b.getPriority()));
		}
		files.get(file).add(new UserPriority(user, 0));
	}

	public void removeFile(String file, String user) throws InterruptedException {
		if(!users.containsKey(user)) {
			return;
		}
		users.get(user).removeFile(file);
		
		files.get(file).remove(new UserPriority(user, 0));
	}
	
	public String findUserWithFile(String name) throws InterruptedException{
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
		Set<String> ids = new HashSet<String>();
		for(String id : users.keySet()) {
			ids.add(id);
		}
		outStream.writeObject(new UsersConnectedMessage("server", destination, ids));
	}
	
	public void printUsersInfo() throws InterruptedException {		
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
	
	private class ReadersWritersMap<T1, T2> {
		private Map<T1, T2> data;
		
		private boolean getOutOfQueue;
		private int activeReaders;
		private boolean writerActive;
		private Lock lock;
		private Condition waitingInQueue;
		private Condition readingAllowed;
		private Condition writingAllowed;
		 
		public ReadersWritersMap() {
			data = new HashMap<T1, T2>();
			
			getOutOfQueue = true;
			activeReaders = 0;
			writerActive = false;
			lock = new ReentrantLock();
			waitingInQueue = lock.newCondition();
		    readingAllowed = lock.newCondition();
		    writingAllowed = lock.newCondition();
		}
		
		private void enterQueue() throws InterruptedException {
	        lock.lock();
	        try {
	            while (!getOutOfQueue) {
	                waitingInQueue.await();
	            }
	            getOutOfQueue = false;
	        } finally {
	            lock.unlock();
	        }
		}
		
		private void notifyQueue() throws InterruptedException {
	        lock.lock();
	        try {
	            getOutOfQueue = true;
	            waitingInQueue.signal();
	        } finally {
	            lock.unlock();
	        }
		}
		
	    private void startReading() throws InterruptedException {
	        lock.lock();
	        try {
	            while (writerActive) {
	                readingAllowed.await();
	            }
	            activeReaders++;
	        } finally {
	            lock.unlock();
	        }
	    }

	    private void finishReading() {
	        lock.lock();
	        try {
	        	activeReaders--;
	            if (activeReaders == 0) {
	                writingAllowed.signal();
	            }
	        } finally {
	            lock.unlock();
	        }
	    }

	    private void startWriting() throws InterruptedException {
	        lock.lock();
	        try {
	            while (writerActive || activeReaders > 0) {
	                writingAllowed.await();
	            }
	            writerActive = true;
	        } finally {
	            lock.unlock();
	        }
	    }

	    private void finishWriting() {
	        lock.lock();
	        try {
	            writerActive = false;
	            readingAllowed.signal();
	            writingAllowed.signal();
	        } finally {
	            lock.unlock();
	        }
	    }
	    
	    public T2 get(T1 key) throws InterruptedException {
	    	enterQueue();
	    	
	    	startReading();
	    	
	    	notifyQueue();
	    	
	    	T2 value = data.get(key);
	    	
	    	finishReading();
	    	
	    	return value;
	    }
	    
	    public int size() throws InterruptedException {
	    	enterQueue();
	    	
	    	startReading();
	    	
	    	notifyQueue();
	    	
	    	int size = data.size();
	    	
	    	finishReading();
	    	
	    	return size;
	    }
	    
	    public boolean containsKey (T1 key) throws InterruptedException{
	    	enterQueue();
	    	
	    	startReading();
	    	
	    	notifyQueue();
	    	
	    	boolean contained = data.containsKey(key);
	    	
	    	finishReading();
	    	
	    	return contained;
	    }
	    
	    public Set<T1> keySet() throws InterruptedException {
	    	enterQueue();
	    	
	    	startReading();
	    	
	    	notifyQueue();
	    	
	    	Set<T1> keySet = data.keySet();
	    	
	    	finishReading();
	    	
	    	return keySet;
	    }
	    
	    public void put(T1 key, T2 value) throws InterruptedException {
	    	enterQueue();
	    	
	    	startWriting();
	    	
	    	notifyQueue();
	    	
	    	data.put(key, value);
	    	
	    	finishWriting();
	    }
	    
	    public void remove(T1 key) throws InterruptedException {
	    	enterQueue();
	    	
	    	startWriting();
	    	
	    	notifyQueue();
	    	
	    	data.remove(key);
	    	
	    	finishWriting();
	    }
	}
}

package server;

import java.util.HashSet;
import java.util.Set;
import client.User;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	Set<User> users;
	ServerSocket serverSocket;

	public Server(int port) {
		users = new HashSet<User>();
		System.out.println("Server started");
		try {
			serverSocket = new ServerSocket(port);
			while(true){
				try{
					Socket cliSocket = serverSocket.accept();

					Thread t = new Thread(new ServerListener(cliSocket, this));
					t.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void addUser(User u){
		System.out.println("User added");
		users.add(u);
		showUser();
	}

	public synchronized void showUser(){
		for(var u : users){
			System.out.println(u);
		}
	}
}

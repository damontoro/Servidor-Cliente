package controller;

import client.Client;
import client.ClientObserver;

public class Controller {
	Client client;
	
	public Controller (Client client){
		this.client = client;
	}
	
	public void initClient(String id) {
		client.setName(id);
		client.loadSharedInfo();
	}
	
	public void connect() {
		client.connect();
	}
	
	public void requestUsers() {
		new Thread(new Runnable() {
			Client c;
			@Override
			public void run() {
				c.requestUsers();
			}
			
			Runnable init(Client c) {
				this.c = c;
				return this;
			}
			
		}.init(client)).start();
	}
	
	public void disconnect() {
		client.disconnect();
	}
	
	public void addObserver(ClientObserver co) {
		client.addObserver(co);
	}
	
	public void removeObserver(ClientObserver co) {
		client.removeObserver(co);
	}
}

package controller;

import client.Client;
import client.ClientObserver;

public class Controller {
	Client client;
	
	public Controller (Client client){
		this.client = client;
	}
	
	public void init(String id) {
		client.setName(id);
		client.loadSharedInfo();
	}
	
	public void connect() {
		client.connect();
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

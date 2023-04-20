package message;

import java.io.Serializable;

public abstract class Message<T> implements Serializable{
	private static final long serialVersionUID = -6843140754460356425L;
	
	private String type, sender, receiver;
	
	Message(String type){
		this.type = type;
	}

	public abstract T retrieveInfo();

	public String getType() { return type; }
}

package message;

import java.io.Serializable;

public abstract class Message<T> implements Serializable{
	private static final long serialVersionUID = -6843140754460356425L;
	
	private String origin;
	private String destination;
	private String type;
	
	Message(String origin, String destination, String type){
		this.origin = origin;
		this.destination = destination;
		this.type = type;
	}

	public abstract T retrieveInfo();

	public String getOrigin() { return origin; }
	public String getDestination() { return destination; }
	public String getType() { return type; }
}

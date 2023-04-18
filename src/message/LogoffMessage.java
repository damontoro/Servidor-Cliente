package message;

import client.User;

public class LogoffMessage extends Message<User>{
	private static final long serialVersionUID = 1010242978520930473L;
	
	public static final String TYPE = "Logoff";
	
	private User user;

	public LogoffMessage(User user) {
		super(TYPE);
		this.user = user;
	}

	@Override
	public User retrieveInfo() {
		return user;
	}

}
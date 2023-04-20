package message;

import client.User;

public class LoginMessage extends Message<User>{
	private static final long serialVersionUID = 3045712162392257511L;
	
	public static final String TYPE = "Login";
	
	private User user;

	public LoginMessage(String origin, String destination, User user) {
		super(origin, destination, TYPE);
		this.user = user;
	}

	@Override
	public User retrieveInfo() {
		return user;
	}

}

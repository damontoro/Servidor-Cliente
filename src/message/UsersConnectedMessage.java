package message;

import java.util.Set;

import client.User;

public class UsersConnectedMessage extends Message<Set<String>>{
	private static final long serialVersionUID = -2559063326443168543L;

	public static final String TYPE = "UsersSet";
	
	private Set<String> users;

	public UsersConnectedMessage(Set<String> users) {
		super(TYPE);
		this.users = users;
	}

	@Override
	public Set<String> retrieveInfo() {
		return users;
	}
}

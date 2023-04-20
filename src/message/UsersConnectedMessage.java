package message;

import java.util.Set;

public class UsersConnectedMessage extends Message<Set<String>>{
	private static final long serialVersionUID = -2559063326443168543L;

	public static final String TYPE = "UsersSet";
	
	private Set<String> users;

	public UsersConnectedMessage(String origin, String destination, Set<String> users) {
		super(origin, destination, TYPE);
		this.users = users;
	}

	@Override
	public Set<String> retrieveInfo() {
		return users;
	}
}

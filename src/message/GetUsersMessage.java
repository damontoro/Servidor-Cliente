package message;

public class GetUsersMessage extends Message<Void>{
	private static final long serialVersionUID = -7443331911534080747L;
	
	public static final String TYPE = "GetUsers";
	
	public GetUsersMessage(String origin, String destination) {
		super(origin, destination, TYPE);
	}

	@Override
	public Void retrieveInfo() {
		return null;
	}

}

package message;

public class DisconnectedMessage extends Message<Void>{
	private static final long serialVersionUID = 7151497474886201817L;
	
	public static final String TYPE = "Disconnected";
	
	public DisconnectedMessage () {
		super(TYPE);
	}

	@Override
	public Void retrieveInfo() {
		return null;
	}

}

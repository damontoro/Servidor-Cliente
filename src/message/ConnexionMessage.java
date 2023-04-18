package message;

public class ConnexionMessage extends Message<Boolean> {
	private static final long serialVersionUID = 4723594170028404484L;

	public static final String TYPE = "Connexion";
	
	private boolean connected;
	
	public ConnexionMessage(String origin, String destination, boolean connected) {
		super(origin, destination, TYPE);
		this.connected = connected;
	}

	@Override
	public Boolean retrieveInfo() {
		return connected;
	}
	
}

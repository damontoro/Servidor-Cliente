package message;

public class StartConnectionMessage extends Message<P2PInfo>{
	private static final long serialVersionUID = 9093893035528743011L;
	
	public static final String TYPE = "StartConnection";
	
	private P2PInfo info; 

	public StartConnectionMessage(String origin, String destination, P2PInfo inf) {
		super(origin, destination, TYPE);
		info = inf;
	}

	@Override
	public P2PInfo retrieveInfo() {
		return info;
	}
}

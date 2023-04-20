package message;


public class RequestFileListMessage extends Message<Void>{

	public static final String TYPE = "RequestFileList";

	public RequestFileListMessage(String origin, String destination) {
		super(origin, destination, TYPE);
	}

	@Override
	public Void retrieveInfo() {
		return null;
	}
	
}

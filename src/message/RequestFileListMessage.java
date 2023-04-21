package message;


public class RequestFileListMessage extends Message<Void>{
	private static final long serialVersionUID = 6980148476926829120L;
	
	public static final String TYPE = "RequestFileList";

	public RequestFileListMessage(String origin, String destination) {
		super(origin, destination, TYPE);
	}

	@Override
	public Void retrieveInfo() {
		return null;
	}
}

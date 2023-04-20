package message;

public class RequestFileMessage extends Message<String>{

	private static final String TYPE = "RequestFile";

	private String fileName;

	public RequestFileMessage(String name) {
		super(TYPE);
		fileName = name;
	}

	@Override
	public String retrieveInfo() {
		return fileName;
	}
	
}

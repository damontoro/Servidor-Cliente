package message;

public class RequestFileMessage extends Message<String>{
	private static final long serialVersionUID = -9189707024340126403L;

	public static final String TYPE = "RequestFile";

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

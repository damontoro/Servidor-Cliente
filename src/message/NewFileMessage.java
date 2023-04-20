package message;

public class NewFileMessage extends Message<String>{

	public static final String TYPE = "NewFile";
	private String file;

	public NewFileMessage(String origin, String destination, String file) {
		super(origin, destination, TYPE);
		this.file = file;
	}

	@Override
	public String retrieveInfo() {
		return file;
	}
	
}

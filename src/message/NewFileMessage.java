package message;

public class NewFileMessage extends Message<String>{
	private static final long serialVersionUID = -4006729642929761469L;
	
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

package message;

public class FileNotFoundMessage extends Message<String> {
	private static final long serialVersionUID = -5670701572797149149L;

	public static final String TYPE = "FileNotFound";
	
	private String name;

	public FileNotFoundMessage(String name) {
		super(TYPE);
		this.name = name;
	}

	@Override
	public String retrieveInfo() {
		return name;
	}
}

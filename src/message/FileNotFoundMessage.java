package message;

public class FileNotFoundMessage extends Message<String> {
	private static final long serialVersionUID = -5670701572797149149L;

	public static final String TYPE = "FileNotFound";
	
	private String name;

	public FileNotFoundMessage(String origin, String destination, String name) {
		super(origin, destination, TYPE);
		this.name = name;
	}

	@Override
	public String retrieveInfo() {
		return name;
	}
}

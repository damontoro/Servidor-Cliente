package message;

public class FileFoundMessage extends Message<String> {
	private static final long serialVersionUID = -5670701572797149149L;

	public static final String TYPE = "FileFound";
	
	private String name;

	public FileFoundMessage(String origin, String destination, String name) {
		super(origin, destination, TYPE);
		this.name = name;
	}

	@Override
	public String retrieveInfo() {
		return name;
	}
}

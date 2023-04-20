package message;

public class GetFileMessage extends Message<String>{
	private static final long serialVersionUID = 697417530477171584L;
	
	public static final String TYPE = "GetFile";
	
	private String name;

	public GetFileMessage(String name) {
		super(TYPE);
		this.name = name;
	}

	@Override
	public String retrieveInfo() {
		return name;
	}
}

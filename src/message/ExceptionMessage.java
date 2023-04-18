package message;

public class ExceptionMessage extends Message<Exception>{
	
	public static final String TYPE = "ExceptionMessage";
	private Exception e;
	public ExceptionMessage(String origin, String destination, Exception e) {
		super(origin, destination, TYPE);
		this.e = e;
	}

	@Override
	public Exception retrieveInfo() {
		return e;
	}
}

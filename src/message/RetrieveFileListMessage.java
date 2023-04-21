package message;

import java.util.List;

public class RetrieveFileListMessage extends Message<List<String>>{
	private static final long serialVersionUID = 1171167153087861344L;
	
	public static final String TYPE = "RetrieveFileList";
	
	private List<String> fileList;

	public RetrieveFileListMessage(String origin, String destination, List<String> fileList) {
		super(origin, destination, TYPE);
		this.fileList = fileList;
	}

	@Override
	public List<String> retrieveInfo() {
		return fileList;
	}
}

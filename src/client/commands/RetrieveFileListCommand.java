package client.commands;

import java.util.List;

import client.Client;
import message.Message;
import message.RetrieveFileListMessage;

public class RetrieveFileListCommand extends ClientCommand{
	private List<String> fileList;

	@Override
	public void execute(Client cli) {
		cli.updateFileList(fileList);
	}

	@Override
	protected ClientCommand parse(Message<?> message) {
		if(message.getType().equals(RetrieveFileListMessage.TYPE)) {
			fileList = ((RetrieveFileListMessage) message).retrieveInfo();
			return this;
		}
		return null;
	}
}

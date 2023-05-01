package client;

import java.util.List;
import java.util.Set;

public interface ClientObserver {
	public void onError(String message);
	public void onConnect(String host, int port, String username);
	public void onUsersRequested(Set<String> users);
	public void onDisconnect(String host, int port);
	public void onFilesUpdated(List<String> files);
	public void onMyFilesUpdated();
	public void onPeerFound(String peer, String file);
}

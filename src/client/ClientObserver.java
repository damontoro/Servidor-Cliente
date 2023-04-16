package client;

import java.util.Set;

public interface ClientObserver {
	public void onError(String message);
	public void onConnect(String host, int port);
	public void onUsersRequested(Set<String> users);
	public void onDisconnect(String host, int port);
}

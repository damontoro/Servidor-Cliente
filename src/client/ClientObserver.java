package client;

public interface ClientObserver {
	public void onError(String message);
	public void onConnect(String host, int port);
}

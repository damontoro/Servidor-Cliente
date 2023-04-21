package client.locks;

public interface Lock {
	void takeLock();
	void releaseLock();
}

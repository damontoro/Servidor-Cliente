package client;

public interface ClientObservable<T> {
	void addObserver(T o);
	void removeObserver(T o);
}

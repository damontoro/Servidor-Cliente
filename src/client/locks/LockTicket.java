package client.locks;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicket implements Lock {
	private volatile int next;
	private volatile AtomicInteger number;
	
	public LockTicket(){
		next = 1;
		number = new AtomicInteger(1);
	}
	
	@Override
	public void takeLock() {
		int turn = number.getAndIncrement();
		while(turn != next);
	}

	@Override
	public void releaseLock() {
		next++;
		next = next;
	}
}


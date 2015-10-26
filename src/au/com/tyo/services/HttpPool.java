package au.com.tyo.services;

import java.util.ArrayList;

public class HttpPool {
	private static final int POOL_SIZE = 5;
	private static final int WAIT_FOR_HOW_LONG = 5 * 60 * 1000; // 5 minutes
	
	private ArrayList<Http> pool;
	
	private static HttpPool instance;
	
	private int size;

	public HttpPool() {
		pool = new ArrayList<Http>(1);
		pool.add(new Http());
		size = POOL_SIZE;
	}

	public synchronized static HttpPool getInstance() {
		if (instance == null)
			instance = new HttpPool();
		return instance;
	}
	
	public synchronized Http getConnection() {
		long start = System.nanoTime();
		Http available = null;
		while (available == null) {
			for (Http conn : pool) {
				if (!conn.isInUsed()) {
					available = conn;
					break;
				}
			}
			if (available == null && pool.size() < size) {
				available = new Http();
				pool.add(available);
			}
			if (available == null)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			long end = System.nanoTime();
			long microseconds = (end - start) / 1000;
			if (microseconds > WAIT_FOR_HOW_LONG)
				break;
		}
		
		if (available != null)
			available.setCaller(null);
		return available;
	}
}

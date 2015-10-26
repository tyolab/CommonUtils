package au.com.tyo;

import java.util.Observable;

public class States extends Observable {
	private boolean hasInternet;
	private boolean networkConnected;
	
	public States() {
		hasInternet = false;
	}

	public synchronized boolean hasInternet() {
		return hasInternet;
	}

	public boolean isNetworkConnected() {
		return networkConnected;
	}

	public void setNetworkConnected(boolean networkConnected) {
		this.networkConnected = networkConnected;
	}

//	public synchronized void checkRealNetworkState(boolean networkConntected) {
//		setNetworkConnected(networkConnected);
//		
//		boolean stateChanged = false;
//		if (networkConntected) {
//			boolean test = checkInternetAvailability();
//			if ((stateChanged = test != hasInternet))
//				hasInternet = test;
//		}
//		else {
//			if ((stateChanged = hasInternet != false))
//				hasInternet = false;
//		}
//		
//		if (stateChanged)
//			 informObserver() ;
//	}

	public void informObserver() {
	    setChanged();
	    notifyObservers();
	}

}

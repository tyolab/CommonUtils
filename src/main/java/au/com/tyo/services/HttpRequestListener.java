/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

public interface HttpRequestListener {
	
	public void onProgressChanged(int progress);
	
	public void onResourceAvailable();
	
	public void onFinished();
//	public void setProgress(int progress);

	public void onError();

    boolean isCancelled();
}

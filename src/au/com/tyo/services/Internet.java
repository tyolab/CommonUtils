/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.services;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Internet {
	
	public static boolean checkAvailability() throws Exception {
		boolean responded = false;
		
		String[] testList = {"http://www.google.com", "http://www.bing.com", "http://www.yandex.com/", "http://www.soso.com/"};
		
		for (String url : testList) {
	        HttpGet requestForTest = new HttpGet(url);
	        try {
	            new DefaultHttpClient().execute(requestForTest); // can last...
	            responded = true;
	            break;
	        } 
	        catch (Exception e) 
	        {
	        	throw e;
	        }
		}
        return responded;
	}
}

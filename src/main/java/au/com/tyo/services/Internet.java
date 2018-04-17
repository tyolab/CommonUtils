/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

public class Internet {
	
	public static boolean checkAvailability() throws Exception {
		boolean responded = false;
		
		String[] testList = {"https://www.google.com", "http://www.bing.com", "http://www.yandex.com/", "http://www.soso.com/"};
		
		for (String url : testList) {
	        HttpConnection http = HttpPool.getInstance().getConnection();

	        try {
				http.get(url);
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

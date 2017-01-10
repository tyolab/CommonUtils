/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * Tutorial:
 * HOW-TO: Handling cookies using the java.net.* API

Author: Ian Brown spam@hccp.org

This is a brief overview on how to retrieve cookies from HTTP responses and how to return cookies in HTTP requests to the appropriate server using the java.net.* APIs. 

What are cookies?
Retrieving cookies from a response.
Setting a cookie value in a request.
Setting multiple cookie values in a request.
Sample code.

What are cookies?

Cookies are small strings of data of the form name=value. These are delivered to the client via the header variables in an HTTP response. Upon recieving a cookie from a web server, the client application should store that cookie, returning it to the server in subsequent requests. For greater detail see the Netscape specification: http://wp.netscape.com/newsref/std/cookie_spec.html

Retrieving cookies from a response:

Open a java.net.URLConnection to the server:
URL myUrl = new URL("http://www.hccp.org/cookieTest.jsp");
URLConnection urlConn = myUrl.openConnection();
urlConn.connect();
Loop through response headers looking for cookies:
Since a server may set multiple cookies in a single request, we will need to loop through the response headers, looking for all headers named "Set-Cookie".

String headerName=null;
for (int i=1; (headerName = uc.getHeaderFieldKey(i))!=null; i++) {
 	if (headerName.equals("Set-Cookie")) {                  
	String cookie = urlConn.getHeaderField(i);               
	...                                                      

Extract cookie name and value from cookie string:
The string returned by the getHeaderField(int index) method is a series of name=value separated by semi-colons (;). The first name/value pairing is actual data string we are interested in (i.e. "sessionId=0949eeee22222rtg" or "userId=igbrown"), the subsequent name/value pairings are meta-information that we would use to manage the storage of the cookie (when it expires, etc.).

        cookie = cookie.substring(0, cookie.indexOf(";"));
        String cookieName = cookie.substring(0, cookie.indexOf("="));
        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
					
This is basically it. We now have the cookie name (cookieName) and the cookie value (cookieValue).

Setting a cookie value in a request:

Values must be set prior to calling the connect method:
URL myUrl = new URL("http://www.hccp.org/cookieTest.jsp");
URLConnection urlConn = myUrl.openConnection();
Create a cookie string:
String myCookie = "userId=igbrown";
Add the cookie to a request:
Using the setRequestProperty(String name, String value); method, we will add a property named "Cookie", passing the cookie string created in the previous step as the property value.

urlConn.setRequestProperty("Cookie", myCookie);
Send the cookie to the server:
To send the cookie, simply call connect() on the URLConnection for which we have added the cookie property:

urlConn.connect()

Setting a multiple cookie values in a request:

Perform the same steps as the above item (Setting a a cookie value in a request), replacing the single valued cookie string with something like the following:
String myCookies = "userId=igbrown; sessionId=SID77689211949; isAuthenticated=true";
This string contains three cookies (userId, sessionId, and isAuthenticated). Separate cookie name/value pairs with "; " (semicolon and whitespace).

Note that you cannot set multiple request properties using the same name, so trying to call the setRequestProperty("Cookie" , someCookieValue) method will just overwrite any previously set value.

 */

    /**
     * CookieManager is a simple utilty for handling cookies when working
     * with java.net.URL and java.net.URLConnection
     * objects.
     * 
     * 
     *     Cookiemanager cm = new CookieManager();
     *     URL url = new URL("http://www.hccp.org/test/cookieTest.jsp");
     *     
     *      . . . 
     *
     *     // getting cookies:
     *     URLConnection conn = url.openConnection();
     *     conn.connect();
     *
     *     // setting cookies
     *     cm.storeCookies(conn);
     *     cm.setCookies(url.openConnection());
     * 
     *     @author Ian Brown
     *      
     **/

public class CookieManager {
        
    private Map store;

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR="; ";
    private static final String COOKIE = "Cookie";

    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';
    
    private DateFormat dateFormat;

    public CookieManager() {
		store = new HashMap();
		dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }
    

    /**
     * Retrieves and stores cookies returned by the host on the other side
     * of the the open java.net.URLConnection.
     *
     * The connection MUST have been opened using the connect()
     * method or a IOException will be thrown.
     *
     * @param conn a java.net.URLConnection - must be open, or IOException will be thrown
     * @throws java.io.IOException Thrown if conn is not open.
     */
    public void storeCookies(URLConnection conn) throws IOException {
	
		// let's determine the domain from where these cookies are being sent
		String domain = getDomainFromHost(conn.getURL().getHost());
		
		
		Map domainStore; // this is where we will store cookies for this domain
		
		// now let's check the store to see if we have an entry for this domain
		if (store.containsKey(domain)) {
		    // we do, so lets retrieve it from the store
		    domainStore = (Map)store.get(domain);
		} else {
		    // we don't, so let's create it and put it in the store
		    domainStore = new HashMap();
		    store.put(domain, domainStore);    
		}
		
		// OK, now we are ready to get the cookies out of the URLConnection
		
		String headerName=null;
		for (int i=1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
		    if (headerName.equalsIgnoreCase(SET_COOKIE)) {
			Map cookie = new HashMap();
			StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);
			
			// the specification dictates that the first name/value pair
			// in the string is the cookie name and value, so let's handle
			// them as a special case: 
			
			if (st.hasMoreTokens()) {
			    String token  = st.nextToken();
			    String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
			    String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
			    domainStore.put(name, cookie);
			    cookie.put(name, value);
			}
	    
			while (st.hasMoreTokens()) {
			    String token  = st.nextToken();
			    cookie.put(token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(),
			     token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length()));
			}
		    }
		}
    }
 

    /**
     * Prior to opening a URLConnection, calling this method will set all
     * unexpired cookies that match the path or subpaths for thi underlying URL
     *
     * The connection MUST NOT have been opened 
     * method or an IOException will be thrown.
     *
     * @param conn a java.net.URLConnection - must NOT be open, or IOException will be thrown
     * @throws java.io.IOException Thrown if conn has already been opened.
     */
    public void setCookies(URLConnection conn) throws IOException {
	
		// let's determine the domain and path to retrieve the appropriate cookies
		URL url = conn.getURL();
		String domain = getDomainFromHost(url.getHost());
		String path = url.getPath();
		
		Map domainStore = (Map)store.get(domain);
		if (domainStore == null) return;
		StringBuffer cookieStringBuffer = new StringBuffer();
		
		Iterator cookieNames = domainStore.keySet().iterator();
		while(cookieNames.hasNext()) {
		    String cookieName = (String)cookieNames.next();
		    Map cookie = (Map)domainStore.get(cookieName);
		    // check cookie to ensure path matches  and cookie is not expired
		    // if all is cool, add cookie to header string 
		    if (comparePaths((String)cookie.get(PATH), path) && isNotExpired((String)cookie.get(EXPIRES))) {
			cookieStringBuffer.append(cookieName);
			cookieStringBuffer.append("=");
			cookieStringBuffer.append((String)cookie.get(cookieName));
			if (cookieNames.hasNext()) cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
		    }
		}
		try {
		    conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
		} catch (java.lang.IllegalStateException ise) {
		    IOException ioe = new IOException("Illegal State! Cookies cannot be set on a URLConnection that is already connected. " 
		    + "Only call setCookies(java.net.URLConnection) AFTER calling java.net.URLConnection.connect().");
		    throw ioe;
		}
    }

    private String getDomainFromHost(String host) {
		if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
		    return host.substring(host.indexOf(DOT) + 1);
		} else {
		    return host;
		}
    }

    private boolean isNotExpired(String cookieExpires) {
		if (cookieExpires == null) return true;
		Date now = new Date();
		try {
		    return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
		} catch (java.text.ParseException pe) {
		    pe.printStackTrace();
		    return false;
		}
    }

    private boolean comparePaths(String cookiePath, String targetPath) {
		if (cookiePath == null) {
		    return true;
		} else if (cookiePath.equals("/")) {
		    return true;
		} else if (targetPath.regionMatches(0, cookiePath, 0, cookiePath.length())) {
		    return true;
		} else {
		    return false;
		}
	
    }
    
    /**
     * Returns a string representation of stored cookies organized by domain.
     */

    public String toString() {
    	return store.toString();
    }
    
    public static void main(String[] args) { 
		CookieManager cm = new CookieManager();
		try {
		    URL url = new URL("http://www.hccp.org/test/cookieTest.jsp");
		    URLConnection conn = url.openConnection();
		    conn.connect();
		    cm.storeCookies(conn);
		    System.out.println(cm);
		    cm.setCookies(url.openConnection());
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
    }
    
}
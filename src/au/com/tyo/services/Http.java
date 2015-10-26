/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import au.com.tyo.io.IO;

public class Http {
	
	public static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; U; en-us; sdk Build/MR1) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0";
	public static String DEFAULT_USER_AGENT_MOBILE = DEFAULT_USER_AGENT + " Mobile Safari/534.30";
			
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;
	public static final int METHOD_PUT = 3;
	public static final int METHOD_DELETE = 4;
	
    private static final int CONNECTION_CONNECT_TIMEOUT = 30000; // 30 seconds
    private static final int CONNECTION_READ_TIMEOUT = 180000; // 180 seconds
	private static final int PROGRESS_MAX = 100;
	
	private static String userAgent;
    
    private boolean enableCompression = true;
    private boolean cacheCookie = true;
    
//    private URLConnection connection = null;
    private HttpURLConnection httpConn = null;
    
    private Map<String, String> serverSideCookies = new HashMap<String, String>();
    private Map<String, String> clientSideCookies = new HashMap<String, String>();
    
    private int responseCode = 200;
    
    private int progress;
	private HttpRequestListener caller;
	private boolean inUsed;
	
	private Map<String, String> headers;
	private int method;
	
	private String cookieFile;
	
	static {
		userAgent = DEFAULT_USER_AGENT;
	}
	
	public static class Parameter {
		String name;
		String value;
		
		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	public Http() {
		headers = new HashMap<String, String>();
		cookieFile = null;
	}
	
	public static void setUserAgent(String agent) {
		userAgent = agent;
	}
	
	public static String getUserAgent() {
		return userAgent;
	}

	public synchronized boolean isInUsed() {
		return inUsed;
	}

	public synchronized void setInUsed(boolean inUsed) {
		this.inUsed = inUsed;
	}

	public HttpRequestListener getCaller() {
		return caller;
	}

	public void setCaller(HttpRequestListener caller) {
		this.caller = caller;
	}
	
	public synchronized void setMethod(int whatMethod) {
//		if (whatMethod >= 1 && whatMethod <= 4)
			method = whatMethod;
	}
	
	public void addCookieFile(String filename) {
		cookieFile = (filename);
	}
	
	public void loadCookieFromFile() {
		if (null != cookieFile) {
//			try {
			String text = new String(IO.readFileIntoBytes(cookieFile));
			String[] cookies = text.split(";");
			for (int i = 0; i < cookies.length; ++i) {
				try {
					String[] values = cookies[i].split(":");
					clientSideCookies.put(values[0], values[1]);
				}
				catch (Exception ex){
					
				}
			}
//			}
//			catch (Exception ex){
//				
//			}	
		}
	}
	
	public synchronized HttpURLConnection init(String url) {
		URLConnection connection = null;
		try {
			connection = new URL(url).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 	(HttpURLConnection)connection;
	}

	public synchronized String get() throws Exception {
		return get(httpConn);
	}
	
	public synchronized String get(String url) throws Exception {
		return get(url, 0);
	}
	
	public synchronized String get(String url, long storedModifiedDate) throws Exception {
		httpConn = init(url); //connection;
		return connect(httpConn, null, storedModifiedDate);
	}
	
	private synchronized String get(HttpURLConnection connection) throws Exception {
		return connect(connection, null, 0);
	}
	
	public static String getQuery(List<Parameter> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (Parameter pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.name, "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.value, "UTF-8"));
	    }

	    return result.toString();
	}
	
	public synchronized String post(String url) throws Exception {
		return post(url, null);
	}
	
	public synchronized String upload(String url, List<Parameter> params) throws Exception {
		httpConn = (HttpURLConnection) new URL(url).openConnection(); //init(url);
		httpConn.setRequestProperty("Content-Type", "multipart/form-data");
		
		return post(httpConn, params);
	}
	
	public synchronized String post(String url, List<Parameter> params) throws Exception {
		httpConn = (HttpURLConnection) new URL(url).openConnection(); //init(url);
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		return post(httpConn, params);
	}
	
	public synchronized String post(HttpURLConnection httpConn, List<Parameter> params) throws Exception {
		String output = ""; 
		setMethod(METHOD_POST);
		httpConn.setRequestMethod("POST");

		try {
			output = connect(httpConn, params, 0);
		}
		catch (Exception ex) {
			throw ex;
		}

		return output;
	}
	
	private synchronized String connect(HttpURLConnection connection, List<Parameter> params, long storedModifiedDate) throws Exception 
	{
		inUsed = true;
		
		BufferedReader br = null;
		
		InputStream is = null;

        StringBuilder text = new StringBuilder();
        
		OutputStream os = null;
		DataOutputStream writer = null;
//		try {
			
			/*
			 * 1. setup connection properties if it is a new conn
			 */

			setUserAgent();
			
			// connection properties
			setConnectionProperties();
			setHeaders();
			
        	setCookies(httpConn, clientSideCookies);
        	
			httpConn.setRequestProperty( "charset", "utf-8");
        	
			try {		
				// method
				switch (method) {
				case METHOD_GET:
				case METHOD_DELETE:			
					connection.setInstanceFollowRedirects(true);
					connection.setUseCaches(true);
					
					// timeout
					connection.setConnectTimeout(CONNECTION_CONNECT_TIMEOUT);
					connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
					
					break;
				case METHOD_PUT:
				case METHOD_POST:
					httpConn.setInstanceFollowRedirects(false);
					httpConn.setUseCaches(false);
					connection.setDoInput(true);
					httpConn.setDoOutput(true);
					
					break;
				}
				
	            /*
	             * If the modified date isn't 0, sets another request property to ensure that
	             * data is only downloaded if it has changed since the last recorded
	             * modification date. Formats the date according to the RFC1123 format.
	             */
	            if (0 != storedModifiedDate) {
	                connection.setRequestProperty(
	                        "If-Modified-Since",
	                        org.apache.http.impl.cookie.DateUtils.formatDate(
	                                new Date(storedModifiedDate),
	                                org.apache.http.impl.cookie.DateUtils.PATTERN_RFC1123));
	            }
			
			try {
				
	            if (null != params && params.size() > 0) {
    				
	    			try {		
	    				
	    				// method
	    				switch (method) {
	    				case METHOD_GET:
	    				case METHOD_DELETE:
	    					
	    					break;
	    				case METHOD_PUT:
	    				case METHOD_POST:
							String urlParameters = getQuery(params);
							byte[] postData = urlParameters .getBytes();
							
							httpConn.setRequestProperty("Content-Length", Integer.toString(postData.length));
	
							os = httpConn.getOutputStream();
				//			BufferedWriter writer = new BufferedWriter(
				//			        new OutputStreamWriter(os, "UTF-8"));
							writer = new DataOutputStream(os);
						    writer.write(postData);
							writer.flush();
	    					writer.close();
	    					writer = null;
	    					break;
	    				}
	    			}
	    			catch (Exception ex) {
	    				ex.printStackTrace();
	    			}
	            }
				
				connection.connect();
	            
				is = connection.getInputStream();
				
		        if (cacheCookie)
		        {
		            getCookies(connection, serverSideCookies);
		            clientSideCookies.putAll(serverSideCookies);
//		            serverSideCookies.putAll(clientSideCookies);
		        }
			}
			catch (Exception ex) {
				is = httpConn.getErrorStream();
			}
			finally {
				responseCode = connection.getResponseCode();
			}
			
			/**
			 * there are many possible ways to deal with each diffirent redirect reponse code
			 * let's do it in the simplest way here for now
			 */
			if (responseCode >= 300 && responseCode <= 310) {
				return get(connection.getHeaderField("location"));
			}
			
			InputStream in = null;
			
			try {
				if (enableCompression && "gzip".equals(connection.getContentEncoding()))
					in = new GZIPInputStream(is);
				else if (enableCompression && "deflate".equals(connection.getContentEncoding())) {
					in = new ZipInputStream(is);
				}
				else
					in = is;
			}
			catch (Exception e) {
				in = is;
			}
			
			if (null != in) {
				br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				
				if (br != null) {
					if (caller != null) {
						caller.onResourceAvailable();
						caller.onProgressChanged(0);
					}
					
			        // get cookies
					double contentLength = connection.getContentLength();
					double bytesRead = 0.0;
					double progress = 20.0;
			
			        // get page text
			        String line;
	
					while ((line = br.readLine()) != null)
					{
					    text.append(line);
					    text.append("\n");
					    
						if (contentLength > 0) {
							bytesRead += line.getBytes().length;
	
							if (bytesRead > contentLength)
								progress = PROGRESS_MAX;
							else
								progress = (bytesRead / contentLength) * PROGRESS_MAX;
						}
						try {
							if (caller != null)
								caller.onProgressChanged((int) progress);
	//							caller.setProgress((int) progress);
						}
						catch (Exception ex) {
							System.err.println("Updating progress failed!");
							ex.printStackTrace();
						}
					}
					if (caller != null)
						caller.onProgressChanged(PROGRESS_MAX);
					in.close();
					
					if (in instanceof GZIPInputStream)
						is.close();
					
					br.close();
				} 
			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			try {
//				responseCode = connection.getResponseCode();
//			} catch (IOException e1) {
//
//			};
//			
//			if (responseCode == 404) {
//				
//			}
//	
//		}
//		catch (Exception e) {
//			try {
//				responseCode = connection.getResponseCode();
//			} catch (IOException e1) {
//
//			};
//		}
//		finally {
			if (caller != null) {
				progress = PROGRESS_MAX;
				caller.onProgressChanged((int) progress);
			}
//		}
		}
		catch (java.lang.IllegalStateException ise) {
			String msg = ise.getMessage();
			if (msg.indexOf("Already connected") < 0)
				throw ise;
		}
		catch (Exception others) {
			throw others;
		}
		finally {
//				httpConn.disconnect();
			
			if (null != writer)
				writer.close();
			if (null != os)
				os.close();
		}
			
		reset();
        return text.toString();
	}

	private void setHeaders() {
		Set<Entry<String, String>> allHeaders = headers.entrySet();
//		Entry<String, String> entry = null;
		for (Entry<String, String> entry : allHeaders) {
			httpConn.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	private void reset() {
		inUsed = false;
		headers.clear();
	}

	public int getResponseCode() {
		return responseCode;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isConntentChanged(String url) {
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
	      con.setRequestMethod("HEAD");
	      return (con.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED);
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	}
	
	public long getLastModifiedDate() {
		return httpConn.getLastModified();
	}

	/**
	 * 
	 * Should be the same as using curl -R --head  http://www.google.com/doodles/doodles.xml | more
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static long getLastModifiedDate(String url) throws MalformedURLException, IOException
	{
		/*
		 * not to use this one
		 * 		HttpURLConnection.setFollowRedirects(false);
		 * 
		 */
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod("HEAD");
		con.connect();
		
		/*long date =*/return con.getLastModified();
		
//		if (date == 0) 
//			date = con.getDate();
//		
//		if (date == 0)
//			return null;
//		return new Date(date);
	}

	public void setConnectionProperties() {
		// enable gzip compression
		if (enableCompression)
			httpConn.setRequestProperty("Accept-encoding", "gzip,deflate");
	}
	
	public void setUserAgent() {
		setUserAgent(httpConn, userAgent);
	}
	
	public static void setUserAgent(URLConnection connection, String userAgent) {
		connection.setRequestProperty("User-Agent", userAgent);
	}
	
	private void setCookies(URLConnection connection, Map<String, String> cookies) {
		if (cookies != null) {
	        StringBuilder cookie = new StringBuilder();
	        for (Map.Entry<String, String> entry : cookies.entrySet())
	        {
	            cookie.append(entry.getKey());
	            cookie.append("=");
	            cookie.append(entry.getValue());
	            cookie.append("; ");
	        }
	        connection.setRequestProperty("Cookie", cookie.toString());
		}
	}
	
	private void getCookies(URLConnection connection, Map<String, String> cookies) {
		Map<String, List<String>> headers = connection.getHeaderFields();
		List<String> values = headers.get("Set-Cookie");

		if (values != null) {
			String cookieValue = null;
			for (Iterator iter = values.iterator(); iter.hasNext(); ) {
			     String cookie = (String) iter.next();
			     
			     /*
			      * why ignore the session cookie?
			      * comment it off for now
			      */
			     /*
	             if (cookie.contains("_session"))
	                 continue;
				 */
			     
	             cookie = cookie.substring(0, cookie.indexOf(';'));
	             String name = cookie.substring(0, cookie.indexOf('='));
	             String value = cookie.substring(cookie.indexOf('=') + 1, cookie.length());
	             cookies.put(name, value);
			} 
		}
	}

	public String getUrl() {
		return this.httpConn.getURL().toString();
	}

	public void setHeader(String header, String value) {
		headers.put(header, value);
	}

	public Map<String, String> getCookies() {
		return clientSideCookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.clientSideCookies = cookies;
	}
	
	public void addCookies(Map<String, String> cookies) {
		this.clientSideCookies.putAll(cookies);
	}
}

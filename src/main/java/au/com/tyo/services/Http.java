/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import au.com.tyo.io.IO;

public class Http {
	
	public static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; U; en-us; sdk Build/MR1) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0";
	public static String DEFAULT_USER_AGENT_MOBILE = DEFAULT_USER_AGENT + " Mobile Safari/534.30";
			
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;  // x-www-form-urlencoded
	public static final int METHOD_POST_MULTIPART_FORM_DATA = 22; // multpart/form-data
	public static final int METHOD_PUT = 3;
	public static final int METHOD_DELETE = 4;
	
    private static final int CONNECTION_CONNECT_TIMEOUT = 30000; // 30 seconds
    private static final int CONNECTION_READ_TIMEOUT = 180000; // 180 seconds
	private static final int PROGRESS_MAX = 100;
	
	private static final String CRLF = "\r\n";
	private static final String TWOHYPHENS = "--";
	private static final String BOUNDARY =  "-----------------0t1y2o3l4a5b6";
	
	public static final String MIME_TYPE_XML = "text/xml";
	public static final String MIME_TYPE_JSON = "text/json";
	public static final String MIME_TYPE_PLAIN = "text/plain";
	
	public static final String HEADER_SET_COOKIE = "Set-Cookie";
	
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
	
	private static String cookiePath = ".";
	
//	private String cookieFile;
	
	public static void setCookiePath(String path) {
		cookiePath = path;
	}
	
	static {
		userAgent = DEFAULT_USER_AGENT;
	}
	
	public static class Settings {

		boolean keepAlive;
		
		boolean automaticLoadCookie;
		
		long storedModifiedDate;
		
		List<Parameter> params; // the parameters that need to posted

		List<Parameter> multipart; //

		List<Parameter> headers;
		
		public Settings() {
			this(null);
			automaticLoadCookie = false;
			keepAlive = true;
			storedModifiedDate = 0;
			headers = new ArrayList<>();
			params = new ArrayList<>();
		}
		
		public Settings(List<Parameter> params) {
			this.params = params;
		}

		public boolean isKeepAlive() {
			return keepAlive;
		}

		public void setKeepAlive(boolean keepAlive) {
			this.keepAlive = keepAlive;
		}

		public boolean isAutomaticLoadCookie() {
			return automaticLoadCookie;
		}

		public void setAutomaticLoadCookie(boolean automaticLoadCookie) {
			this.automaticLoadCookie = automaticLoadCookie;
		}

		public long getStoredModifiedDate() {
			return storedModifiedDate;
		}

		public void setStoredModifiedDate(long storedModifiedDate) {
			this.storedModifiedDate = storedModifiedDate;
		}

		public List<Parameter> getParams() {
			return params;
		}

		public List<Parameter> getHeaders() {
			return headers;
		}

		public void addHeader(String header, String value) {
			headers.add(new Parameter(header, value));
		}

		public void addParam(String name, String value) {
			params.add(new Parameter(name, value));
		}

		public void setParams(List<Parameter> params) {
			this.params = params;
		}

		public boolean hasParams() {
			return null != params && params.size() > 0;
		}
		
	}

	static public Settings createDefaultSettings() {
		return new Settings();
	}
	
	public static class Parameter extends AbstractMap.SimpleEntry<String, String> {
		public static final String DELIMETER = ";";
		
		String contentType;
		
		HashMap<String, String> extra;
		
		public Parameter(String name, String value) {
			super(name, value);
			
			contentType = MIME_TYPE_PLAIN;
			extra = null;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}
		
		public void addExtra(String name, String value) {
			if (extra == null)
				extra = new HashMap<String, String>();
			
			extra.put(name, value);
		}
		
		public String extraToString() {
			if (null == extra)
				return "";
			
			StringBuffer sb = new StringBuffer();
			
			for (Entry<String, String> entry : extra.entrySet()) {
				if (sb.length() > 0)
					sb.append("; ");
				
				sb.append(entry.getKey() + "=\"" + entry.getValue() + "\"");
				
			}
			
			return sb.toString();
		}
		
		public boolean isNotPlainText() { return !contentType.equals(MIME_TYPE_PLAIN); }
	}
	
	public Http() {
		headers = new HashMap<String, String>();
//		cookieFile = null;
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
	
	public String createCookieFile() {
		return createCookieFile(httpConn.getURL().getHost());
	}
	
	public static String createCookieFile(String host) {
		return cookiePath + File.separator + host + ".cookie";
	}
	
	public void loadCookieFromFile(String cookieFile) {
		if (null != cookieFile) {
			File file = new File(cookieFile);
			
			if (file.exists()) {
				String text = new String(IO.readFileIntoBytes(file));
				String[] cookies = text.split(";");
				if (null != cookies)
					for (int i = 0; i < cookies.length; ++i) {
						try {
							String[] values = cookies[i].split(":");
							if (null != values && values.length == 2)
								clientSideCookies.put(values[0], values[1]);
						}
						catch (Exception ex){
							
						}
					}
			}
//			}
//			catch (Exception ex){
//				
//			}	
		}
	}
	
	public void saveCookieToFile() throws IOException {
		String cookieFile = createCookieFile();
		if (clientSideCookies.size() > 0) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(cookieFile, false));
//				int count = 0;
				for (Entry entry : clientSideCookies.entrySet()) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					
					writer.write(key +":" + value + ";");
//					if (count > 0)
//						writer.write(";");
//					++count;
				}
			}
			catch (IOException ex) {
				throw ex;
			}
			finally {
				if (null != writer)
					writer.close();
			}
		}
//		Uri uri = Uri.parse(httpConn.getURL());
//		String host = httpConn.getURL().getHost();
//		String cookieName = host + ".cookie";
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
		return get(url, 0, true);
	}
	
	public synchronized String get(String url, boolean keepAlive) throws Exception {
		return get(url, 0, keepAlive);
	}
	
	public synchronized String get(String url, long storedModifiedDate, boolean keepAlive) throws Exception {
		httpConn = init(url); //connection;
		return connect(null, storedModifiedDate, keepAlive);
	}
	
	private synchronized String get(HttpURLConnection connection) throws Exception {
		return connect(null, 0, true);
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

	        result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
	
	public synchronized String post(String url) throws Exception {
		return post(url, new Settings());
	}
	
	public synchronized String upload(String url, Settings settings) throws Exception {
		httpConn = (HttpURLConnection) new URL(url).openConnection(); //init(url);
		String contentType = "multipart/form-data; boundary=" + BOUNDARY;
		httpConn.setRequestProperty("Content-Type", contentType);

		return post(settings, METHOD_POST_MULTIPART_FORM_DATA);
	}
	
	public synchronized String post(String url, Settings settings) throws Exception {
		httpConn = (HttpURLConnection) new URL(url).openConnection(); //init(url);
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		return post(settings);
	}
	
	public synchronized String post(Settings settings) throws Exception {
		return post(settings, METHOD_POST);
	}
	
	public synchronized String post(Settings settings, int postMethod) throws Exception {
		String output = ""; 
		setMethod(postMethod);
		httpConn.setRequestMethod("POST");
		httpConn.setRequestProperty("Cache-Control", "no-cache");

		try {
			output = connect(settings);
		}
		catch (Exception ex) {
			throw ex;
		}

		return output;
	}
	
	private synchronized String connect(List<Parameter> params, 
			long storedModifiedDate, boolean keepAlive) throws Exception {
		Settings settings = new Settings();
		settings.setKeepAlive(keepAlive);
		settings.setParams(params);
		settings.setStoredModifiedDate(storedModifiedDate);
		
		return connect(settings);
	}
		
	private synchronized String connect(Settings settings) throws Exception {
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
			setHeaders(settings.getHeaders().toArray());
			
			if (settings.isAutomaticLoadCookie()) {
				String cookieFile = createCookieFile();
				loadCookieFromFile(cookieFile);
			}
        	setCookies(clientSideCookies);
        	
			httpConn.setRequestProperty( "Charset", "utf-8");
        	
			try {		
				// method
				switch (method) {
				case METHOD_GET: // not implemented
				case METHOD_DELETE:			
					httpConn.setInstanceFollowRedirects(true);
					httpConn.setUseCaches(true);
					
					// timeout
					httpConn.setConnectTimeout(CONNECTION_CONNECT_TIMEOUT);
					httpConn.setReadTimeout(CONNECTION_READ_TIMEOUT);
					
					break;
				case METHOD_PUT:
				case METHOD_POST:
				case METHOD_POST_MULTIPART_FORM_DATA:
					httpConn.setInstanceFollowRedirects(false);
					httpConn.setUseCaches(false);
					httpConn.setDoInput(true);
					httpConn.setDoOutput(true);
					
					break;
				}
				
	            /*
	             * If the modified date isn't 0, sets another request property to ensure that
	             * data is only downloaded if it has changed since the last recorded
	             * modification date. Formats the date according to the RFC1123 format.
	             */
	            if (0 != settings.getStoredModifiedDate()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
					dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

	                httpConn.setRequestProperty(
	                        "If-Modified-Since",
                            dateFormat.format(new Date(settings.getStoredModifiedDate())));
	            }
			
	        /*
	         * VERY IMPORTANT!
	         * 
	         * all the setrequestpropertys have to happen before getting the outputstream which mean it will connect
	         * to server automatically
	         * 
	         */
			try {
				
	            if (settings.hasParams()) {
	            	byte[] postData = null;
	    			try {		

	    				// method
	    				switch (method) {
	    				case METHOD_GET:
	    				case METHOD_DELETE:
	    					
	    					break;
	    				case METHOD_PUT:
	    				case METHOD_POST:
							String urlParameters = getQuery(settings.getParams());
							postData = urlParameters.getBytes();
							
							httpConn.setRequestProperty("Content-Length", Integer.toString(postData.length));
				//			BufferedWriter writer = new BufferedWriter(
				//			        new OutputStreamWriter(os, "UTF-8"));
							os = httpConn.getOutputStream();
		    				writer = new DataOutputStream(os);
						    writer.write(postData);

	    					break;
	    				case METHOD_POST_MULTIPART_FORM_DATA:
	    					// now the content
	    					StringBuffer sb = new StringBuffer();
	    					sb.append(CRLF);
	    					boolean needTwoHyphens = false;
	    					if (settings.getParams().size() > 1) // yeah, multi parts
	    						needTwoHyphens = true; 
	    					String boundary = (needTwoHyphens ? TWOHYPHENS : "") + BOUNDARY + CRLF;
	    					
	    					for (Parameter param : settings.getParams()) {
		    					sb.append(boundary);
		    					
		    					String extra = param.extraToString();
		    					String line = String.format("Content-Disposition: form-data; name=\"%s\"%s", 
		    							param.getKey(), extra.length() > 0 ? ("; " + extra) : "") + CRLF;
		    					sb.append(line);
		    					
		    					if (param.isNotPlainText())
		    						sb.append("Content-Type:" + param.getContentType() + CRLF);

		    					sb.append(CRLF);
		    					
		    					sb.append(param.getValue());
		    					sb.append(CRLF);
	    					}
	    					sb.append(TWOHYPHENS);
	    					sb.append(BOUNDARY);
	    					sb.append(TWOHYPHENS);
	    					
	    					postData = sb.toString().getBytes();
	    					httpConn.setRequestProperty("Content-Length", Integer.toString(postData.length));
	    					
							os = httpConn.getOutputStream();
		    				writer = new DataOutputStream(os);
		    				writer.write(postData);
	    					break;
	    				}
						writer.flush();

	    			}
	    			catch (Exception ex) {
	    				ex.printStackTrace();
	    			}
	    			finally {
    					writer.close();
    					writer = null;
	    			}
	            }
	            else {
    				switch (method) {
    				case METHOD_GET:
    				case METHOD_DELETE:
    					
    					break;
    				case METHOD_PUT:
    				case METHOD_POST:
//    					httpConn.setRequestProperty("Content-Length", "0");
    					break;
					default:
						break;
    				}
	            }
				
//				if (!settings.isKeepAlive()) {
//					httpConn.setRequestProperty("Connection", "close"); 
//					
//					/*
//					 * as for Android, this may be necessary to avoid the SocketException: 
//					 * 	libcore.io.ErrnoException: recvfrom failed: ECONNRESET (Connection reset by peer)
//					 */
////					String value = System.getProperty("http.keepAlive");
////					System.setProperty("http.keepAlive", "false");
//				}
//				else
//					httpConn.setRequestProperty("Connection", "Keep-Alive"); 
				
				httpConn.connect();
	            
				is = httpConn.getInputStream();
				
		        if (cacheCookie)
		        {
		            getCookies(serverSideCookies);
		            clientSideCookies.putAll(serverSideCookies);
//		            serverSideCookies.putAll(clientSideCookies);
		        }
			}
			catch (Exception ex) {
				is = httpConn.getErrorStream();
			}
			finally {
				responseCode = httpConn.getResponseCode();
			}
			
			if (!settings.isKeepAlive()) {
				/*
				 * with previous settings
				 */
//				System.setProperty("http.keepAlive", "true");
			}
			
			/**
			 * there are many possible ways to deal with each diffirent redirect reponse code
			 * let's do it in the simplest way here for now
			 */
			if (responseCode >= 300 && responseCode <= 310) {
				return get(httpConn.getHeaderField("location"));
			}
			
			InputStream in = null;
			
			try {
				if (enableCompression && "gzip".equals(httpConn.getContentEncoding()))
					in = new GZIPInputStream(is);
				else if (enableCompression && "deflate".equals(httpConn.getContentEncoding())) {
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
					double contentLength = httpConn.getContentLength();
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
//				responseCode = httpConn.getResponseCode();
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
//				responseCode = httpConn.getResponseCode();
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
		setHeaders(allHeaders.toArray());
	}

	public void setHeaders(Object[] objects) {
		for (Object obj : objects) {
			if (obj instanceof Entry) {
				Entry<String, String> entry = (Entry) obj;
				httpConn.setRequestProperty(entry.getKey(), entry.getValue());
			}
			else if (obj instanceof Parameter) {
				Parameter param = (Parameter) obj;
				httpConn.setRequestProperty(param.getKey(), param.getValue());
			}
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
	
	private void setCookies(Map<String, String> cookies) {
		if (cookies != null) {
	        StringBuilder cookie = new StringBuilder();
	        for (Map.Entry<String, String> entry : cookies.entrySet())
	        {
	            cookie.append(entry.getKey());
	            cookie.append("=");
	            cookie.append(entry.getValue());
	            cookie.append("; ");
	        }
	        httpConn.setRequestProperty("Cookie", cookie.toString());
		}
	}
	
	private void getCookies(Map<String, String> cookies) {
		Map<String, List<String>> headers = httpConn.getHeaderFields();
		
		/*
		 * loop the header not by get, because of the case-insensitive thingy
		 */
		
		
		List<String> values = headers.get(HEADER_SET_COOKIE);

		if (values != null && values.size() > 0) {
//			String cookieValue = null;
			headerToCookie(cookies, values);
		}
		else {
			for (Entry entry : headers.entrySet()) {
				String key = (String) entry.getKey();
				if (null != key && key.equalsIgnoreCase(HEADER_SET_COOKIE)) {
					headerToCookie(cookies, (List<String>) entry.getValue());
					break;
				}
			}
		}
	}

	private void headerToCookie(Map<String, String> cookies, List<String> values) {
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

	public String getUrl() {
		return this.httpConn.getURL().toString();
	}

	public void setHeader(String header, String value) {
		headers.put(header, value);
	}

	public Map<String, String> getClientCookies() {
		return clientSideCookies;
	}

	public void setClientCookies(Map<String, String> cookies) {
		this.clientSideCookies = cookies;
	}
	
	public void addClientCookies(Map<String, String> cookies) {
		this.clientSideCookies.putAll(cookies);
	}

}

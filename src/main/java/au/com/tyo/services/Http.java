/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class Http extends HttpConnection {

	//    private URLConnection connection = null;
	private HttpURLConnection httpConn = null;

	public Http() {
		headers = new HashMap<String, String>();
//		cookieFile = null;
	}

	@Override
    public String createCookieFile() {
        return createCookieFile(httpConn.getURL().getHost());
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

	@Override
	public synchronized String get(String url, long storedModifiedDate, boolean keepAlive) throws Exception {
		httpConn = init(url); //connection;
		return connect(url, null, storedModifiedDate, keepAlive);
	}

    @Override
	public synchronized void upload(String url, HttpRequest settings) throws Exception {
		httpConn = (HttpURLConnection) new URL(url).openConnection(); //init(url);
		String contentType = "multipart/form-data; boundary=" + BOUNDARY;
		httpConn.setRequestProperty("Content-Type", contentType);

		post(settings, METHOD_POST_MULTIPART_FORM_DATA);
	}

    @Override
	public synchronized InputStream post(HttpRequest settings) throws Exception {
		httpConn = (HttpURLConnection) new URL(settings.getUrl()).openConnection(); //init(url);
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		return post(settings);
	}

    @Override
	public synchronized InputStream post(HttpRequest settings, int postMethod) throws Exception {
		InputStream output = null;
		setMethod(postMethod);
		httpConn.setRequestMethod("POST");
		httpConn.setRequestProperty("Cache-Control", "no-cache");

		try {
			output = connectAsInputStream(settings);
		}
		catch (Exception ex) {
			throw ex;
		}

		return output;
	}

	protected synchronized String connect(String url, List<Parameter> params,
			long storedModifiedDate, boolean keepAlive) throws Exception {
		HttpRequest settings = new HttpRequest(url, params);
		settings.setKeepAlive(keepAlive);
		settings.setStoredModifiedDate(storedModifiedDate);
		
		return connect(settings);
	}

	@Override
	public InputStream postJSON(String url, Object json) throws Exception {
		httpConn.setRequestProperty("Content-Type", "application/json");
		return post(new HttpRequest(url), METHOD_POST);
	}

	protected synchronized String connect(HttpRequest settings) throws Exception {
        InputStream is = connectAsInputStream(settings);
        InputStream in = null;

        if (httpConn == null)
            httpConn = init(settings.getUrl());

        String text = null;
        try {
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

            text = httpInputStreamToText(in, httpConn.getContentLength());

            if (in instanceof GZIPInputStream)
                is.close();

            in.close();
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
        }
        return text;
	}

	protected synchronized InputStream connectAsInputStream(HttpRequest settings) throws Exception {

		inUsed = true;
		cancelled = false;

        InputStream is = null;
		OutputStream os = null;
		DataOutputStream writer = null;

		try {
			
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

            httpConn.setRequestProperty("Charset", "utf-8");

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
                                String postContent = null;
                                if (null != settings.getContent())
                                    postContent = settings.getContent().toString();
                                else
                                    postContent = getQuery(settings.getParams());
                                postData = postContent.getBytes();

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

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        writer.close();
                        writer = null;
                    }
                } else {
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
                if (!isCancelled())
                    is = httpConn.getInputStream();

                if (cacheCookie) {
                    getCookies(serverSideCookies);
                    clientSideCookies.putAll(serverSideCookies);
//		            serverSideCookies.putAll(clientSideCookies);
                }
            } catch (Exception ex) {
                is = httpConn.getErrorStream();
            } finally {
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
                return connectAsInputStream(new HttpRequest(httpConn.getHeaderField("location")));
            }
        }
        finally {
            if (caller != null) {
                progress = PROGRESS_MAX;
                caller.onProgressChanged((int) progress);
            }

            if (null != writer)
                writer.close();
            if (null != os)
                os.close();

            reset();
        }

        return is;
	}

    /**
     *
     * @param objects
     */
    @Override
    public void setHeaders(Object[] objects) {
        for (Object obj : objects) {
            if (obj instanceof Map.Entry) {
                Map.Entry<String, String> entry = (Map.Entry) obj;
                httpConn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            else if (obj instanceof Parameter) {
                Parameter param = (Parameter) obj;
                httpConn.setRequestProperty(param.getKey(), param.getValue());
            }
        }
    }

    /**
     *
     * @return
     */
	public long getLastModifiedDate() {
		return httpConn.getLastModified();
	}

    /**
     *
     */
	public void setConnectionProperties() {
		// enable gzip compression
		if (enableCompression)
			httpConn.setRequestProperty("Accept-encoding", "gzip,deflate");
	}

    /**
     *
     */
	public void setUserAgent() {
		setUserAgent(httpConn, userAgent);
	}

    /**
     *
     * @param cookies
     */
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

    /**
     *
     * @param cookies
     */
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

    /**
     *
     * @param cookies
     * @param values
     */
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

    /**
     *
     * @return
     */
    @Override
	public String getUrl() {
		return this.httpConn.getURL().toString();
	}

    @Override
    protected void reset() {
        super.reset();
        setInUsed(false);
        httpConn = null;
    }
}

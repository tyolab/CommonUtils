package au.com.tyo.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.tyo.io.IO;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 16/5/17.
 */

public abstract class HttpConnection {

    public static final String VERSION = "1.0.0";

    public static String DEFAULT_USER_AGENT = "TYODROID/" + VERSION;

    public static String BROWSER_USER_AGENT = DEFAULT_USER_AGENT + " Mozilla/5.0 (Linux; U; en-us; sdk Build/MR1) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0";
    public static String BROWSER_USER_AGENT_MOBILE = BROWSER_USER_AGENT + " Mobile Safari/534.30";

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;  // x-www-form-urlencoded
    public static final int METHOD_POST_MULTIPART_FORM_DATA = 22; // multpart/form-data
    public static final int METHOD_PUT = 3;
    public static final int METHOD_DELETE = 4;

    protected static final int CONNECTION_CONNECT_TIMEOUT = 30000; // 30 seconds
    protected static final int CONNECTION_READ_TIMEOUT = 180000; // 180 seconds
    protected static final int PROGRESS_MAX = 100;

    protected static final String CRLF = "\r\n";
    protected static final String TWOHYPHENS = "--";
    protected static final String BOUNDARY =  "-----------------0t1y2o3l4a5b6";

    public static final String MIME_TYPE_XML = "text/xml";
    public static final String MIME_TYPE_JSON = "text/json";
    public static final String MIME_TYPE_PLAIN = "text/plain";

    public static final String HEADER_SET_COOKIE = "Set-Cookie";

    /**
     * static members
     */
    protected static String userAgent;

    protected static String cookiePath = ".";

    static {
        userAgent = BROWSER_USER_AGENT;
    }

    public static class HttpRequest {
        String url;

        String requestMethod;

        boolean keepAlive;

        boolean automaticLoadCookie;

        long storedModifiedDate;

        /**
         * for url encoded name / value pairs
         */
        List<Parameter> params; // the parameters that need to posted

        List<Parameter> multipart; //

        List<Parameter> headers;

        Object content;

        public HttpRequest(String url) {
            this.url = url;
            requestMethod = null;
            automaticLoadCookie = false;
            keepAlive = true;
            storedModifiedDate = 0;
            headers = new ArrayList<>();
            params = new ArrayList<>();
        }

        public HttpRequest(String url, List<Parameter> params) {
            this(url);
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

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }

        public boolean hasParams() {
            return null != params && params.size() > 0;
        }

        public String toUrlEncodedString() throws UnsupportedEncodingException {
            if (hasParams())
                return getQuery(params);
            return null;
        }

        public Map paramsToMap() {
            Map map = new HashMap();
            for (Parameter param : params)
                map.put(param.getKey(), param.getValue());
            return map;
        }

        public String getUrl() {
            return url;
        }
    }

    static public HttpRequest createDefaultSettings(String url) {
        return new HttpRequest(url);
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

            for (Map.Entry<String, String> entry : extra.entrySet()) {
                if (sb.length() > 0)
                    sb.append("; ");

                sb.append(entry.getKey() + "=\"" + entry.getValue() + "\"");

            }

            return sb.toString();
        }

        public boolean isNotPlainText() { return !contentType.equals(MIME_TYPE_PLAIN); }
    }

    /**
     * other members
     */
    protected boolean enableCompression = true;
    protected boolean cacheCookie = true;

    protected Map<String, String> serverSideCookies = new HashMap<String, String>();
    protected Map<String, String> clientSideCookies = new HashMap<String, String>();

    protected int responseCode = 200;

    protected int progress;
    protected HttpRequestListener caller;
    protected boolean inUsed;

    protected Map<String, String> headers;
    protected int method;

    protected boolean cancelled;

    /**
     * Indicator for letting the process keep the instance for private use
     *
     */

    private boolean engaged = false;

    public void setEngaged(boolean engaged) {
        this.engaged = engaged;
    }

    public boolean isEngaged() {
        return engaged;
    }

    public static void setCookiePath(String path) {
        cookiePath = path;
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

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    public synchronized void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
        }
    }

    public void saveCookieToFile() throws IOException {
        String cookieFile = createCookieFile();
        if (clientSideCookies.size() > 0) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(cookieFile, false));
//				int count = 0;
                for (Map.Entry entry : clientSideCookies.entrySet()) {
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

    public abstract String createCookieFile();

    public synchronized String get(String url) throws Exception {
        return get(url, 0, true);
    }

    public synchronized String get(String url, boolean keepAlive) throws Exception {
        return get(url, 0, keepAlive);
    }

    /**
     *
     * @param url
     * @param storedModifiedDate
     * @param keepAlive
     * @return
     * @throws Exception
     */
    public abstract String get(String url, long storedModifiedDate, boolean keepAlive) throws Exception;

    /**
     *
     * @param url
     * @return
     * @throws Exception
     */
    public synchronized InputStream post(String url) throws Exception {
        return post(new HttpRequest(url));
    }

    protected abstract InputStream connectForInputStream(String url) throws Exception;

    /**
     *
     * @param settings
     * @return
     * @throws Exception
     */
    public synchronized InputStream post(HttpRequest settings) throws Exception {
        return post(settings, METHOD_POST);
    }

    /**
     * Return reponse as string
     *
     * @param settings
     * @return
     * @throws Exception
     */
    public String postWithResult(HttpRequest settings) throws Exception {
        return httpInputStreamToText(post(settings, METHOD_POST));
    }

    /**
     *
     * @return
     */
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



    /**
     *
     * @param connection
     * @param userAgent
     */
    public static void setUserAgent(URLConnection connection, String userAgent) {
        connection.setRequestProperty("User-Agent", userAgent);
    }

    /**
     *
     * @return
     */
    public Map<String, String> getClientCookies() {
        return clientSideCookies;
    }

    /**
     *
     * @param cookies
     */
    public void setClientCookies(Map<String, String> cookies) {
        this.clientSideCookies = cookies;
    }

    /**
     *
     * @param cookies
     */
    public void addClientCookies(Map<String, String> cookies) {
        this.clientSideCookies.putAll(cookies);
    }

    /**
     *
     * @param header
     * @param value
     */
    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    /**
     *
     */
    protected void setHeaders() {
        Set<Map.Entry<String, String>> allHeaders = headers.entrySet();
        setHeaders(allHeaders.toArray());
    }

    public abstract long getLastModifiedDate(String url) throws MalformedURLException, IOException;

    protected void reset() {
        inUsed = false;
        headers.clear();
    }
    /**
     *
     * code from: http://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, List<String>> toQueryParameterList(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }

    /**
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> toQueryParameters(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    /**
     * @param in
     */
    public static String httpInputStreamToText(InputStream in) throws IOException {
        return httpInputStreamToText(in, -1);
    }

    /**
     *
     * @param in
     * @param contentLength
     * @return
     * @throws IOException
     */
    protected static String httpInputStreamToText(InputStream in, double contentLength) throws IOException {
        return httpInputStreamToText(in, contentLength, null);
    }

    /**
     *
     * @param in
     * @param contentLength
     * @return
     * @throws IOException
     */
    public static String httpInputStreamToText(InputStream in, double contentLength, HttpRequestListener caller) throws IOException {

        if (null != in) {
            BufferedReader br = null;
            StringBuilder text = new StringBuilder();

            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            if (br != null) {
                if (caller != null) {
                    caller.onResourceAvailable();
                    caller.onProgressChanged(0);
                }

                double bytesRead = 0.0;
                double progress = 20.0;

                // get page text
                String line;

                while ((line = br.readLine()) != null)
                {
                    if (null != caller && caller.isCancelled())
                        break;

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
                br.close();
                return text.toString();
            }
        }
        return null;
    }

    public abstract InputStream upload(String url, HttpRequest settings) throws Exception;

    public String uploadWithResult(String url, HttpRequest settings) throws Exception {
        return httpInputStreamToText(upload(url, settings));
    }

    public abstract InputStream post(HttpRequest settings, int postMethod) throws Exception;

    public abstract void setHeaders(Object[] objects);

    public abstract String getUrl();

    public abstract InputStream postJSON(String url, Object json) throws Exception;

    public InputStream getAsInputStream(String url) throws Exception {
        return connectForInputStream(url);
    }
}

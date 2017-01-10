/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services.third;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import au.com.tyo.services.HttpPool;

public class GooGl {
	
	public static final String GOO_GL_REQUEST_URL_TEMPLATE = "";
	
	private static final String API_KEY = "AIzaSyALjSgLRg2kgMXVT2Q_RE_i2ZLt1OB6xdI";

	public static String shorten(String url) {
		String jsonResult = "{}";
		try {
			jsonResult = HttpPool.getInstance().getConnection().get(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String shortenedUrl = parse(jsonResult);
		return shortenedUrl;
	}
	
	public static String post(String url) {
		HttpURLConnection httpcon = null;
		
		try {
			httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
			
			httpcon.setDoOutput(true);
			httpcon.setRequestProperty("Content-Type", "application/json");
			httpcon.setRequestProperty("Accept", "application/json");
			httpcon.setRequestMethod("POST");
			httpcon.connect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] outputBytes = {0};
		OutputStream os = null;
		InputStream is = null;
		BufferedReader in = null;
        StringBuilder text = new StringBuilder();
        
		try {
			outputBytes = "{'longUrl': \" \"}".getBytes("UTF-8");
			
			os = httpcon.getOutputStream();
			os.write(outputBytes);
			is = httpcon.getInputStream();
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				
			if (in != null) {
		        // get page text
		        String line;

				while ((line = in.readLine()) != null)
				{
				    text.append(line);
				    text.append("\n");
				}
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (os != null)
					os.close();
				if (is != null)
					is.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
		
		return text.toString();
	}
	
	public static String getShortenedUrl(String url) {
		String jsonText = post(url);
		String shortUrl = parse(jsonText);
		return shortUrl;
	}
	
	public static String parse(String jsonText) {
		String url = null;
		if (jsonText != null || jsonText.length() > 0) {
			JSONObject array = null;
			
			try {
				array = new JSONObject(jsonText);
				
				if (array.has("id"))
					url = array.getString("id");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return url;
	}
	
	
}

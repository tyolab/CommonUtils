/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import au.com.tyo.io.FileUtils.Progress;

public class StreamUtils implements IOConstants {
	
	public static String GzipStreamToString(InputStream is) {
		InputStream gzipStream = null;
		InputStreamReader reader = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();

		try {
			gzipStream = new GZIPInputStream(is);	
			
			reader = new InputStreamReader(gzipStream);
			in = new BufferedReader(reader);

			String line;

			while ((line = in.readLine()) != null) 
			    sb.append(line);

			/*
			 * don't use IO readFileIntoBytes on compressed content
			 */
//			String text = new String(IO.readFileIntoBytes(gzipStream), "UTF-8");
			
//			String name = f.getName();

		} catch (FileNotFoundException e) {
			e.printStackTrace();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e1) {
				}
			
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e1) {
				}
			
			if (gzipStream != null)
				try {
					gzipStream.close();
				} 
				catch (IOException e) {
				}
		}
		return sb.toString();
	}

	public static void GzipStreamToOutputStream(InputStream in, OutputStream out, Progress progress, long fileSize) {
		GZIPInputStream gzis = null;

		try {
			gzis = new GZIPInputStream(in);
			
			writeToStreamWithProgress(gzis, out, progress, fileSize);
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (gzis != null)
				try {
					gzis.close();
				} 
				catch (IOException e) {
				}
			
			if (out != null)
				try {
					out.close();
				} catch (IOException e1) {
				}
			
			
			if (in != null)
				try {
					in.close();
				} catch (IOException e1) {
				}
		}
	}
	
	public static void writeToStreamWithProgress(InputStream in, OutputStream out, Progress progress, long fileSize) throws IOException {
		double percent = 0.0f;
		byte[] buffer = new byte[BUFFER_SIZE];
        int byteRead = 0;
		int len;
        while ((len = in.read(buffer)) > 0) {
        	out.write(buffer, 0, len);
        	
            if (progress != null && fileSize > 0) {
            	byteRead += len;
            	if (byteRead >= fileSize)
            		percent = 1;
            	else
            		percent = (double)byteRead / (double)fileSize;
            	progress.infoProgress(Double.valueOf((percent  * 100)).intValue());
            }
        }
	}
	
	
}

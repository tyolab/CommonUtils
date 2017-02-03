/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class IO {

	public static final int BUFFER_SIZE = 4096;
	
	// In android, 
    // We guarantee that the available method returns the total
    // size of the asset...  of course, this does mean that a single
    // asset can't be more than 2 gigs.
	static public byte[] readFileIntoBytes(InputStream is) throws IOException {
	    int size;
	    byte[] bytes = null;
//		try {
			size = is.available();
		    bytes = new byte[size];
		    is.read(bytes, 0, size);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return bytes;
	}
	
	static public byte[] readFileIntoBytes(File file) {
	    byte[] bytes = null;
	    FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		    bytes = readFileIntoBytes(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (null != fis)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return bytes;
	}
	
	/*
	 * TODO charsets
	 */
	static public byte[] readFileIntoBytes(String file) {
		return readFileIntoBytes(new File(file));
	}
	
	static String readFileIntoString(String file) {
		String content = null;
		try {
			content = new String(readFileIntoBytes(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static void writeFile(String filename, byte[] bytes) {
		writeFile(filename, bytes, "UTF-8");
	}

	public static void writeFile(String filename, byte[] bytes, String charset) {
        FileOutputStream out = null;
		try {
//			out = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream(bytes), charset));
			out = new FileOutputStream(filename);
	        out.write(bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally { 
		    try { if (out != null ) out.close(); }
		    catch(Exception ex) {} 
		}
	}
	
	public static void writeFile(File file, String content) {
		writeFile(file, content, "UTF-8");
	}
	
	public static void writeFile(File file, String content, String charset) {
        BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
	        out.write(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally { 
		    try { if (out != null ) out.close(); }
		    catch(Exception ex) {} 
		}
	}
	
	public static byte[] inputStreamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[BUFFER_SIZE];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}

	public static long writeFile(String tempFile, InputStream is) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(tempFile));

        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = is.read(buf)) > 0) {
            fos.write(buf, 0, n);
            nread += n;
        }
        return nread;
	}
}

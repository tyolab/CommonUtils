/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import au.com.tyo.io.FileUtils.Progress;

/**
 * Code from:
 * 
 * <a href="http://stackoverflow.com/questions/6717165/how-can-i-zip-and-unzip-a-string-using-gzipoutputstream-that-is-compatible-with">How to</a>
 * 
 */

public class GZIP {

	public static byte[] compress(String string) throws IOException {
	    return compress(string.getBytes());
	}
	
	public static byte[] compress(byte[] bytes) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
	    GZIPOutputStream gos = new GZIPOutputStream(os);
	    gos.write(bytes);
	    gos.close();
	    byte[] compressed = os.toByteArray();
	    os.close();
	    return compressed;
	}
	
	public static InputStream decompressStream(InputStream input) throws IOException {
		PushbackInputStream pb = new PushbackInputStream(input, 2); //we need a pushbackstream to look ahead
		byte[] signature = new byte[2];
		pb.read(signature); //read the signature
		pb.unread(signature); //push back the signature to the stream
		if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b) { //check if matches standard gzip magic number
			return new GZIPInputStream(pb);
		}
		return pb;
	}

	public static String decompress(byte[] compressed) throws IOException {
//	    final int BUFFER_SIZE = 32;
	    ByteArrayInputStream is = new ByteArrayInputStream(compressed);
//	    GZIPInputStream gis = new GZIPInputStream(is);
//	    GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
//	    StringBuilder string = new StringBuilder();
//	    byte[] data = new byte[BUFFER_SIZE];
//	    int bytesRead;
//	    while ((bytesRead = gis.read(data)) != -1) {
//	        string.append(new String(data, 0, bytesRead));
//	    }
//	    gis.close();
//	    is.close();
//	    return string.toString();
	    return StreamUtils.GzipStreamToString(is);
	}
	
	public static void decompressToFile(String fileName, String outName) throws FileNotFoundException {
		decompressToFile(new File(fileName), new File(outName), null, -1);
	}
	
	public static void decompressToFile(File file, File outFile, Progress progress, long fileSize) throws FileNotFoundException {
		StreamUtils.GzipStreamToOutputStream(new FileInputStream(file), new FileOutputStream(outFile), progress, fileSize);
	}
	
	/*
	=== Update ===

	If you need .Net compability my code has to be changed a little:
	
	
	final String text = "hello";
	try {
	    byte[] compressed = compress(text);
	    for (byte character : compressed) {
	        Log.d("test", String.valueOf(character));
	    }
	    String decompressed = decompress(compressed);
	    Log.d("test", decompressed);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	*/

/*	public static byte[] compress(String string) throws IOException {
	    byte[] blockcopy = ByteBuffer
	        .allocate(4)
	        .order(java.nio.ByteOrder.LITTLE_ENDIAN)
	        .putInt(string.length())
	        .array();
	    ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
	    GZIPOutputStream gos = new GZIPOutputStream(os);
	    gos.write(string.getBytes());
	    gos.close();
	    os.close();
	    byte[] compressed = new byte[4 + os.toByteArray().length];
	    System.arraycopy(blockcopy, 0, compressed, 0, 4);
	    System.arraycopy(os.toByteArray(), 0, compressed, 4, os.toByteArray().length);
	    return compressed;
	}

	public static String decompress(byte[] compressed) throws IOException {
	    final int BUFFER_SIZE = 32;
	    ByteArrayInputStream is = new ByteArrayInputStream(compressed, 4, compressed.length - 4);
	    GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
	    StringBuilder string = new StringBuilder();
	    byte[] data = new byte[BUFFER_SIZE];
	    int bytesRead;
	    while ((bytesRead = gis.read(data)) != -1) {
	        string.append(new String(data, 0, bytesRead));
	    }
	    gis.close();
	    is.close();
	    return string.toString();
	}*/
	
}

/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class IO {

	public static final int BUFFER_SIZE = 4096 * 4;

    /**
     *
     *
     * @param fileIn
     * @return
     * @throws Exception
     */
    public static Object readObject(InputStream fileIn) throws Exception {
        Object target = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(fileIn);
            target = (Object) in.readObject();
        }
        finally {
            try {
                if (in != null ) in.close();
                if (fileIn != null ) fileIn.close();
            }
            catch (Exception e) {
                throw e;
            }
        }
        return target;
    }

	/**
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Object readObject(File file) throws Exception {

		FileInputStream fileIn = null;
        fileIn = new FileInputStream(file);

        Object obj = readObject(fileIn);
        return obj;
	}

	/**
	 * 	// In android,
	 // We guarantee that the available method returns the total
	 // size of the asset...  of course, this does mean that a single
	 // asset can't be more than 2 gigs.
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 */
	static public byte[] readFileIntoBytes(InputStream is) throws IOException {
	    int size;
	    byte[] bytes = null;

		size = is.available();
		bytes = new byte[size];
		is.read(bytes, 0, size);
		return bytes;
	}

	/**
	 * For input stream of unknown size
	 *
	 * @param is
	 * @return
	 */
	static public byte[] inputStreamToByteArray(InputStream is) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int len;
		while ((len = is.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, len);
		}

		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

    /**
     *
     * @param file
     * @return
     */
	static public byte[] readFileIntoBytes(File file) throws IOException {
	    byte[] bytes = null;
	    FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		    bytes = readFileIntoBytes(fis);
		} catch (IOException e) {
			throw e;
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
	
	/**
	 * TODO
     * charsets
	 */
	static public byte[] readFileIntoBytes(String file) throws IOException {
		return readFileIntoBytes(new File(file));
	}

	/**
	 *
	 * @param file
	 * @return
	 */
	static String readFileIntoString(String file) throws IOException {
		String content = null;
        content = new String(readFileIntoBytes(file), "UTF-8");

		return content;
	}

	/**
	 *
	 * @param filename
	 * @param bytes
     */
	public static void writeFile(String filename, byte[] bytes) throws IOException {
		writeFile(new File(filename), bytes);
	}

    /**
     *
     * @param file
     * @param bytes
     */
	public static void writeFile(File file, byte[] bytes) throws IOException {
        FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
	        out.write(bytes);
		}
		catch (IOException e) {
            throw e;
		}
		finally { 
		    try { if (out != null ) out.close(); }
		    catch(Exception ex) {} 
		}
	}

	/**
	 *
	 * @param file
	 * @param content
	 */
	public static void writeFile(File file, String content) throws IOException {
		writeFile(file, content, "UTF-8");
	}

	/**
	 *
	 * @param file
	 * @param content
	 * @param charset
	 */
	public static void writeFile(File file, String content, String charset) throws IOException {
        BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
	        out.write(content);
		} catch (Exception e) {
		    throw e;
		} 
		finally { 
		    try { if (out != null ) out.close(); }
		    catch(Exception ex) {} 
		}
	}

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
	public static byte[] inputStreamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		long nRead = pipe(is, buffer);

		buffer.flush();

		return buffer.toByteArray();
	}

	/**
	 *
	 * @param tempFile
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static long writeFile(String tempFile, InputStream is) throws IOException {
		return writeFile(new File(tempFile), is);
	}

    /**
     *
     * @param tempFile
     * @param is
     * @return
     * @throws IOException
     */
	public static long writeFile(File tempFile, InputStream is) throws IOException {
        if (null == is)
            return 0;

        FileOutputStream outputStream = new FileOutputStream(tempFile);

        long nread = pipe(is, outputStream);

        outputStream.close();
        return nread;
	}

	/**
	 *
	 * @param tempFile
	 * @param os
	 * @throws IOException
	 */
    public static void writeFile(File tempFile, ByteArrayOutputStream os) throws IOException {
        OutputStream outputStream = new FileOutputStream(tempFile);
        os.writeTo(outputStream);
    }

	/**
	 *
	 * @param target
	 * @param file
	 * @throws Exception
	 */
	public static void writeObject(Object target, File file) throws Exception {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try
		{
			fileOut = new FileOutputStream(file);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(target);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			try {
				if (out != null )
					out.close();
				if (fileOut != null)
					fileOut.close();
			}
			catch(Exception ex) {
				throw ex;
			}
		}
	}

    public static InputStream bytesAsInputStream(byte[] bytes) {
		return new ByteArrayInputStream(bytes);
    }

	/**
	 *
	 * @param file
	 * @return
	 */
	public static String readText(File file) throws IOException {
		return new String(readFileIntoBytes(file));
    }

	/**
	 * Pipe from in to out, without closing any of that at the end
	 *
	 * @param inputStream
	 * @param outputStream
	 * @return
	 * @throws IOException
	 */
	public static long pipe(InputStream inputStream, OutputStream outputStream) throws IOException {
		long nread = 0L;
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = inputStream.read(buf)) > 0) {
			outputStream.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}

	public static long pipe(File srcdFile, File dstFile) throws IOException {
		return pipe(new FileInputStream(srcdFile), new FileOutputStream(dstFile));
	}
}

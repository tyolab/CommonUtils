/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * the below code is from:
 * 
 *  http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
 */

public class FileUtils implements IOConstants {
	
	public interface Progress {
		void infoProgress(Integer progress);
	}
	
	public static void copyFile(byte[] bytes, File destFile) throws IOException {
		InputStream is = new ByteArrayInputStream(bytes);
		copyFile(is, destFile, null, 0);
	}
	
	public static void copyFile(InputStream is, File destFile) throws IOException {
		copyFile(is, destFile, null, 0);
	}
	
	/*
	 * progress: 0 - 100
	 */
	public static void copyFile(InputStream is, File destFile, Progress progress, int fileSize) throws IOException {
		 if(!destFile.exists()) {
			  destFile.createNewFile();
		 }
		 
//		try {  
		    OutputStream os = new FileOutputStream(destFile, false);  
		    try {
		        byte[] buffer = new byte[BUFFER_SIZE];
		        int byteRead = 0;
		        double percent = 0.0f;
		        for (int n; (n = is.read(buffer)) != -1; ) {
		            os.write(buffer, 0, n); 
		            if (progress != null && fileSize > 0) {
		            	byteRead += n;
		            	if (byteRead >= fileSize)
		            		percent = 1;
		            	else
		            		percent = (double)byteRead / (double)fileSize;
		            	progress.infoProgress(Double.valueOf((percent  * 100)).intValue());
		            }
		        }
		        
		        if (progress != null)
		        	progress.infoProgress(100);
		    }
		    finally { 
		    	if (os != null) os.close(); 
		    }  
//		}
		/* inut stream shouldn't be closed here */
//		finally { 
//			if is.close(); 
//		}  
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		 if(!destFile.exists()) {
			  destFile.createNewFile();
		 }

		 FileChannel source = null;
		 FileChannel destination = null;
		 try {
			  source = new FileInputStream(sourceFile).getChannel();
			  destination = new FileOutputStream(destFile).getChannel();
			  destination.transferFrom(source, 0, source.size());
		 }
		 finally {
			  if(source != null) {
				   source.close();
			  }
			  if(destination != null) {
				   destination.close();
			  }
		 }
	}
	
	public static void copyFile(String sourceFile, String destFile) throws IOException {
		copyFile(new File(sourceFile), new File(destFile));
	}
	
	public static void copyPiece(String sourceFile, String destFileStr, int pieceSize, Progress progress) {
		try {
			RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
			long offset = raf.length() - pieceSize;
			
			File destFile = new File(destFileStr);
			 if(!destFile.exists()) {
				  destFile.createNewFile();
			 }
			 
		    OutputStream os = new FileOutputStream(destFile, false);  
		    try {
		        byte[] buffer = new byte[BUFFER_SIZE];
		        int byteRead = 0;
		        double percent = 0.0f;
		        raf.seek(offset);
		        
		        for (int n; (n = raf.read(buffer)) != -1; ) {
		            os.write(buffer, 0, n); 
		            if (progress != null && pieceSize > 0) {
		            	byteRead += n;
		            	if (byteRead >= pieceSize)
		            		percent = 1;
		            	else
		            		percent = (double)byteRead / (double)pieceSize;
		            	progress.infoProgress(Double.valueOf((percent  * 100)).intValue());
		            }
		        }
		        
		        if (progress != null)
		        	progress.infoProgress(100);
		    }
		    finally { 
		    	if (os != null) os.close(); 
		    }  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void moveFile(String sourceFile, String destFile) throws IOException {
		moveFile(new File(sourceFile), new File(destFile));
	}
	
	public static void moveFile(File sourceFile, File destFile) throws IOException {
		copyFile(sourceFile, destFile);
		
		sourceFile.delete();
	}
	 
	public static void writeFile(File file, String content) {
		writeFile(file, content, "UTF-8");
	}
	
	public static void writeFile(File file, String content, String charset) {
        BufferedWriter out;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
	        out.write(content);
	        out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}
	
	public static String increaseFileNumber(String name) {
		return increaseFileNumber(name, false);
	}
	
	public static String increaseFileNumber(String name, boolean first) {
		String filename = name;
		int pos;
		if (first)
			pos = filename.indexOf(".");
		else
			pos = filename.lastIndexOf(".");
		String ext = "";
	
		if (pos < 0)
			pos = filename.length();
		else
			ext = filename.substring(pos);
			//--pos;

		int i;
		String newfile;
		for (i = pos - 1; i > 0; --i)
			if (!Character.isDigit(filename.charAt(i)))
				break;
		if (i != (pos - 1)) {
			++i;
			newfile = filename.substring(0, i);
		}
		else
			newfile = filename.substring(0, pos);

		int number = 0;
		String str_num = filename.substring(i, pos);
		if (str_num.length() > 0 && Character.isDigit(str_num.charAt(0)))
			number = Integer.valueOf(str_num);
		else
			number = 0;
		++number;

		return newfile + number + ext;
	}

	/**
	 *
	 * @param fileName
     */
	public static void delete(String fileName) throws FileNotFoundException {
		delete(new File(fileName));
	}

	/**
	 *
	 * @param f
     */
	public static void delete(File f) throws FileNotFoundException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}

		if (!f.delete()) throw new FileNotFoundException("Failed to delete file: " + f);
	}

	public static List<File> sortByLastModified(List files) {
		return sortByLastModified(files, false);
	}

	public static List<File> sortByLastModified(List files, boolean accendingOrder) {
		Collections.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				long m1 = f1.lastModified();
				long m2 = f2.lastModified();

				int v = accendingOrder ? 1 : -1;

				if (m1 == m2)
					return 0;
				else if (m1 > m2)
					return v * -1;
				return v;
			}
		});
		return files;
	}
}

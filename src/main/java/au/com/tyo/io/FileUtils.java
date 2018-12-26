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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * some code below was from:
 * 
 *  http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
 */

public class FileUtils implements IOConstants {

	/**
	 * The progress interface
	 */
	public interface Progress {
		void infoProgress(Integer progress);

        void moveToNextStage();
    }

	/**
	 *
	 * @param fileName
	 * @return
	 */
    public static String[] getFileNameWithoutExtension(String fileName) {
		String[] name = new String[2];
		int pos = fileName.lastIndexOf('.');
		if (pos > 0) {
			name[0] = fileName.substring(0, pos);
			if (pos < (fileName.length() - 1))
				name[1] = fileName.substring(pos);
		}
		else {
			name[0] = fileName;
		}
		return name;
    }

	/**
	 *
	 * @param bytes
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(byte[] bytes, File destFile) throws IOException {
		InputStream is = new ByteArrayInputStream(bytes);
		copyFile(is, destFile, null, 0);
	}

	/**
	 *
	 * @param is
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(InputStream is, File destFile) throws IOException {
		copyFile(is, destFile, null, 0);
	}
	
	/**
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

    /**
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFileIfNotTheSame(String sourceFile, String destFile) throws IOException {
        File sourceFileHandle = new File(sourceFile);
        File destFileHandle = new File(destFile);

        if (!destFileHandle.exists() || sourceFileHandle.length() != destFileHandle.length())
            FileUtils.copyFile(sourceFileHandle, destFileHandle);
    }

    /**
     *
     * @param sourceFile
     * @param destFile
     * @param progress
     * @throws IOException
     */
    public static void copyPieceIfNotTheSame(String sourceFile, String destFile, Progress progress) throws IOException {
        File sourceFileHandle = new File(sourceFile);
        File destFileHandle = new File(destFile);

        if (!destFileHandle.exists() || sourceFileHandle.length() != destFileHandle.length())
            FileUtils.copyPiece(sourceFileHandle, destFileHandle, 0, sourceFile.length(), progress);
    }

	/**
	 *
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
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

	/**
	 *
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(String sourceFile, String destFile) throws IOException {
		copyFile(new File(sourceFile), new File(destFile));
	}

    /**
     *
     * @param sourceFile
     * @param destFileStr
     * @param index
     * @param pieceSize, -1 the whole size
     * @param progress
     */
    public static void copyPiece(String sourceFile, String destFileStr, int index, int pieceSize, Progress progress) throws IOException {
        copyPiece(new File(sourceFile), new File(destFileStr), index, pieceSize, progress);
    }

    /**
     *
     * @param sourceFile
     * @param destFile
     * @param index
     * @param pieceSize
     * @param progress
     * @throws IOException
     */
	public static void copyPiece(File sourceFile, File destFile, int index, int pieceSize, Progress progress) throws IOException {
		try {
			RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
			long offset;

            /**
             * From the back, pieceSize is the size of the file we want to use
             */
			if (index < 0)
				offset = raf.length() - pieceSize;
			else
				offset = index;

			if (pieceSize < 0)
			    pieceSize = (int) raf.length();

			if ((offset + pieceSize) > raf.length())
				throw new IllegalStateException("The offset and piece size is not within the range of the package size");

			 if(!destFile.exists())
			 	 destFile.createNewFile();
			 else
			     destFile.delete();
			 
		    OutputStream os = new FileOutputStream(destFile, false);  
		    try {
		        byte[] buffer = new byte[BUFFER_SIZE];
		        int byteRead = 0;
		        double percent = 0.0f;

		        raf.seek(offset);

		        for (int n; (n = raf.read(buffer)) != -1; ) {
		            if ((byteRead + n) <= pieceSize) {
                        os.write(buffer, 0, n);
                        byteRead += n;
                    }
		            else {
		                int leftByteLen = pieceSize - byteRead;
                        os.write(buffer, 0, leftByteLen);
                        byteRead += leftByteLen;
                    }
		            if (progress != null) {
		            	if (byteRead >= pieceSize)
		            		percent = 100;
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
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 *
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void moveFile(String sourceFile, String destFile) throws IOException {
		moveFile(new File(sourceFile), new File(destFile));
	}

	/**
	 *
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void moveFile(File sourceFile, File destFile) throws IOException {
		copyFile(sourceFile, destFile);
		
		sourceFile.delete();
	}

	/**
	 *
	 * @param file
	 * @param content
	 */
	public static void writeFile(File file, String content) {
		writeFile(file, content, "UTF-8");
	}

	/**
	 *
	 * @param file
	 * @param content
	 * @param charset
	 */
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

	/**
	 *
	 * @param name
	 * @return
	 */
	public static String increaseFileNumber(String name) {
		return increaseFileNumber(name, false);
	}

	/**
	 *
	 * @param name
	 * @param first
	 * @return
	 */
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

	/**
	 *
	 * @param files
	 * @return
	 */
	public static List<File> sortByLastModified(List files) {
		return sortByLastModified(files, false);
	}

	/**
	 *
	 * @param files
	 * @param accendingOrder
	 * @return
	 */
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

	/**
	 *
	 * For Java 7+, or Android 26+
	 *
	 * @param fileName
	 * @param text
	 * @throws IOException
	 */
	public static void append(String fileName, String text) throws IOException {
		Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.APPEND);
	}

	/**
	 *
	 * @param fileName
	 * @param text
	 */
	public static void appendWith(String fileName, String text) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		out.println(text);
		out.close();
	}
}

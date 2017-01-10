/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Cache<FileType> {
	
	@SuppressWarnings("unchecked")
	public FileType read(File file) throws Exception {
		FileType target = null;
		FileInputStream fileIn = null;
        ObjectInputStream in = null;
        try {
			fileIn = new FileInputStream(file);
	        in = new ObjectInputStream(fileIn);
	        target = (FileType) in.readObject();
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        	try {
        		file.delete();
        	}
        	catch (Exception deleteEx) {}
        	throw ex;
        }
        finally {
        	try {
                if (in != null ) in.close();
                if (fileIn != null ) fileIn.close();
        	}
        	catch (Exception e) {}
        }
		return target;
	}
	
	public void write(FileType target, File file) throws Exception {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try
       {
			fileOut = new FileOutputStream(file);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(target);
       }
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally { 
			try {
				if (out != null )
					out.close();
				if (fileOut != null)
					fileOut.close();
			}
			catch(Exception ex) {} 
		}
	}
}

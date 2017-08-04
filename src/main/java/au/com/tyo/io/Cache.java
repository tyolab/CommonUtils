/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.File;

public class Cache<FileType> {
	
	@SuppressWarnings("unchecked")
	public FileType read(File file) throws Exception {
		FileType target = (FileType) IO.readObject(file);
		return target;
	}

	public void write(FileType target, File file) throws Exception {
		IO.writeObject(target, file);
	}

}

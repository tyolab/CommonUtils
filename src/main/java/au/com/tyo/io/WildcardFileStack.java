/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

public class WildcardFileStack extends WildcardFiles<File> implements Comparator<File> {

	public WildcardFileStack(File file) {
		this(file, "*");
	}
	
	public WildcardFileStack(File file, String pattern) {
		super(file, pattern);

		init(file, pattern);
	}

	public WildcardFileStack(String inputfile, String pattern) {
		this(new File(inputfile), pattern);
	}

	public WildcardFileStack(String inputfile) {
		this(new File(inputfile));
	}
	
	private void init(File file, String pattern) {
		if (file == null)
			return;

		if (file.isDirectory()) {
			inputFileDir = file;
			wildcard = pattern;
		}
		else {
			if (file.exists() && file.isFile()) {
				add(file);
				return;
			}

			inputFileDir = file.getParentFile();
			if (inputFileDir.exists() && inputFileDir.isDirectory())
				wildcard = file.getName();
			else
				return;
		}

		createPattern(wildcard);
	}

	public void listFiles() {
		this.listFilesInStack(this, this.inputFileDir);
	}
	
	public void sortByDate() {
		Collections.sort(this, this);
	}

	public File next() {
		File file = null;
		if (!empty()) {
			file = pop();
			if (file.isDirectory() && toListAllFiles) {
				this.listFilesInStack(this, file);
				file = next();
			}
		}
		return file;
	}

	@Override
	public int compare(File lhs, File rhs) {
		return rhs.lastModified() > lhs.lastModified() ? -1 : 1;
	}

	public void deleteAll() {
		synchronized (this) {
			boolean oldtoListAllFiles = toListAllFiles;
			boolean includSubfolders = includeAllSubfolders;

			// delete files in subdirectories only when we come to it
			includeAllSubfolders = false;
			toListAllFiles = true;
			listFiles();
			File file;
			while ((file = next()) != null) {
				file.delete();
			}
			toListAllFiles = oldtoListAllFiles;
			includeAllSubfolders = includSubfolders;
		}
	}

}

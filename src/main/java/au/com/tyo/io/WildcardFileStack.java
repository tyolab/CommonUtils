/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

public class WildcardFileStack extends WildcardFiles implements Comparator<File> {
	
	private Stack<File> stack;// = new Stack<File>();
	
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

		stack = new Stack<File>();

		if (file.isDirectory()) {
			inputFileDir = file;
			wildcard = pattern;
		}
		else {
			if (file.exists() && file.isFile()) {
				stack.add(file);
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
		this.listFilesInStack(stack, this.inputFileDir);
	}
	
	public void sortByDate() {
		Collections.sort(this.stack, this);
	}
	
//	private void listFiles(File parentDirHandler) throws IOException {
		
//		if (parentDirHandler.exists()) {
//		   File[] allFiles = parentDirHandler.listFiles();
//		    for (File f : allFiles) {
//		        if (includeAllSubfolders && f.isDirectory()) {
//		        	if (this.isToListAllFiles())
//		        		listFiles(f);
//		        	else
//		        		stack.push(f);
//		        }
//		        else if (accept(f))
//		        	stack.push(f);
//		    }
//		}
//		else
//			throw new IOException(inputFileDir + ": No such folder exists");
//	}

	public File next() {
		File file = null;
		if (!stack.empty()) {
			file = stack.pop();
			if (file.isDirectory() && toListAllFiles) {
				this.listFilesInStack(stack, file);
				file = next();
			}
		}
		return file;
	}
	
	public int size() {
		return stack.size();
	}

	@Override
	public int compare(File lhs, File rhs) {
		return rhs.lastModified() > lhs.lastModified() ? -1 : 1;
	}
	
	public File get(int index) {
		return stack.get(index);
	}
}

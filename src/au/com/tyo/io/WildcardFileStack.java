package au.com.tyo.io;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import au.com.tyo.io.WildcardFiles;

public class WildcardFileStack extends WildcardFiles implements Comparator<File> {
	
	private Stack<File> stack;// = new Stack<File>();
	
	public WildcardFileStack(File file) throws Exception {
		this(file, "*");
	}
	
	public WildcardFileStack(File file, String pattern) throws Exception {
		super(file, pattern);
		init(file, pattern);
	}
	
	public WildcardFileStack(String inputfile) throws Exception {
		super(inputfile);
		File file = new File(inputfile);
		if (file.getParentFile() != null)
			init(file.getParentFile(), file.getName());
		else 
			init(file, "*");	
	}
	
	private void init(File file, String pattern) {
		if (file == null)
			return;
		
		stack = new Stack<File>();
		if (file.isDirectory()) {
			inputFileDir = file;
			wildcard = "*";
		}
		else {
			if (file.exists() && file.isFile()) {
				stack.add(file);
				return;
			}
		}
		wildcard = pattern;
		createPattern(wildcard);
	}
	
	public WildcardFileStack(String inputfile, String pattern) throws Exception {
		this.setDirectory(inputfile);
		createPattern(pattern);
//		listInputFolder();
	}

	public void listFiles() throws Exception {
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
				try {
					this.listFilesInStack(stack, file);
					file = next();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
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

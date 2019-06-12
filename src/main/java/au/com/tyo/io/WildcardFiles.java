/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.tyo.io.WildcardFiles;

/*
 * Usage:
 *
	String[] arrFile = WildcardFiles.list(inputfiles);
  for (String onefile : arrFile) 
  {
  	String inputfile = WildcardFiles.getDirectory() + File.separator + onefile;
  	// do whatever you want with the input file...
  }
 */

public class WildcardFiles<T> extends Stack<T> implements FilenameFilter, FileFilter
{
	protected Pattern pattern = null;
	protected String wildcard = "";
	protected File inputFileDir; // = new File(".");
		
	protected boolean includeAllSubfolders = false;

	/**
	 * This is for listing all files including those in the sub(sub*)folders
	 */
	protected boolean toListAllFiles = false;

	protected boolean folderOnly;
	protected boolean fileOnly;

	protected Pattern folderPattern;

	/**
	 * If the file is a file, length has to be > 0
	 * If the file is a directory, the number of files inside it has to greater than zero
	 */
	protected boolean mustNotEmpty;

	private FileFilter fileFilter;
	
	public WildcardFiles(){
		this("*.*");
	}
	
	public WildcardFiles(String search)
	{
		this(new File("."), search);
	}
	
	public WildcardFiles(File file, String pattern) {
		inputFileDir = file;
		createPattern(pattern);

		fileFilter = null;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void setFolderOnly(boolean folderOnly) {
		this.folderOnly = folderOnly;
	}

	public void setFileOnly(boolean fileOnly) {
		this.fileOnly = fileOnly;
	}

	public boolean toListAllFiles() {
		return toListAllFiles;
	}

	public void setToListAllFiles(boolean toListAllFiles) {
		this.toListAllFiles = toListAllFiles;
	}
	
	public boolean toIncludeAllSubfolders() {
		return includeAllSubfolders;
	}

	public void setIncludeAllSubfolders(boolean includeAllSubfolders) {
		this.includeAllSubfolders = includeAllSubfolders;
	}

	protected void createPattern(String search) {
		String reform = search;
		//reform = reform.replaceAll("\\.", "\\.");  
		//reform = reform.replaceAll("\\?", "."); 
		reform = reform.replaceAll("\\*", ".*");
		
		pattern = Pattern.compile(reform);
	}
	
	public String getDirectory()
	{
		return inputFileDir.getAbsolutePath();
	}
	
	protected void breakFile(String inputfile) throws Exception
	{
		String dir;
		if (new File(inputfile).isDirectory()) {
			wildcard = "*";		
			dir = inputfile;
		}
		else {
			wildcard = inputfile;
			int lastIndex = 0;
			if ((lastIndex = inputfile.lastIndexOf(File.separator)) > -1) {
				dir = inputfile.substring(0, lastIndex);
				wildcard = inputfile.substring(lastIndex + 1);
				wildcard.trim();
				if (wildcard.length() == 0)
					wildcard = "*";
			}
			else {
				dir = ".";
				wildcard = inputfile;
			}
		}
		setDirectory(dir);
	}
	

	protected void setDirectory(String dir) throws Exception {
		inputFileDir = new File(dir);
		if (!inputFileDir.exists()){
			throw new Exception("No such file: " + inputFileDir);
		}
	}

    /**
     *
     * @param stack
     */
	public void listFilesInStack(Stack<File> stack) {
		this.listFilesInStack(stack, inputFileDir);
	}

    /**
     *
     * @param stack
     * @param fileDir
     */
	public long listFilesInStack(Stack<File> stack, File fileDir) {
		if (!fileDir.exists())
			return 0;
		
		if (pattern == null) 
			createPattern(wildcard);

		FileFilter aFilter;

		if (null != fileFilter)
			aFilter = fileFilter;
		else
			aFilter = this;

		// Nullable
	    File[] allFiles = fileDir.listFiles();
	    int count = allFiles.length;
	    if (null != allFiles)
			for (File f : allFiles) {
				if (includeAllSubfolders && f.isDirectory()) {
					// put the subfolder in the stack itself
					stack.push(f);

					if (this.toListAllFiles()) {
						count += listFilesInStack(stack, f);
					}
				}
				else if (aFilter.accept(f))
					stack.push(f);
				else
					--count;
			}
	    return count;
	}
	
	/**
	 * @return the wildcard
	 */
	public String getWildcard() {
		return wildcard;
	}

	/**
	 * @param wildcard the wildcard to set
	 */
	public void setWildcard(String wildcard) {
		this.wildcard = wildcard;
	}

	public File[] listFiles(File subfolder) 
	{
		File[] arrFile = subfolder.listFiles((FileFilter) this);
		return arrFile;
	}
	
	public String[] list(String inputfile) throws Exception 
	{
		breakFile(inputfile);
		String[] arrFile = inputFileDir.list(new WildcardFiles(wildcard));
		return arrFile;
	}
	
	private boolean match(String name) 
	{
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();		
	}
	
	@Override
	public boolean accept(File file)
	{
		if (fileOnly && !file.isFile())
			return false;
		if (folderOnly && !file.isDirectory())
			return false;

		if (mustNotEmpty) {
			if (file.isFile() && file.length() == 0)
				return false;
			if (file.isDirectory()) {
				WildcardFileStack stack = new WildcardFileStack(file);
				stack.listFiles();
				if (stack.size() == 0)
					return false;
			}
		}

		return match(file.getName());
	}

	@Override
	public boolean accept(File dir, String name) {
		return match(name);
	}

	public boolean isMustNotEmpty() {
		return mustNotEmpty;
	}

	public void setMustNotEmpty(boolean mustNotEmpty) {
		this.mustNotEmpty = mustNotEmpty;
	}
}

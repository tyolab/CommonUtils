/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
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

public class WildcardFiles implements FilenameFilter, FileFilter
{
	protected Pattern pattern = null;
	protected String wildcard = "";
	protected File inputFileDir = new File(".");
		
	protected boolean includeAllSubfolders = false;
	
	protected boolean toListAllFiles = false;
	
	//	protected static WildcardFiles wildcardfiles = null; //new WildcardFiles(wildcard);
	
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
	
//	private static void listFiles(String inputfile, Stack stack) 
//	{
//			File[] arrFile = new File(inputFileDir).listFiles((FileFilter)new WildcardFiles(wildcard));
//			stack.addAll(Arrays.asList(arrFile));
//	}
	
	protected void setDirectory(String dir) throws Exception {
		inputFileDir = new File(dir);
		if (!inputFileDir.exists()){
			throw new Exception("No such file: " + inputFileDir);
		}
	}
	
//	public Stack<File> list() throws Exception {
//		return listFilesInStack(inputFileDir);
//	}
	public void listFilesInStack(Stack<File> stack) throws Exception {
		this.listFilesInStack(stack, inputFileDir);
	}
	
	public void listFilesInStack(Stack<File> stack, File fileDir) throws Exception 
	{
//		Stack<File> stack = new Stack<File>();
//		File inputFileHandler = new File(inputFileDir2);
		if (!fileDir.exists())
			return;
		
		if (pattern == null) 
			createPattern(wildcard);
		
	   File[] allFiles = fileDir.listFiles();
	    for (File f : allFiles) {
	        if (includeAllSubfolders && f.isDirectory()) {
	        	if (this.toListAllFiles())
	        		listFilesInStack(stack, f);
	        	else
	        		stack.push(f);
	        }
	        else if (accept(f))
	        	stack.push(f);
	    }

//		if (includeAllSubfolders) {
//			File[] allFiles = inputFileDir.listFiles();
//			 for (File f : allFiles) {
//				if (includeAllSubfolders && f.isDirectory()) {
//					if (this.toListAllFiles())
//						listFilesInStack(stack, f);
//					else
//						stack.push(f);
//				}
//				else if (accept(f))
//					stack.push(f);
//			 }
//		}
//		else {
//			if (fileDir.isFile()) 
//				stack.add(fileDir);
////			File[] arrFile = inputFileDir.listFiles((FileFilter) this);
////			if (arrFile != null)
////				stack.addAll(Arrays.asList(arrFile));
//		}
//		if (fileDir.isFile()) {
//			stack.add(fileDir);
//		}
//		else if (fileDir.isDirectory() && includeAllSubfolders) {
//			stack.addAll(Arrays.asList(listFiles(fileDir)));
//		}
//		else {
//
//		}
//		return stack;
	}
	
//	public Stack<File> listFilesInStack(String inputfile) throws Exception 
//	{
////		breakFile(inputfile);
//		return listFilesInStack(new File(inputfile));
//	}
	
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
		return match(file.getName());
	}

	@Override
	public boolean accept(File dir, String name) {
		return match(name);
	}	
}

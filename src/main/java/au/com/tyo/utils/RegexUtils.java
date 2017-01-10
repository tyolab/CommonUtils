/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	public static final String QUOTED_ENTITY1 = "\"([^\"\\]*(?:\\.[^\"\\]*)*)\"";
	public static final String QUOTED_ENTITY2 = "((\").*(\")|(').*('))";
	public static final String QUOTED_ENTITY3 = "\"([^\"]*)\"";
	public static final String NOTE_ENTITY = "\\(.*\\)";
	
	public static ArrayList<String> quotedEntitiesToArray(String what) {
		ArrayList<String> array = new ArrayList<String>();
		Pattern pattern = Pattern.compile(QUOTED_ENTITY2);
		
		Matcher matcher = pattern.matcher(what);
		
		while (matcher.find())
			array.add(what.substring(matcher.start(), matcher.end()).replaceAll("[^a-zA-Z0-9]", " ").trim());
		return array;
	}
	
	public static String removeNotes(String what) {
//		Pattern pattern = Pattern.compile(NOTE_ENTITY);
//		Matcher matcher = pattern.matcher(what);
//		matcher.replaceAll("");
		return what.replaceAll(NOTE_ENTITY, "");
	}
}

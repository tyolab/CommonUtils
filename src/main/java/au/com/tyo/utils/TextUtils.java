/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.util.ArrayList;

public class TextUtils {
	
	public static final String FOOT_NOTE_MARK_REGEX = "(((\\[)(\\s+|))(\\d+)((\\s+|)(\\])))";
	
	public static String ellipsize(String input, int maxCharacters) {
		return ellipsize(input, maxCharacters, 0);
	}
	
	/**
	 * Puts ellipses in input strings that are longer than than maxCharacters. Shorter strings or
	 * null is returned unchanged.
	 * @param input the input string that may be subjected to shortening
	 * @param maxCharacters the maximum characters that are acceptable for the unshortended string. Must be at least 3, otherwise a string with ellipses is too long already.
	 * @param the number of characters that should appear after the ellipsis (0 or larger) 
	 */
	public static String ellipsize(String input, int maxCharacters, int charactersAfterEllipsis) {
		if(maxCharacters < 3) {
			throw new IllegalArgumentException("maxCharacters must be at least 3 because the ellipsis already take up 3 characters");
		}
		
		if(maxCharacters - 3 < charactersAfterEllipsis) {
			throw new IllegalArgumentException("charactersAfterEllipsis must be less than maxCharacters");
		}
		
		if (input == null || input.length() < maxCharacters) {
			return input;
		}
		
		return input.substring(0, maxCharacters - 3 - charactersAfterEllipsis) + "..." + (charactersAfterEllipsis > 0 ? input.substring(input.length() - charactersAfterEllipsis) : "");
	}
	
	public static ArrayList<String> buildQueries(String what) {
		ArrayList<String> queries = new ArrayList<String>();
		queries.addAll(RegexUtils.quotedEntitiesToArray(what));
		
		String whatLeft = null;
//		if (queries.size() > 0)
//			for (String text : queries) 
//				whatLeft = what.replaceAll(text, " ");
//		else
			whatLeft = what;

		String[] words = whatLeft.split("[^\\w]+");
		/*
		 * slow algorithm
		 */
//		for (int i = words.length ; i > 0; --i) {
//			StringBuffer temp = new StringBuffer();
//			for (int j = 0; j < i; ++j) {
//				if (temp.length() > 0)
//					temp.append(" ");
//				temp.append(words[j]);
//			}
//			queries.add(temp.toString());
//		}
		ArrayList<String> list = new ArrayList<String>(); 
		for (int i = 0; i < words.length; ++i) {
			if (i == 0) {
				list.add(words[i]);
			}
			else {
				list.add(0, list.get(0) + " " + words[i]);
			}
		}
		
		queries.addAll(list);
		return queries;
	}
	
	public static String removeNotes(String what) {
		/*
		 * new StringBuffer(StringUtils.escapeHtml(what))
		 * 
		 * I dont understand why escaping it?, so remove it for now to make sure
		 * nothing goes wrong
		 */
		StringBuffer after = new StringBuffer(what);
		
		int index = 0;
		while ((index = after.indexOf("(", index)) > -1) {
			int end = -1;
			if (index > -1)
				end = after.indexOf(")", index);
			if (end > index)
				after.replace(index, end + 1, "");
			else
				break;
		}
		return after.toString();
	}
	
	public static String removeFootNoteMarks(String what) {
		return what.replaceAll(FOOT_NOTE_MARK_REGEX, "");
	}
	
	public static String removeSpaces(String abs) {
		return abs.replaceAll("(\\s+|\\s)", ""); 
	}
 	
	public static String removeSpaces(String abs, int howMany) {
		return abs.replaceAll("[\\s]{" + String.valueOf(howMany) + ",}", " ").replaceAll("(\\s+),", ",").replaceAll("(\\s+)\\.", ".");
	}
	
	public static String removeExtraSpaces(String abs) {
		// if more than 2
		return removeSpaces(abs, 2);
	}

	public static String padRightWith(String s, char with, int n) {
		if (s.length() > n)
			return s;
		return String.format("%1$-" + n + "s", s).replace(' ', with);
	}

	public static String padLeftWith(String s, char with, int n) {
		if (s.length() > n)
			return s;
		return String.format("%1$" + n + "s", s).replace(' ', with);
	}
//	public static boolean isWithinDistance(List<Integer> list, int distance) {
//		int array = list.toArray();
//		Arrays.sort(list);
//	}
}

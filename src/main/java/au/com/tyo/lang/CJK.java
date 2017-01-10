/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.lang;

public class CJK {
	
	public static boolean isCJKLangCode(String langCode) {
		if (langCode == null || langCode.length() == 0)
			return false;
		String twoChars = langCode.substring(0, 2);
		return twoChars.equals("zh") || twoChars.equals("ja") || twoChars.equals("ko");
	}
	
	public static String removeSpacesBetweenChinese(String from) {
		if (from.length() == 0)
			return "";
		
		StringBuffer sb = new StringBuffer();
		
		char pre, current, next;
		pre = from.charAt(0);
		if (!Character.isWhitespace(pre))
			sb.append(pre);
		for (int i = 1; i < from.length(); ++i) {
			current = from.charAt(i);
			
			if ((i + 1) < from.length())
				next = from.charAt(i + 1);
			else
				next = ' ';
			
			if (Character.isWhitespace(current)) {
				if (!(Unicode.isChinese(pre) || Unicode.isChinese(next)) && !(Character.isWhitespace(pre) || Character.isWhitespace(next)))
						sb.append(current);
			}
			else
				sb.append(current);
//			if (!( &&
//					(Unicode.isChinese(pre) || Character.isWhitespace(pre) ) || 
//					(Unicode.isChinese(next) || Character.isWhitespace(next))))
//				sb.append(current);
			
			pre = current;
		}
		return sb.toString();
	}
	
}

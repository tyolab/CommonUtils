/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.lang;

public class Unicode {
	public static final String FULL_STOP = "。։۔܁܂።᙮᠃᠉꓿꘎︒｡";
	
	public static final String FULL_STOP_ENGLISH = ".﹒．";
	
	public static final String FULL_STOP_ALL = FULL_STOP_ENGLISH + FULL_STOP;
	
	public static final String QUESTION_MASK = "?？؟";

	
	public static boolean isChinese(char aChar) {
		return isChinese(Character.UnicodeBlock.of(aChar));
	}		
	
	public static boolean isChinese(Character.UnicodeBlock cb) {
		return  cb == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
				cb == Character.UnicodeBlock.CJK_COMPATIBILITY ||
						cb == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS ||
								cb == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT ||
										cb == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT ||
												cb == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
														cb == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
																cb == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B /*||
																		cb == Character.UnicodeBlock.CJK_ ||*/
				;
	}
	
	public static boolean isKorean(char aChar) {
		return isKorean(Character.UnicodeBlock.of(aChar));
	}
	
	public static boolean isKorean(Character.UnicodeBlock cb) {
		return cb == Character.UnicodeBlock.HANGUL_JAMO ||
				cb == Character.UnicodeBlock.HANGUL_SYLLABLES ||
				cb == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
	}
	
	public static boolean isJapanese(char aChar) {
		return isJapanese(Character.UnicodeBlock.of(aChar));
	}
	
	public static boolean isJapanese(Character.UnicodeBlock cb) {
		return cb == Character.UnicodeBlock.HIRAGANA ||
				cb == Character.UnicodeBlock.KATAKANA ||
				cb == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS;
	}
	
	public static boolean containsChinese(String term) {
		/* 
		 * unimplemented yet
		 */
		return false;
	}
	
	public static String replaceFullStopWith(String source, String withBefore, String withAfter, boolean keepOrigin) {
		StringBuffer buf = new StringBuffer();
		int len = source.length();
		if (len < 2)
			return source;
		
		char preChar = source.charAt(0);
		char c;
		char nextNextChar = ' ';
		
		buf.append(preChar);
		
		int i = 1;
		for (; i < len; ++i) {
			c = source.charAt(i);
//			nextChar = source.charAt(i + 1);
			int j = i + 1;
			nextNextChar = ' ';
			while (j < len) {
				nextNextChar = source.charAt(j);
				if (!Character.isWhitespace(nextNextChar))
					break;
				++j;
			}
			if (((c == '.' || c == '﹒'|| c == '．') && !Character.isUpperCase(preChar) && Character.isUpperCase(nextNextChar)) 
					|| FULL_STOP.indexOf(c) > -1) {
				buf.append(withBefore);
				if (keepOrigin)
					buf.append(c);
				buf.append(withAfter);
			}
			else
				buf.append(c);
			preChar = c;
		}
		
		return buf.toString();
	}
	
	public static int backToBeginning(String text, int pos) {
		return backToBeginning(text, pos, "");
	}
	
	public static int backToBeginning(String text, int pos, String extra) {
		String stops = FULL_STOP_ALL + extra;
		while (pos > 0) {
			if (stops.indexOf(text.charAt(pos)) > -1)
				break;
			--pos;
		}
		return pos;
	}
	
	public static int toFullStop(String text) {
		return toFullStop(text, 0);
	}
	
	public static int toFullStop(String text, int pos) {
		while (pos < (text.length() - 1)) {
			if (FULL_STOP_ALL.indexOf(text.charAt(pos)) > -1)
				break;
			++pos;
		}
		return pos;
	}

	public static boolean isCJKCharacter(char aChar) {
		Character.UnicodeBlock cb = Character.UnicodeBlock.of(aChar);
		return isChinese(cb) || isJapanese(cb) || isKorean(cb);
	}
}

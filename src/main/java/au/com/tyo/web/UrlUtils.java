/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.web;

public class UrlUtils {
	
	public static String extractDomain(String url) {
		String domain = "";
		int pos = 0;
		int tmpPos = 0;
		
		if ((tmpPos = url.indexOf("://")) > -1) {
			pos = tmpPos + 3;
		}
		
//		if ((tmpPos = url.indexOf(WIKIPEDIA_MOBILE_DOMAIN, pos)) > -1) {
//			pos = tmpPos + WIKIPEDIA_MOBILE_DOMAIN.length();
//		}
		
		if ((tmpPos = url.indexOf('.', pos)) > -1) {
			domain = url.substring(pos, tmpPos);
		}
		return domain;
	}

}

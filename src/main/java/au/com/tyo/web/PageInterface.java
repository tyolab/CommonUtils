/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.web;

public interface PageInterface {

	String getTitle();

	String createHtmlContent();

	String createStyleAndScript();

	String getThemeName();

	String createTitle();

    PageInterface getXPage();

	String getLangCode();
}

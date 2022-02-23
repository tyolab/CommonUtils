/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.web;

import java.io.File;

import au.com.tyo.CommonSettings;

public class PageBuilder {

    public static String htmlTemplate;

	public static final String HTML_SECTION_DIV_END = "</div>\n";
	public static final String HTML_DIV_END = "</div>\n";
	
	public static final String ASSETS_PATH_ANDROID = "file:///android_asset";

	public static final String HTML_STATIC_PATH = "tyokiie";

	public static final String HTML_ASSETS_ANDROID = ASSETS_PATH_ANDROID + File.separator + HTML_STATIC_PATH + File.separator;
	
	public static String html_page_parameters = ""; 
	
	protected String html_header_content;
	
	protected String html_title_div;

	private static String html_section_div_end;

	static {
		PageBuilder.html_section_div_end = HTML_SECTION_DIV_END;
	}
	
	public PageBuilder() {
		html_title_div = null;
		html_header_content = null;
	}

	public static void setHtmlSectionDivEnd(String html_section_div_end) {
		PageBuilder.html_section_div_end = html_section_div_end;
	}

	public static String getAndroidAssetPath() {
		return ASSETS_PATH_ANDROID;
	}

	public static String getHtmlStaticPath() {
		return HTML_STATIC_PATH;
	}

	public static String getHtmlAssetsAndroid() {
		return HTML_ASSETS_ANDROID;
	}

	public static String addAndroidAssetPath(String file) {
		return String.format("file:///android_asset/%s", file);
	}
	
	public static String createCss(String cssName) {
		return String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"> </link>\n", cssName);
	}
	
	public static String createJavaScript(String jsName) {
		return String.format("<script type=\"text/javascript\" src=\"%s\" ></script>\n", jsName);
	}

	public String toHtml(PageInterface page) {
		String template = getHtmlTemplate();
        if (null == template)
            return toHtmlWithEmbeddedTemplate(page);
        else {
			String temp = null;
			if (CommonSettings.isAndroid())
            	temp = template.replaceAll("tyokiie/", HTML_ASSETS_ANDROID);
			else
				temp = template;
			temp = String.format(temp,
					page.getLangCode(),
                    createHtmlAttributes(page),
                    page.createStyleAndScript(),
                    page.getTitle(),
                    page.createHtmlContent());
            return temp;
        }
	}

	public String toHtmlWithEmbeddedTemplate(PageInterface page) {
		StringBuffer sb = new StringBuffer("<!doctype html>\n");
		
		openHtml(sb, page);
		
		openHead(sb);
		
		appendMetaInfo(sb);
		
		appendStyleScripts(sb, page);
		
		/*
		 * TODO do something here to to add the javascripts
		 */
		
		appendTitle(sb, page);
		
		closeHead(sb);
		openBody(sb);

		sb.append(page.createHtmlContent());
		
		closeBody(sb);
		closeHtml(sb);
		return sb.toString();		
	}

	public static void appendMetaInfo(StringBuffer sb) {
		sb.append("<meta charset=\"utf-8\">\n");
	}

	public void closeDiv(StringBuffer sb, int stack) {
		for (int j = 0; j < stack; ++j)
			closeDiv(sb);
	}
	
	public void appendStyleScripts(StringBuffer sb, PageInterface page) {
		sb.append(page.createStyleAndScript());
	}
	
	public void openHead(StringBuffer sb) {
		sb.append("<head>\n");
		if (html_header_content != null && html_header_content.length() > 0)
			sb.append(html_header_content);
	}
	
	public static void openBody(StringBuffer sb) {
		sb.append("<body>\n");
	}
	
	public void appendTitle(StringBuffer sb, PageInterface page) {
		sb.append(page.createTitle());
//		if (title != null) {
//			if (html_title_div != null)
//				sb.append(String.format(html_title_div, title));
//			else
//				sb.append(String.format("<div id=\"title\">%s</div><br>", title));
//		}
	}
	
	public static void openHtml(StringBuffer sb, PageInterface page) {
		sb.append("<html");

		sb.append(createHtmlAttributes(page));
		
		sb.append(">\n");
	}

	public static String createHtmlAttributes(PageInterface page) {
        StringBuffer sb = new StringBuffer();
        sb.append(" xml:theme=\"" + (page.getThemeName() != null ? page.getThemeName() : "none") + "\"");

        sb.append(" xml:platform=\"" + CommonSettings.getOs() + "\"");

        sb.append(" xml:device=\"" + CommonSettings.getDevice() + "\"");

        sb.append(" xml:orientation=\"" + (CommonSettings.isLandscapeMode() ? "landscape" : "portrait") + "\"");

        sb.append(" xml:parameters=\"" + html_page_parameters + "\"");
        return sb.toString();
    }
	
	public static void closeHead(StringBuffer sb) {
		sb.append("</head>\n");
	}
	
	public static void closeBody(StringBuffer sb) {
		sb.append("</body>\n");
	}
	
	public static void closeHtml(StringBuffer sb) {
		sb.append("</html>\n");
	}

	public static void closeSectionDiv(StringBuffer sb) {
		sb.append(html_section_div_end);
	}

	public static void closeDiv(StringBuffer sb) {
		sb.append(HTML_DIV_END);
	}

    public static void setHtmlTemplate(String htmlTemplate) {
        PageBuilder.htmlTemplate = htmlTemplate;
    }

    public static String getHtmlTemplate() {
        return PageBuilder.htmlTemplate;
    }
}

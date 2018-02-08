/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import au.com.tyo.lang.Entities;
import au.com.tyo.lang.Unicode;

public class StringUtils {

    public static String exceptionStackTraceToString(Exception ex) {
        if (null == ex)
            return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString(); // stack trace as a string
    }

    /**
     *
     * @param c
     * @return
     */
	public static String arrayToString(Collection<?> c) {
		return arrayToString(c, ",");
	}

    /**
     *
     * @param c
     * @param separator
     * @return
     */
	public static String arrayToString(Collection<?> c, String separator) {
	    return arrayToString(c.toArray(), separator);
    }

    public static String arrayToString(Object[] c, String separator) {
		StringBuffer buffer = new StringBuffer();
		for (Object o : c) {
		    if (o == null)
		        continue;

		    if (o instanceof String && ((String) o).length() == 0)
		        continue;

			if (buffer.length() > 0)
				buffer.append(separator);
			buffer.append(o.toString());
		}
		return buffer.toString();
	}

	public static String join(Object[] c, String separator) {
	    return arrayToString(c, separator);
    }

    public static String join(String separator, Object... params) {
        return join(params, separator);
    }

	/*
	 * Can't remember which version is from
	 * 
	 * The following code is from Apache Common Lang library
	 * 
	 */
	/* ====================================================================
	 * The Apache Software License, Version 1.1
	 *
	 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
	 * reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions
	 * are met:
	 *
	 * 1. Redistributions of source code must retain the above copyright
	 *    notice, this list of conditions and the following disclaimer.
	 *
	 * 2. Redistributions in binary form must reproduce the above copyright
	 *    notice, this list of conditions and the following disclaimer in
	 *    the documentation and/or other materials provided with the
	 *    distribution.
	 *
	 * 3. The end-user documentation included with the redistribution, if
	 *    any, must include the following acknowledgement:
	 *       "This product includes software developed by the
	 *        Apache Software Foundation (http://www.apache.org/)."
	 *    Alternately, this acknowledgement may appear in the software itself,
	 *    if and wherever such third-party acknowledgements normally appear.
	 *
	 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
	 *    Foundation" must not be used to endorse or promote products derived
	 *    from this software without prior written permission. For written
	 *    permission, please contact apache@apache.org.
	 *
	 * 5. Products derived from this software may not be called "Apache"
	 *    nor may "Apache" appear in their names without prior written
	 *    permission of the Apache Software Foundation.
	 *
	 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
	 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
	 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
	 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
	 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
	 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
	 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
	 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
	 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
	 * SUCH DAMAGE.
	 * ====================================================================
	 *
	 * This software consists of voluntary contributions made by many
	 * individuals on behalf of the Apache Software Foundation.  For more
	 * information on the Apache Software Foundation, please see
	 * <http://www.apache.org/>.
	 */
	
    // HTML and XML
    //--------------------------------------------------------------------------
    /**
     * <p>Escapes the characters in a <code>String</code> using HTML entities.</p>
     *
     * <p>
     * For example:
     * </p> 
     * <p><code>"bread" & "butter"</code></p>
     * becomes:
     * <p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     * </p>
     *
     * <p>Supports all known HTML 4.0 entities, including funky accents.
     * Note that the commonly used apostrophe escape character (&amp;apos;)
     * is not a legal entity and so is not supported). </p>
     *
     * @param str  the <code>String</code> to escape, may be null
     * @return a new escaped <code>String</code>, <code>null</code> if null string input
     * 
     * @see #unescapeHtml(String)
     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     */
    public static String escapeHtml(String str) {
    	String result = null;
        try {
            StringWriter writer = new StringWriter ((int)(str.length() * 1.5));
            escapeHtml(writer, str);
            result = writer.toString();
        } catch (Exception ioe) {
            //should be impossible
//            throw new UnhandledException(ioe);
        }
		return result;
    }

    /**
     * <p>Escapes the characters in a <code>String</code> using HTML entities and writes
     * them to a <code>Writer</code>.</p>
     *
     * <p>
     * For example:
     * </p> 
     * <code>"bread" & "butter"</code>
     * <p>becomes:</p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     *
     * <p>Supports all known HTML 4.0 entities, including funky accents.
     * Note that the commonly used apostrophe escape character (&amp;apos;)
     * is not a legal entity and so is not supported). </p>
     *
     * @param writer  the writer receiving the escaped string, not null
     * @param string  the <code>String</code> to escape, may be null
     * @throws IllegalArgumentException if the writer is null
     * @throws IOException when <code>Writer</code> passed throws the exception from
     *                                       calls to the {@link Writer#write(int)} methods.
     * 
     * @see #escapeHtml(String)
     * @see #unescapeHtml(String)
     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     */
    public static void escapeHtml(Writer writer, String string) throws IOException {
        if (writer == null ) {
            throw new IllegalArgumentException ("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        Entities.HTML40.escape(writer, string);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.</p>
     *
     * <p>For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;"
     * will become "&lt;Fran&ccedil;ais&gt;"</p>
     *
     * <p>If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. "&amp;gt;&amp;zzzz;x" will
     * become "&gt;&amp;zzzz;x".</p>
     *
     * @param str  the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #escapeHtml(Writer, String)
     */
    public static String unescapeHtml(String str) {
    	String result = null;
        try {
            StringWriter writer = new StringWriter ((int)(str.length() * 1.5));
            unescapeHtml(writer, str);
            result = writer.toString();
        } catch (IOException ioe) {
            //should be impossible
//            throw new UnhandledException(ioe);
        }
		return result;
    }

    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.</p>
     *
     * <p>For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;"
     * will become "&lt;Fran&ccedil;ais&gt;"</p>
     *
     * <p>If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. "&amp;gt;&amp;zzzz;x" will
     * become "&gt;&amp;zzzz;x".</p>
     *
     * @param writer  the writer receiving the unescaped string, not null
     * @param string  the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the writer is null
     * @throws IOException if an IOException occurs
     * @see #escapeHtml(String)
     */
    public static void unescapeHtml(Writer writer, String string) throws IOException {
        if (writer == null ) {
            throw new IllegalArgumentException ("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        Entities.HTML40.unescape(writer, string);
    }

    /**
     * 
     * @param phrase
     * @return
     */
	public static ArrayList<String> phraseToList(String phrase) {
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < phrase.length(); ++i) {
			char c = phrase.charAt(i);
			if (Unicode.isChinese(c) || Character.isWhitespace(c)) {
				if (buffer.length() > 0) {
					list.add(buffer.toString());
					buffer.setLength(0);
				}
				
				if (Unicode.isChinese(c))
					list.add(String.valueOf(c));
			}
			else
				buffer.append(c);
		}
		if (buffer.length() > 0)
			list.add(buffer.toString());
		return list;
	}

    public static String nullToEmpty(Object str) {
        if (str instanceof Integer) {
            int value = (int) str;
            return -1 == value ? "" : String.valueOf(value);
        }
        return null == str ? "" : str.toString();
    }
}

/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.parser;

import java.io.File;

import au.com.tyo.io.IO;
import au.com.tyo.utils.ByteArrayKMP;

public class Sgml {
	protected byte[] content;
	protected int start;
	protected int end;
	
	protected SgmlTree sgmlTree;
	protected SgmlNode root;
	
	public Sgml() {
		
	}
	
//	public class Property {
//		String key;
//		String value;
//		
//		public String getKey() {
//			return key;
//		}
//		public void setKey(String key) {
//			this.key = key;
//		}
//		public String getValue() {
//			return value;
//		}
//		public void setValue(String value) {
//			this.value = value;
//		}
//	}
	public SgmlNode parse(File file) {
		return parse(new String(IO.readFileIntoBytes(file)));
	}
	
	public SgmlNode parse(String file) {
		this.content = file.getBytes();
		root = parse(content);
		sgmlTree = new SgmlTree(root);
		return root;
	}
	
	public SgmlNode parse(byte[] content) {
		return parse(content, 0);
	}
	
	public String toString() {
		return root.toString();
	}
	
	public static void main(String[] args) {
		  final String TEST_STRING = "<select id=\"languageselection\"><option value=\"http://en.wikipedia.org/?useformat=mobile&amp;title=hello\" selected=\"selected\">English</option><option value=\"//arc.m.wikipedia.org/wiki/%DC%AB%DC%A0%DC%A1%DC%90_(%DC%A6%DC%AC%DC%93%DC%A1%DC%90)\">ܐܪܡܝܐ</option><option value=\"//de.m.wikipedia.org/wiki/Hallo\">Deutsch</option><option value=\"//es.m.wikipedia.org/wiki/Hola_(saludo)\">Español</option><option value=\"//fr.m.wikipedia.org/wiki/Bonjour\">Français</option><option value=\"//it.m.wikipedia.org/wiki/Ciao\">Italiano</option><option value=\"//he.m.wikipedia.org/wiki/%D7%94%D7%9C%D7%95\">עברית</option><option value=\"//nn.m.wikipedia.org/wiki/Hallo\">‪Norsk (nynorsk)‬</option><option value=\"//pt.m.wikipedia.org/wiki/Oi_(interjei%C3%A7%C3%A3o)\">Português</option><option value=\"//ru.m.wikipedia.org/wiki/%D0%90%D0%BB%D0%BB%D0%BE\">Русский</option><option value=\"//simple.m.wikipedia.org/wiki/Hello\">Simple English</option><option value=\"//vec.m.wikipedia.org/wiki/Ciao\">Vèneto</option><option value=\"//zh.m.wikipedia.org/wiki/Hello\">中文</option></select>";
		  //final String TEST_STRING = "<select id=\"languageselection\"><option value=\"//zh.m.wikipedia.org/wiki/Hello\">中文</option><option value=\"http://en.wikipedia.org/?useformat=mobile&amp;title=hello\" selected=\"selected\">English</option></select>";
		  Sgml sgml = new Sgml();
		  
		  sgml.parse(TEST_STRING);
		  
		  System.out.println(sgml.toString());
	}

	public SgmlNode parse(byte[] textBytes, int index) {
		return parse(textBytes, index, "");
	}
	
	public SgmlNode parse(byte[] textBytes, String tagName) {
		ByteArrayKMP kmpSearch = new ByteArrayKMP(("<" + tagName).getBytes());
		int index = kmpSearch.search(textBytes, 0);
		return parse(textBytes, index, tagName);
	}

	public SgmlNode parse(byte[] textBytes, int index, String tagName) {
//		content = textBytes;
		root = new SgmlNode();
		if (tagName != null && tagName.length() > 0)
			root.setContent(tagName);
		root.setLevel(0);
		root.setEnd(textBytes.length);
		root.parse(textBytes, (tagName != null && tagName.length() > 0) ? index + tagName.length() + 1 : index); // tag name length and <
		if (root.countChildren() == 0 || root.getEnd() == root.getStart())
			root = null;
		return root;
	}

	public SgmlNode parseFirstTag(byte[] textBytes, int index) {
		root = new SgmlNode();
		root.setLevel(0);
		root.parseTag(textBytes, index);
		return root;
	}
	
//	  private int skipWhitespace(int index) {
//	  byte ch = content[index];
//	  while (index < content.length && Character.isSpace((char) ch)) {
////		  ++index;
//		  ch = content[++index];
//	  }
//	  return index;
//}
//
//private SgmlNode parse(String tag, int start) {
//	int stack = 1;
//	int length = content.length;
//	int next = start;
//	boolean isEndTag = false;
//	SgmlNode currentNode = null;
//	String rootTag = null;
//	
//	if (tag != null && tag.length() > 0)
//		rootTag = tag;
//	
//	while (stack > 0 && next < length) {
//		next = skipWhitespace(next);
//		
//		if (content[next] == '<')			// then remove the XML tags
//		{
//			while (Character.isSpace((char) content[next]))
//				++next;
//			if (content[next] == '/') {
//				++next;
//				while (Character.isSpace((char) content[next]))
//					++next;
//				isEndTag = true;
//			}
////			++next;
//			StringBuffer tagBuffer = new StringBuffer();
//				
//			while (content[next] != '>' && !Character.isSpace((char) content[next])) {
//				tagBuffer.append((char)content[++next]);
//			}
//			String this_tag = tagBuffer.toString().trim();
//			
//			if (rootTag == null && this_tag.length() > 0) {
//				rootTag = this_tag;
//				--stack;
//			}
//			else 
//				if (this_tag.equalsIgnoreCase(rootTag)) {
//					if (isEndTag) {
//						--stack;
//					}
//					else {
//						++stack;
//					}
//				}
//			
//			currentNode = new SgmlNode(rootTag);
//			if (content[next] != '>')
//				currentNode.parseAttribute(content, next);
//			else
//				++next; // '>'
//			
//			if (!isEndTag) {	
//				next = skipWhitespace(next);
//				if (next < length) {
//					SgmlNode child = new SgmlNode();
//					if (content[next] != '<') { // text node
//						child.setNodeType(SgmlNode.NODE_TEXT);
//					}
//					else {
//						child.setNodeType(SgmlNode.NODE_ELEMENT);
////						while (child != null && next < content.length) {
////							
////							child = parse(null, child.getEnd());
////						}
//					}
//					next = child.parse(content, next);
//					currentNode.addChild(child);
//				}
//			}
//
//		}
//		else { 
//			++next;
//		}
//	}
////	if (stack == 0)
////		return next;
//	return currentNode;
//}
}

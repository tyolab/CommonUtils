package au.com.tyo.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SgmlNode extends Node<SgmlNode> {

	public static final String SGML_TAG_OPEN = "<";
	public static final String SGML_TAG_CLOSE = ">";
	
	public static final int XML_NODE_UNKNOWN = -1;
	public static final int XML_NODE_INFO = 0;
	public static final int XML_NODE_COMMENT = 1;
	public static final int XML_NODE_ELEMENT = 2;
	public static final int XML_NODE_TEXT = 3;
	
	public static final int END_TAG_NONE = 0;
	public static final int END_TAG_FIRST = 1;
	public static final int END_TAG_LAST = 2;
	
	String content = "";
	HashMap<String, SgmlAttribute> attributes = new HashMap<String, SgmlAttribute>();

	int start;
	int end;

	private int nodeType = XML_NODE_UNKNOWN;
	
	private int isEndTag;
//	private boolean completed;
	private SgmlNode parent;
	private String name;
	
	public SgmlNode(int type) {
		nodeType = type;
		init();
	}
    
	public SgmlNode(byte[] bytes) {
		 super(bytes); 
		 name = "";
	}
  
	public SgmlNode(String content) {
		this.content = content;
		init();
	}

	public SgmlNode() {
		init();
	}
	
	protected void init() {
		super.init();
		name = "";
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	  private int skipWhitespace(byte[] content, int index) {
		  if (index < content.length) {
			  byte ch = content[index];
			  while (index < (content.length - 1) && Character.isSpace((char) ch)) 
				  ch = content[++index];
		  }
		  return index;
	  }
	  
	public int parseAttribute(byte[] bytes, int start) {
		SgmlAttribute attribute = new SgmlAttribute();
 		int end = attribute.parse(bytes, start);
		if (attribute.getName() != null) {
			attributes.put(attribute.getName(), attribute);
		}
		return end;
	}

	private int parseText(byte[] bytes, int index) {
		int next = index;
		while ( next < bytes.length && bytes[next] != '<')
			++next;
		if (next < bytes.length)
			name = new String(bytes, index, next - index);
		return next;
	}
	
	public int parse(byte[] bytes) {
		return parse(bytes, 0);
	}
	
	public int parse(byte[] bytes, int index) {
		if (bytes.length == 0)
			return -1;
		
		start = index;
		end = index;
		
		if (index < bytes.length) {
			if (nodeType == XML_NODE_TEXT) 
				end = parseText(bytes, index);
			else
				end = parseElement(bytes, index);
		}
		return end;
	}
	
	private int parseElement(byte[] bytes, int index) {
		int next = index;

		isEndTag = END_TAG_NONE;
		do {
			next = parseTag(bytes, next);
			
			/*
			 * DEBUG
			 */
//			if (this.getAttribute("lang") != null && this.getAttribute("lang").equalsIgnoreCase("ku"))
//				System.err.println("want it to stop here");
		} while (nodeType != XML_NODE_ELEMENT);
		
		if (name != null && isEndTag == END_TAG_NONE) {
			do {
				next = skipWhitespace(bytes, next);
				if (next < bytes.length) {
					SgmlNode child = new SgmlNode();
						if (bytes[next] != '<')  // text node
							child.setNodeType(SgmlNode.XML_NODE_TEXT);
						else 
							child.setNodeType(SgmlNode.XML_NODE_COMMENT);
					child.setLevel(level + 1);
					next = child.parse(bytes, next);
				
					if (child.getNodeType() == SgmlNode.XML_NODE_TEXT)
						this.addChild(child); 
					else {
						if (child.getEndTagState() == END_TAG_FIRST &&
							child.getName() != null && 
							child.getName().trim().equalsIgnoreCase(name)) {
							end = child.getEnd();
							isEndTag = END_TAG_LAST;
						}
						else 
							this.addChild(child); 
					}
				}
				else
					break;
			} while ((END_TAG_NONE == isEndTag/* && stack > 0*/));
		
//			next = parseTag(bytes, next);
		}
//				else {  // something wrong with text, not a valid
//					//TODO
//					// maybe throws an exception here
//					break;
////					++next;
//				}
//			}
		
		// if isEndTag ==  false, and next == length
		// there is at least a mismatch tag

		return next;
	}
	
		
	private int getEndTagState() {
		return isEndTag;
	}
	
	private int parseAttributes(byte[] bytes, int index) {
		int next = index;
		while (next < bytes.length && bytes[next] != '>' && bytes[next] != '/') {
			next = parseAttribute(bytes, next);
			next = skipWhitespace(bytes, next);
		}
		
		if (next < bytes.length && bytes[next] == '/') {
			isEndTag = END_TAG_LAST;
			++next; 
	//		next = skipWhitespace(bytes, next);
		}
		
	//	assert(bytes[next] == '>');
	//	end = ++next;  // '>', also the tag may end here because there is no explicit end tag
		return next;
	}
	
	private String levelToTabs() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; ++i)
			sb.append("\t");
		return sb.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String tabs = levelToTabs();
		if (nodeType == this.XML_NODE_TEXT)
			sb.append(" " + tabs + "TEXT:" + name + "\n");
		else {

			sb.append("+" + tabs + name + "\n");
			sb.append(" " + tabs + "ATTRIBUTES:\n");
			Collection<SgmlAttribute> set = attributes.values();
			for (SgmlAttribute attribute : set) 
				sb.append(" " + tabs + "           " + attribute.toString() + "\n");
		}
//		++level;
		Iterator<SgmlNode> it = iterator();
		while (it.hasNext()) {
			SgmlNode child = it.next();
			sb.append(child.toString());
		}
//		--level;
		return sb.toString();
	}

	public void setContent(String value) {
		content = value;
	}
	
	public String getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		String text = "";
		if (children.size() > 0) {
			SgmlNode first = children.get(0);
			if (first.getNodeType() == XML_NODE_TEXT)
				text = first.getName();
		}
		else if (this.getNodeType() == XML_NODE_TEXT)
			text = name;
		return text;
	}

	public String getAttribute(String key) {
		if (attributes.containsKey(key)) {
			return attributes.get(key).getValue();
		}
		return null;
	}
	
	public int parseTag(byte[] textBytes, int index, String this_tag) {
		name = this_tag;
		return parseTag(textBytes, index);
	}

	public int parseTag(byte[] textBytes, int index) {
		if (bytes == null)
			bytes = textBytes;
		
		int next = index;
		next = skipWhitespace(bytes, next);
		if (next >= bytes.length)
			return next;
		
//		int stack = 0;
		String this_tag = null;
		nodeType = XML_NODE_ELEMENT;
		
		if ((char) bytes[next] == '<')			// then remove the XML tags
		{
			++next;
			next = skipWhitespace(bytes, next);
			
			if ((char) bytes[next] == '?') {
				nodeType = XML_NODE_INFO;
			}
			else {
				if ((char)bytes[next] == '/') {
					++next;
	//				while (Character.isSpace((char) bytes[next]))
	//					++next;
					isEndTag = END_TAG_FIRST;
				}
	//				++next;
				StringBuffer tagBuffer = new StringBuffer();
				
				while (next < textBytes.length && bytes[next] != '>' && !Character.isSpace((char) bytes[next])) 
					tagBuffer.append((char)bytes[next++]);
				
				this_tag = tagBuffer.toString().trim();
				if (name == null || name.length() == 0) {
					name = this_tag;
	//				++stack;
				}
				else if (!this_tag.equalsIgnoreCase(name)) {
					end = next;
					name = null;
					return next;
				}
				this_tag = null;
				
				if (isEndTag == END_TAG_NONE) {
					next = parseAttributes(bytes, next);
				}
				else {
	
					/*
					 * TODO if there are something here, it is an well-formness error
					 */
	
				}
			}
			
			next = skipWhitespace(bytes, next);
			while (next < bytes.length && (char)bytes[next] != '>') 
				++next;
			end = ++next;
		}
		else {
			if (name != null && name.length() > 0) {
				next = parseAttributes(bytes, next);
				this_tag = null;
//				++stack;
			}
			else {
				// something wrong here
				// then this is a text node
				nodeType = XML_NODE_TEXT;
				next = parseText(bytes, next);
				return next;
			}
		}
		return next;
	}

	public void parse() {
		if (content != null && content.length() > 0)
			parse(content.getBytes(), 0);
	}

	public SgmlNode getDecendant(int i, int j) {
		if (countChildren() == 0)
			return null;
		
		if (i == 0) {
			if (this.countChildren() > j)
				return getChild(j);
			return null;
		}
		return getChild(0).getDecendant(i - 1, j);
	}
	
	public SgmlNode findChild(String name) {
		return findChild(name, -1);
	}
	
	public SgmlNode findChild(String name, int index) {
		int count = -1;
		SgmlNode child = null;
		for (int it = 0; it < this.countChildren(); ++it) {
			++count;
			if (this.getNodeType() == XML_NODE_ELEMENT) {
				child = this.getChild(it);
				String childName = child.getName();
				// debug
				// cout << "comparing with " << name << endl;
				if (name.equalsIgnoreCase(childName)) {
					if (index == -1 || (index > -1 && count == index))
						break;
					child = null;
				}
				else
					child = child.findChild(name, index);
			}
		}
		return child;
	}
	
	public SgmlNode path(String path) {
		String[] paths = path.split("/");
		return path(paths, 0);
	}
	
	public SgmlNode path(String[] path, int index) {
		if (path != null && path.length > 0 && index < path.length) {
//			if (index > path.length) {
//				if (this.getName().equals(path[path.length - 1]))
//					return this;
//				else
//					return null;
//			}
			
			for (SgmlNode child : children) {
				if (child.getName().equals(path[index])) {
					if (index == (path.length - 1))
						return child;
					return child.path(path, index + 1);
				}
			}
		}
		return null;
	}
	
	public String toText() {
		StringBuffer buffer = new StringBuffer();
		return toText(buffer);
	}
	
	/**
	 * 
	 * @return
	 */
	public String toText(StringBuffer buffer) {
		for (SgmlNode child : children) {
			if (child.getNodeType() == XML_NODE_TEXT) {
				buffer.append(child.getText());
				buffer.append("\n");
			}
			else if (this.getNodeType() == XML_NODE_ELEMENT)
				child.toText(buffer);
		}
		return buffer.toString();
	}

	public boolean hasChild(String name) {
		return findChild(name) != null;
	}
	
	public static List<SgmlNode> extractNodes(String text, String tagNameStart) {
		return extractNodes(text, tagNameStart);
	}
	
	public static List<SgmlNode> extractNodes(String text, String tagNameStart, String tagNameEnd) {
		int start = 0;
		int byteLength = start; 
		List<SgmlNode> nodes = new ArrayList<SgmlNode>();
		
		while ((start = text.indexOf(tagNameStart, start)) > -1) {
			String substr = text.substring(0, start);
			byteLength = substr.getBytes().length;
			
			SgmlNode node = new SgmlNode();
			node.parse(text.getBytes(), byteLength);
			
			if (node.getEnd() > -1 && node.getEnd() > start) {
				start = node.getEnd();
				nodes.add(node);
			}
			else
				break;
		}
		return nodes;
	}
	
	public static SgmlNode createSimpleNodeWithText(String text, String tagStart) {
		return createTextNode(text, tagStart, tagStart, 0, true);
	}
	
	public static SgmlNode createTextNode(String text, String tagStart, String tagEnd, int index, boolean isXml) {
		SgmlNode node = new SgmlNode();
		node.setNodeType(XML_NODE_TEXT);
		
		String start_tag;
	
		int start_tag_len, end_tag_len;
	
		start_tag_len = tagStart.length();
		if (isXml && tagStart.indexOf("<") < 0 ) {
			start_tag = "<" + tagStart; // + ">";
			start_tag_len += 2;
//			node.setName(tagStart);
		}
		else
			start_tag = tagStart;
	
		end_tag_len = tagEnd.length();
		String end_tag;
		if (isXml && tagEnd.indexOf("<") < 0) {
			end_tag = "</" + tagEnd + ">";
			end_tag_len += 2;
		}
		else
			end_tag = tagEnd;
		
		int pos = -1;
		pos = text.indexOf(start_tag, index);
		
		if (pos > -1) {	
			pos += start_tag_len;
			if (isXml) {
				while (text.charAt(pos) != '>')
					++pos;
				++pos;
			}
			node.setStart(pos);
			
			int end;
			/*
			 * 			
			if (isXml)

			 * 
			 */
			
			end = text.indexOf(end_tag, pos);
			
			if (end > pos) {
				node.setEnd(end);
				node.setName(text.substring(pos, end));
			}
			else if (isXml) {
			/* now the between part */
				StringBuffer buff = new StringBuffer();
				do {
					buff.append(text.charAt(pos));
				} while (text.charAt(++pos) != '<');
				
				while (text.charAt(pos) != '>')
				++pos;
				
				node.setEnd(pos);
				node.setName(buff.toString());
			}
		}

//		node.setName(tag);
		
		return node;
	}
	
//	public SgmlNode between(String source, String open_tag, String close_tag,
//			int index, boolean is_xml)
//	{
////		String end = source + strlen(source);
//	
//		String start_tag;
//		int start, finish;
//	
//		int start_tag_len, end_tag_len;
//	
//		start_tag_len = open_tag.length();
//		if (open_tag.indexOf("<") > -1 || !is_xml)
//			start_tag = open_tag;
//		else {
//			start_tag = "<" + open_tag + ">";
//			start_tag_len += 2;
//		}
//	
//		end_tag_len = close_tag.length();
//		String end_tag;
//		if (close_tag.indexOf("<") > 0 || !is_xml)
//			end_tag = close_tag;
//		else {
//			end_tag = "</" + close_tag + ">";
//			end_tag_len += 2;
//		}
//	
//		if ((start = source.indexOf(start_tag_len), start_tag.c_str())) != NULL && start < end)
//			{
//			start += start_tag_len;
//	
//			if ((finish = strstr(start, end_tag.c_str())) != NULL && finish < end)
//				{
//				*start_r = (String )start;
//				*finish_r = (String )finish;
//				}
//			}
//	}

//	String between(String source, String open_tag, String close_tag, boolean is_xml) 
//	{
//		int start, finish;
//	
//		between(source, open_tag, close_tag, start, &finish, is_xml);
//	
//		if (start == NULL || finish == NULL)
//			return NULL;
//	
//		return strnnew(start, finish - start);;
//	}
//
//	public String between(String source, String open_tag) {
//		return between(source, open_tag, open_tag);
//	}
	
	public static List<SgmlNode> fastExtractNodes(String text, String tagNameStart) {
		return fastExtractNodes(text, tagNameStart);
	}
	
	public static List<SgmlNode> fastExtractNodes(String text, String tagNameStart, String tagNameEnd) {

		List<SgmlNode> nodes = new ArrayList<SgmlNode>();
		int pos = 0;
		SgmlNode node = null;
		while ((node = createTextNode(text, "<a", "</a>", pos, true)).getEnd() > pos) {

				nodes.add(node);
				pos = node.getEnd();
		}
		return nodes;
	}
}
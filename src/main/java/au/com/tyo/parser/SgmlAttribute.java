/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.parser;

public class SgmlAttribute {
	byte[] bytes;
	int name_start;
	int name_end;
	int	value_start;
	int	value_end;
	boolean has_equal_;
	boolean has_quote_;
	boolean force_end_quote_;  // if has quote, then index must end within the quote
	boolean is_single_quote_;  // false is double quote, true for single quote
	String end_chars_ = ">"; // it was "'\">", I can't figure out why now, and the previous wasn't checking the nested quotes
	int start;
	int end;
	
	int previous;
	int index;
	
	String name;
	String value;
	
	byte ch;
	byte pre_ch;
	
//	private HashMap<String, String> properties = null;
	
	private boolean is_end_char() {
		boolean result = false;
//		byte ch = bytes[index];
		if (end_chars_.length() > 0) {
			int pos = end_chars_.indexOf((char)ch);
			if (pos > -1)
				result = true;
		}
		else {
			if (has_equal_ && !has_quote_)
				result = Character.isSpace((char) ch);
		}
		
		if (result)
			if (has_equal_) {
				if (has_quote_) {
					int pre = index;
					pre--;
					skipWhitespaceBackward(pre);
//					byte pre_ch = bytes[pre];
					if ( (pre_ch == '\"')	|| (pre_ch == '\'')) {
						  value_end = pre;
					}
					else
						value_end = pre++;
				}
				else
					value_end = end;
			}
		
		return result;
	 }
	
	public int parse(byte[] bytes) {
		return parse(bytes, 0);
	}
	
	  public int parse(byte[] bytes, int start) {
		  this.bytes = bytes;
		  this.start = start;
		  this.end = bytes.length;
		  

		  previous = index = start;
		  init();
		  
		  checkName();
		  
		  while (!is_end())
			  ++index;
		  
		  if (value_start > name_end && value_start == value_end)
			  value_end = index;
		  
		  if (value_start < value_end)
			  value = new String(bytes, value_start, value_end - value_start);
		  
		  return index;
	  }
	  
	  private boolean eow() {
		  return index >= end;
	  }

	  private void skipWhitespace() {
		  while (index < bytes.length && (Character.isSpace((char) (ch = bytes[index])))) 
			  ++index;
	  }
	  
	  boolean init() {
			skipWhitespace();
			
			start = index;
			name_start = index;
			name_end = index;
			value_start = index;
			value_end = index;

			return true;
		}

	  private void checkName() {
		  boolean has_space;
//		  ++index;
		  while (!eow()) {
			name_end = index;
			ch = bytes[index];
			has_space = Character.isSpace((char) ch);

			if (has_space)
				skipWhitespace();

			if (ch == '=') {
				has_equal_ = true;

				// deside where is the end of name
				int pre = index;
				skipWhitespaceBackward(--pre);
				name_end = ++pre;
				
				name = new String(bytes, name_start, name_end - name_start);
				
				++index;
				skipWhitespace();
				if (ch == '\"' || ch == '\'') {
					is_single_quote_ = (ch == '\'');
					has_quote_ = true;
					++index;
				}
				value_start = index;
				value_end = index;
				break;
				//--index;
			}
			++index;
		  }
	  }
	  
		boolean is_end() {
			boolean ret = eow();
			//	ret = (has_equal_ && (value_.begin() != value_end = )));
		
			if (!ret) {
				ch = bytes[index];
				
//				if (ch == '"')
//					System.err.println("Stop here");
				// if have the break character like "=, space" or something specified as the break char
				// which mean the beginning of VALUE
				if (has_equal_) {
					boolean check_end_char = false;
					if (has_quote_) {
						int pre = index;
						pre--;
						pre_ch = bytes[pre];
						boolean ret1 = false;
						boolean ret2 = false;
						if (( (ret1 = (ch == '\"'))
								|| (ret2 = (ch == '\''))
							  )
							 && pre_ch != '\\') {
							if (is_single_quote_)
								ret = ret2;
							else
								ret = ret1;

							if (ret) {
								value_end = index;
								++index;
							}
						}
						if (!ret && !force_end_quote_)
							check_end_char = true;
					} else {
						check_end_char = true;
					}
					
					if (check_end_char)
						ret = is_end_char();
				}
			} // (1) if (!ret)
		return ret;
	}

		private void skipWhitespaceBackward(int pre) {
			  pre_ch = bytes[pre];
			  while (Character.isSpace((char) pre_ch)) {
				  --pre;
				  pre_ch = bytes[pre];
			  }
			
		}
		
//  	  public String getValue(String key) {
//		  if (properties != null)
//			  return properties.get(key);
//		  return null;
//	  }


	public String getName() {
			return name;
	}

	public String getValue() {
		return value;
	}
	
	public String toString() {
		return name + " = " + value;
	}

	public static void main(String[] args) {
  		  final String TEST_STRING = "value=\"http://en.wikipedia.org/?useformat=mobile&amp;title=hello\" ";
  		  SgmlAttribute properties = new SgmlAttribute();
  		  properties.parse(TEST_STRING.getBytes(), 0);
  		  
  		  String value = properties.getValue();
  		  String name = properties.getName();
  		  System.out.println("TEST STRING: " + TEST_STRING);
  		  System.out.println(name + ": " + value);
  				  
  	  }
}

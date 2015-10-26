package au.com.tyo.utils;

public class UrlCode {
	
	public  static final byte[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	/* Converts a hex character to its integer value */
	public static byte fromHex(byte ch) {
	  return (byte) (Character.isDigit(ch) ? ch - '0' : Character.toLowerCase(ch) - 'a' + 10);
	}

	/* Converts an integer value to its hex character*/
	public static byte toHex(byte code) {
		int c = (int) code;
		return HEX[code & 15];
	}

	/* Returns a url-encoded version of str */
	/* IMPORTANT: be sure to free() the returned string after use */
	public static byte[] encode(byte[] str) {
	  byte pstr; // = str, *
	  byte[] buf = (new byte[str.length * 3 + 1]); //malloc(strlen(str) * 3 + 1), *pbuf = buf;
	  int count = 0;
//	  while (*pstr) {
	  for (int i = 0; i < str.length;) {
		pstr = str[i];
	    if (Character.isLetterOrDigit(pstr) || pstr == '-' || pstr == '_' || pstr == '.' || pstr == '~') 
	    	buf[count++] = pstr;
//	    	*pbuf++ = *pstr;
	    else if (pstr == ' ') 
	    	buf[count++] = '+';
//	      *pbuf++ = '+';
	    else {
	    	buf[count++] = '%';
	    	buf[count++] = toHex((byte) (pstr >> 4));
	    	buf[count++] = toHex((byte) (pstr & 15));
	    }
//	    pstr++;
	    ++i;
	  }
//	  *pbuf = '\0';
	  byte[] result = new byte[count];
//	  *pbuf = '\0';
	  System.arraycopy(buf, 0, result, 0, count);
	  
	  return result;
	}

	/* Returns a url-decoded version of str */
	/* IMPORTANT: be sure to free() the returned string after use */
	public static byte[] decode(byte[] str) {
//	  byte[] pstr = str;
	  byte[] buf = new byte[str.length];
	  int count = 0;
//	  byte[] *pbuf = buf;
	  //while (*pstr) {
	  for (int i = 0; i < str.length;) {
		  byte pstr = str[i];
		  
	    if (pstr == '%') {
	      if (((i + 1) < str.length && str[i + 1] > 0) && ((i + 2) < str.length && str[i + 2] > 0)) {
	        buf[count++] = (byte) (fromHex(str[i + 1]) << 4 | fromHex(str[i + 2]));
	        i += 2;
	      }
	    } else if (pstr == '+') { 
	    	buf[count++] = ' ';
	    } else {
	    	buf[count++] = pstr;
	    }
	    ++i;
//	    pstr++;
	  }
	  byte[] result = new byte[count];
//	  *pbuf = '\0';
	  System.arraycopy(buf, 0, result, 0, count);
	  
	  return result;
	}

}

package au.com.tyo.parser;

/*
 * this file was taken from the CliX package implemented by Schenkel, R.
 * for building a YAWN system
 * 
 * [1] Schenkel, R., F. Suchanek, and G. Kasneci, "YAWN: A Semantically Annotated Wikipedia XML Corpus." In Proceedings of BTW'2007, 2007.
 */

public class XML
{
    public static final String nsXLink="http://www.w3.org/1999/xlink";
    
    public static String unXMLify(String input)
    {
    	if (input==null) return null;
		input=input.replaceAll("&quot;","\"");
		input=input.replaceAll("&lt;","<");
		input=input.replaceAll("&gt;",">");
		input=input.replaceAll("&apos;","'");
		input=input.replaceAll("&amp;","&");
    	
		return input;
    }
    
    public static String unXMLify2(String input)
    {
    	if (input==null) return null;
		input=input.replaceAll("&amp;","&");
		input=input.replaceAll("&quot;","\"");
		input=input.replaceAll("&lt;","<");
		input=input.replaceAll("&gt;",">");
		input=input.replaceAll("&apos;","'");
    	
		return input;
    }
    
    public static String XMLify2(String input)
    {
    	if (input==null) return null;
		//input=input.replaceAll("&","&amp;");
		input=input.replaceAll("<","&lt;");
		input=input.replaceAll(">","&gt;");
		input=input.replaceAll("\"","&quot;");
		input=input.replaceAll("'","&apos;");

		return input;
    }

    public static String XMLify(String input)
    {
    	if (input==null) return null;
		input=input.replaceAll("&","&amp;");
		input=input.replaceAll("<","&lt;");
		input=input.replaceAll(">","&gt;");
		input=input.replaceAll("\"","&quot;");
		input=input.replaceAll("'","&apos;");

		return input;
    }

    public static String Tagify(String input)
    {
    	if (input==null) return null;
    	
    	input=input.trim();
    	
//    	if (Character.isDigit(input.charAt(0)))
//    		input="_"+input;
//    	
//    	input=input.replaceAll(" ","_");
//    	input=input.replaceAll("/","_");
//    	input=input.replaceAll("(","_");
//    	input=input.replaceAll(")","_");
//    	input=input.replaceAll("[","_");
//    	input=input.replaceAll("]","_");

    	char arr[]=input.toCharArray();
    	StringBuffer tag=new StringBuffer();
    	for (int i=0;i<arr.length;i++)
    	{
    		if (Character.isLetter(arr[i])) tag.append(arr[i]);
    		else if ((i>0)&&(Character.isDigit(arr[i]))) tag.append(arr[i]);
    		else if ((i>0)&&(arr[i]=='-')) tag.append('-');
    		else if ((i>0)&&(arr[i]=='.')) tag.append('.');
    		else if ((i>0)&&(arr[i]=='_')) tag.append('_');
    		else tag.append('_');
    	}

    	return tag.toString();
    }
    
   
	public static String getElementText(String doc, String tag) {
		int pos = -1;
		pos = doc.indexOf("<" + tag);
		StringBuffer buff = new StringBuffer();
		if (pos > -1) {
			pos += (tag.length() + 1);
			while (doc.charAt(pos) != '>')
				++pos;
			++pos;
			do {
				buff.append(doc.charAt(pos));
			} while (doc.charAt(++pos) != '<');
			
		}
		return buff.toString();
	}
}

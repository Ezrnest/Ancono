package cn.timelives.java.utilities.xml;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XmlUtilities {
	//no instance
	private XmlUtilities(){}
	public static final String Starting = "</";
	public static final String Ending = "/>";
	public static final String Separator = System.lineSeparator();
	private static final int rep_num = 5;
	private static final String[] replacement_key = new String[rep_num],replacement_value = new String[rep_num];
	private static final Pattern[] replacements = new Pattern[rep_num];
	static{
		replacement_key[0] = "&";
		replacement_key[1] = "\"";
		replacement_key[2] = "'";
		replacement_key[3] = "<";
		replacement_key[4] = ">";
		replacement_value[0] = "&amp;";
		replacement_value[1] = "&quot;";
		replacement_value[2] = "&apos;";
		replacement_value[3] = "&lt;";
		replacement_value[4] = "&gt;";
	}
	static{
		for(int i=0;i<rep_num;i++){
			replacement_value[i] = Matcher.quoteReplacement(replacement_value[i]);
			replacements[i] = Pattern.compile(replacement_key[i], Pattern.LITERAL);
		}
	}
	/**
	 * Replace the all illegal context("'&<>) in the input text.
	 * @param input
	 * @return
	 */
	public static String quote(String input){
		for(int i=0;i<rep_num;i++){
			Matcher mat = replacements[i].matcher(input);
			input = mat.replaceAll(replacement_value[i]);
		}
		return input;
	}
	//An array for prefix of the xml/html file,the number of prefix should be absolutely enough.
	private static final String[] prefixs = { "", 
			"	", 
			"		", 
			"			", 
			"				",
			"					", 
			"						", 
			"							",
			"								", 
			"									",
			"										", };
	

	public static final String getPrefix(int level) {
		if (level < prefixs.length) {
			return prefixs[level];
		}
		char[] buf = new char[level];
		Arrays.fill(buf, '	');
		return String.copyValueOf(buf);
	}
	
	
	public static void appendBlockStart(String quotedName,StringBuilder builder){
		builder.append('<').append(quotedName).append('>');
	}
	
	public static void appendBlockEnd(String quotedName,StringBuilder builder){
		builder.append(Starting).append(quotedName).append('>');
	}
	
}

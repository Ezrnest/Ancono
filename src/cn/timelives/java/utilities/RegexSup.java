package cn.timelives.java.utilities;

import java.util.regex.Pattern;

/**
 * A tool class for some useful patterns.
 * @author lyc
 *
 */
public class RegexSup {
	private RegexSup(){}
	/**
	 * A pattern for one or more spaces
	 */
	public static final Pattern SPACE = Pattern.compile(" +");
	
	public static final Pattern LINE = Pattern.compile("^.*?$");
	
}

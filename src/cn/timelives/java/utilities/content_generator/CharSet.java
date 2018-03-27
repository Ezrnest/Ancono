package cn.timelives.java.utilities.content_generator;
/**
 * This class defines different character sets that consist different parts of a word.
 * @author rw185035
 *
 */
public class CharSet {
	private CharSet(){};//no instance should be created
	
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	public static final char[] AEIOU = {'a','e','i','o','u'};
	
	public static final char[] LIGHT = {'c','f','g','h','k','l','p','s','t'};
	
	public static final char[] HEAVY = {'b','d','j','m','n','r'};
	
	public static final char[] OTHER = {'q','v','w','x','y','z'};
	
	public static final String[] MIX_START ={
									"ch","sh","ph","dr","tr","th","ac"
										};
	public static final String[] MIX_MID = {"em","ie","ea","ao","ou","in","en","er","or"};
	
	public static final String[] MIX_END = {"ght","ing","ed","ly","tory","ment"};
	
	public static final char[] TALL_CHAR = {'b','d','f','h','k','l','t'};
	
	public static final char[] DOWN_CHAR = {'g','j','y'};
	
	public static final char[] ROUND_CHAR = {'a','c','e','o'};
}

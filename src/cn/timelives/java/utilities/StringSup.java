/**
 * 2017-09-28
 */
package cn.timelives.java.utilities;

import java.util.ArrayList;

/**
 * @author liyicheng
 * 2017-09-28 21:36
 *
 */
public final class StringSup {

	/**
	 * 
	 */
	public StringSup() {
	}
	
	
	public static String addPadding(String str,int lengthToReach,char c) {
		if(str.length()>=lengthToReach) {
			return str;
		}
		StringBuilder sb = new StringBuilder(lengthToReach);
		sb.append(str);
		int t = str.length();
		while(t++<lengthToReach) {
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static void addPadding(StringBuilder sb,int lengthToReach,char c) {
		if(sb.length()>=lengthToReach) {
			return;
		}
		sb.ensureCapacity(lengthToReach);
		sb.append(c);
		int t = sb.length();
		while(t++<lengthToReach) {
			sb.append(c);
		}
	}
	
	public static int parseOrDefault(String x,int defaultValue) {
		try {
			return Integer.parseInt(x);
		}catch(Exception ex ) {
			return defaultValue;
		}
	}

	public static boolean endWith(String s,String suffix,int pos){
		return s.startsWith(suffix,pos-suffix.length());
	}

	public static String[] splitWithMatching(String str,char split,char leftBracket,char rightBracket){
		int bar = 0;
		int prevPos = 0;
		var list = new ArrayList<String>();
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(c == leftBracket){
				bar ++;
				continue;
			}
			if(c == rightBracket){
				bar --;
				continue;
			}
			if(bar == 0){
				if(c == split){
					list.add(str.substring(prevPos,i));
					prevPos = i+1;
				}
			}
		}
		if(prevPos<=str.length()){
			list.add(str.substring(prevPos));
		}
		return list.toArray(new String[]{});
	}

    public static String[] splitWithMatching(String str,char split){
	    return splitWithMatching(str,split,'(',')');
    }
}

/**
 * 2017-09-28
 */
package cn.timelives.java.utilities;

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
}

/**
 * 2017-09-28
 */
package cn.timelives.java.utilities;

import java.util.ArrayList;
import java.util.Arrays;

import static cn.timelives.java.utilities.ArraySup.getSum;

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

    private static void fillBlank(StringBuilder sb, int len) {
        for (int i = 0; i < len; i++) {
            sb.append(' ');
        }
    }

    public static String formatMatrix(String[][] mat) {
        return formatMatrix(mat, new int[0]);
    }

    public static String formatMatrix(String[][] mat, int[] setWidth) {
        int column = 0;
        for (String[] aMat : mat) {
            column = Math.max(column, aMat.length);
        }
        if (setWidth.length < column) {
            setWidth = Arrays.copyOf(setWidth, column);
        }
        for (String[] aMa : mat) {
            for (int j = 0; j < aMa.length; ++j) {
                //get the String stored and calculate the best length
                setWidth[j] = Math.max(aMa[j].length(), setWidth[j]);
            }
        }

        int len = getSum(setWidth) + column + 1 << 3;// add up to it: both side of the line should have bracket and space

        StringBuilder sb = new StringBuilder(len * mat.length);
        int t0;
        for (int i = 0; i < mat.length; i++) {
            if (i == 0)
                sb.append('┌');
            else if (i == mat.length - 1)
                sb.append('└');
            else
                sb.append('│');

            int j = 0;
            for (; j < mat[i].length; j++) {
                int t = setWidth[j] - mat[i][j].length();
                fillBlank(sb, t + 1);
                sb.append(mat[i][j]);
            }

            t0 = getSum(setWidth, j, setWidth.length) + setWidth.length - j + 1;
            if (t0 != 0) {
                fillBlank(sb, t0);
            }

            if (i == 0)
                sb.append('┐');
            else if (i == mat.length - 1)
                sb.append('┘');
            else
                sb.append('│');
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}

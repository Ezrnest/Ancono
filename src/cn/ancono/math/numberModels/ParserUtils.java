package cn.ancono.math.numberModels;

import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.structure.Pair;

import java.util.LinkedList;
import java.util.List;

public final class ParserUtils {
    private ParserUtils() {
    }

    public static void throwFor(String expr, String msg, int index) {
        throw new NumberFormatException(System.lineSeparator() + msg + expr + System.lineSeparator() +
                String.valueOf(ArraySup.fillArr(index + msg.length(), ' ')) + '^');
    }

    public static void throwFor(String expr, int index) {
        throwFor(expr, "Wrong format: ", index);
    }

    public static List<Pair<String, Boolean>> splitByAdd(String expr, final int offset) {
        if (expr.isEmpty()) {
            throwFor(expr, "Empty: ", offset);
        }
        List<Pair<String, Boolean>> list = new LinkedList<>();
        int i = 0;
        int end = expr.length() - 1;
        while (expr.charAt(i) == ' ') {
            i++;
        }
        boolean first = true;
        int prev = i;
        int brac = 0;
        boolean positive = !(expr.charAt(i) == '-');
        for (; i <= end; i++) {
            char c = expr.charAt(i);
            if (brac == 0) {
                if (c == '+' || c == '-') {
                    if (!first) {
                        if (prev != 0 && i == prev + 1) {
                            throwFor(expr, offset + i);
                        }
                        String part = expr.substring(prev, i);
                        Pair<String, Boolean> p = new Pair<String, Boolean>(part, positive);
                        list.add(p);
                    }
                    prev = i + 1;
                    positive = c == '+';

                    first = false;
                    continue;
                }
            }
            if (c == '(') {
                brac++;
            } else if (c == ')') {
                brac--;
                if (brac < 0) {
                    throwFor(expr, "Missing left bracket: ", offset + i);
                }
            }
            first = false;

        }
        //deal with the last one
        if (prev > end) {
            throwFor(expr, offset + end);
        } else if (brac != 0) {
            throwFor(expr, "Missing right bracket: ", offset + end);
        } else {
            String part = expr.substring(prev, end + 1);
            Pair<String, Boolean> p = new Pair<String, Boolean>(part, positive);
            list.add(p);
        }
        return list;
    }

    /**
     * finds the ending of the integer, returns the index of the first  non-digit character.
     *
     * @param str
     * @param start
     * @return exclusive
     */
    public static int findIntEnd(String str, int start) {
        for (int i = start; i < str.length(); i++) {
            if (!isDigit(str.charAt(i))) {
                return i;
            }
        }
        return str.length();
    }

    /**
     * Returns the index of the matching bracket, start should be
     * (1+<i>the index of the bracket to match</i>)
     *
     * @param str
     * @param start
     * @return inclusive for ).
     */
    public static int findMatchBrac(String str, int start) {
        int bra = 1;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') {
                bra++;
            } else if (c == ')') {
                bra--;
            }
            if (bra == 0) {
                return i;
            }
        }
        return str.length();
    }

    public static int findQuoteChar(String str, int start, char ch) {
        int bra = 0;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') {
                bra++;
            } else if (c == ')') {
                bra--;
            }
            if (bra == 0 && c == ch) {
                return i;
            }
        }
        return str.length();
    }


    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }


}

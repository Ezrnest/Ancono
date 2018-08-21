package cn.timelives.java.utilities;

import cn.timelives.java.math.algebra.linearAlgebra.Matrix;
import cn.timelives.java.math.numberModels.Fraction;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Arrays;

import static cn.timelives.java.utilities.ArraySup.fillArr;
import static cn.timelives.java.utilities.ArraySup.getSum;

/**
 * this class help users with easier usage of printing.
 * You can use {@link #print(Object)},{@link #printnb(Object)} and many other
 * methods. The default output is {@linkplain System#out}
 *
 * @author lyc
 */
public class Printer {

    private static PrintWriter out = new PrintWriter(System.out, true);

    public static void reset(PrintStream ps) {
        out = new PrintWriter(ps, true);
    }

    public static void reset(OutputStream os) {
        out = new PrintWriter(os, true);
    }

    public static void reset(PrintWriter pw) {
        out = pw;
    }

    public static void reset(Writer w) {
        out = new PrintWriter(w, true);
    }

    public static PrintWriter getOutput() {
        return out;
    }

    /**
     * System.out.println(obj.toString)
     *
     * @param obj
     */
    public static void print(Object obj) {
        out.println(obj);
    }

    public static void print() {
        out.println();
    }

    /**
     * System.out.println(Arrays.toString(obj))
     *
     * @param obj
     */
    public static void print(Object[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(double num) {
        print(SNFSupport.DF.format(num));
    }

    public static void print(double num, int showDigit) {
        DecimalFormat df = SNFSupport.dfByDigit(showDigit);
        print(df.format(num));
    }

    public static void print(int n) {
        out.println(n);
    }

    public static void print(long n) {
        out.println(n);
    }

    public static void print(char c) {
        out.println(c);
    }

    public static void print(boolean b) {
        out.println(b);
    }

    public static void printf(String format, Object... args) {
        out.printf(format, args);
    }

    public static void print(int[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(byte[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(char[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(long[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(float[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(double[] obj) {
        out.println(Arrays.toString(obj));
    }

    /**
     * print the double numbers with limited digit number.
     *
     * @param obj
     * @param shownDigit
     */
    public static void print(double[] obj, int shownDigit) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        getMiddle(sb, obj, shownDigit);
        sb.append(']');
        print(sb.toString());
    }

    private static StringBuilder getMiddle(StringBuilder sb, double[] obj, int shownDigit) {
        DecimalFormat df = SNFSupport.dfByDigit(shownDigit);
        for (int i = 0; i < obj.length; i++) {
            sb.append(df.format(obj[i]));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb;
    }

    public static void print(short[] obj) {
        out.println(Arrays.toString(obj));
    }

    public static void print(boolean[] obj) {
        out.println(Arrays.toString(obj));
    }


    public static void printnb(Object obj) {
        out.print(obj);
        out.flush();
    }

    public static void print_() {
        print_(16);
    }

    public static void print_(int count) {
        char[] buf = new char[count];
        for (int i = 0; i < count; i++) {
            buf[i] = '-';
        }
        out.println(buf);
    }

    /**
     * print the char c for {@code count} times and decides whether to change line.
     *
     * @param count
     * @param c
     * @param nb
     */
    public static void print_(int count, char c, boolean nb) {
        char[] buf = new char[count];
        for (int i = 0; i < count; i++) {
            buf[i] = c;
        }
        if (nb) {
            out.print(buf);
        } else {
            out.println(buf);
        }

    }

    /**
     * print the matrix and the method will choose the best way to show it
     * a Matrix should be shown like following
     * ┌ 1 2 3 4 5 6 7 ┐
     * │ 2 3 4 5 6 7 8 │
     * └ 3 4 5 2 0 8 9 ┘
     * the main part is like :
     * 23  45 23 31 32 312241241 ┐
     * 231 23 34 23 13 23        │
     * 012 78 62 -1 3  2         ┘
     *
     * @param mat
     * @param setWidth : the size for each integer
     * @throws IllegalArgumentException: if the width is less than the width that one of the integer should have
     */
    public static void printMatrix(int[][] mat, int setWidth) {
        int maxCount = 0;
        for (int[] arr : mat) {
            maxCount = Math.max(arr.length, maxCount);
        }
        int[] width = fillArr(maxCount, setWidth);
        printMatrix(mat, width);

    }

    /**
     * Print the given matrix.The matrix will be printed properly and make sure number in each column is aligned.
     *
     * @param mat
     */
    public static void printMatrix(Matrix<Fraction> mat) {
        if (mat != null)
            printMatrix(mat.getValues());
        else
            print("Null");
    }

    /**
     * print the Matrix by given width for each column
     *
     * @param mat
     * @param setWidth
     * @throws IllegalArgumentException: if the width is less than the width that one of the integer should have
     */
    public static void printMatrix(int[][] mat, int[] setWidth) {
        String[][] ma = ArraySup.mapTo2(mat, Integer::toString, String.class);
        print(StringSup.formatMatrix(ma, setWidth));
    }

    /**
     * Print the given matrix.The matrix will be printed properly and make sure number in each column is aligned.
     *
     * @param mat
     */
    public static void printMatrix(int[][] mat) {
        int len = 0;
        for (int i = 0; i < mat.length; i++) {
            len = Math.max(len, mat[i].length);
        }
        int[] width = new int[len];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; ++j) {
                width[j] = Math.max(width[j], getStrLen(mat[i][j]));
            }
        }
        printMatrix(mat, width);
    }

    public static void printMatrix(int[][] mat, boolean turn) {
        if (turn) {
            mat = ArraySup.turnMatrix(mat);
        }
        printMatrix(mat);
    }

    /**
     * just like this method whose arguments is int[][],but the
     *
     * @param mat
     * @param setWidth
     */
    public static void printMatrix(double[][] mat, int[] setWidth, DecimalFormat df) {
//		
        int len = 0;
        int t0 = 0;
        len = getSum(setWidth) + setWidth.length + 1 << 3;// add up to it: both side of the line should have fraction and space
        StringBuilder sb = new StringBuilder(len * mat.length);
        for (int i = 0; i < mat.length; i++) {
            if (i == 0)
                sb.append('┌');
            else if (i == mat.length - 1)
                sb.append('└');
            else
                sb.append('│');
            sb.append(' ');

            int j = 0;
            for (; j < mat[i].length; j++) {
                String temp = df.format(mat[i][j]);
                sb.append(temp);
                int t = setWidth[j] - temp.length();
                if (t < 0)
                    throw new IllegalArgumentException("Width " + setWidth + " is too small for" + temp);
                fillBlank(sb, t + 1);
            }

            t0 = getSum(setWidth, j, setWidth.length) + setWidth.length - j;
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
        print(sb.toString());

    }

    public static void printMatrix(double[][] mat, DecimalFormat df) {
        printMatrix0(mat, df);


    }


    /**
     * A convenient print method which makes strings representing the objects are right-justified.
     *
     * @param mat
     */
    public static void printMatrix(Object[][] mat) {
        print(StringSup.formatMatrix(ArraySup.mapTo2(mat, Object::toString, String.class)));

    }


    private static final int[] tens = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 10000000, 100000000, 1000000000, Integer.MAX_VALUE};


    /**
     * print the double numbers with limited digit number.
     *
     * @param obj
     * @param shownDigit
     */
    public static void print(double[] obj, int shownDigit, PrintStream ps) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        getMiddle(sb, obj, shownDigit);
        sb.append(']');
        print(sb.toString());
    }


    public static void print_(PrintStream ps) {
        print_(16);
    }

    public static void print_(int count, PrintStream ps) {
        char[] buf = new char[count];
        for (int i = 0; i < count; i++) {
            buf[i] = '-';
        }
        out.println(buf);
    }

    /**
     * print the char c for {@code count} times and decides whether to change line.
     *
     * @param count
     * @param c
     * @param nb
     */
    public static void print_(int count, char c, boolean nb, PrintStream ps) {
        char[] buf = new char[count];
        for (int i = 0; i < count; i++) {
            buf[i] = c;
        }
        if (nb) {
            out.print(buf);
        } else {
            out.println(buf);
        }

    }

    /**
     * print the matrix and the method will choose the best way to show it
     * a Matrix should be shown like following
     * ┌ 1 2 3 4 5 6 7 ┐
     * │ 2 3 4 5 6 7 8 │
     * └ 3 4 5 2 0 8 9 ┘
     * the main part is like :
     * 23  45 23 31 32 312241241 ┐
     * 231 23 34 23 13 23        │
     * 012 78 62 -1 3  2         ┘
     *
     * @param mat
     * @param setWidth : the size for each integer
     * @throws IllegalArgumentException: if the width is less than the width that one of the integer should have
     */
    public static void printMatrix(int[][] mat, int setWidth, PrintStream ps) {
        int maxCount = 0;
        for (int[] arr : mat) {
            maxCount = Math.max(arr.length, maxCount);
        }
        int[] width = fillArr(maxCount, setWidth);
        printMatrix(mat, width);

    }

    /**
     * Print the given matrix.The matrix will be printed properly and make sure number in each column is aligned.
     *
     * @param mat
     */
    public static <T> void printMatrix(Matrix<T> mat, PrintStream ps) {
        if (mat != null)
            printMatrix(mat.getValues());
        else
            print("Null");
    }


    private static void printMatrix0(double[][] mat, DecimalFormat df) {
        String[][] ma = new String[mat.length][];
        int co = 0;
        for (int i = 0; i < mat.length; i++) {
            co = Math.max(co, mat[i].length);
        }
        int[] width = new int[co];
        for (int i = 0; i < mat.length; i++) {
            ma[i] = new String[mat[i].length];
            for (int j = 0; j < mat[i].length; ++j) {
                ma[i][j] = df.format(mat[i][j]);
                //get the String stored and calculate the best length
                width[j] = Math.max(ma[i][j].length(), width[j]);
            }
        }
        int len = getSum(width) + co + 1 << 3;// add up to it: both side of the line should have fraction and space

        StringBuilder sb = new StringBuilder(len * mat.length);
        int t0 = 0;
        for (int i = 0; i < mat.length; i++) {
            if (i == 0)
                sb.append('┌');
            else if (i == mat.length - 1)
                sb.append('└');
            else
                sb.append('│');
            sb.append(' ');

            int j = 0;
            for (; j < mat[i].length; j++) {
                sb.append(ma[i][j]);
                int t = width[j] - ma[i][j].length();
                fillBlank(sb, t + 1);
            }

            t0 = getSum(width, j, width.length) + width.length - j;
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
        print(sb.toString());
    }


    private static void fillBlank(StringBuilder sb, int blankCount) {
        for (int i = 0; i < blankCount; ++i) {
            sb.append(' ');
        }
    }

    private static int getStrLen(int t) {
        if (t == 0)
            return 1;

        int len = 0;

        if (t < 0)
            len++;
        t = Math.abs(t);
        for (int i = 0; i < tens.length; i++) {
            if (t < tens[i]) {
                len += i;
                break;
            }
        }
        return len;
    }

    /**
     * Gets an instance of the printer,which has all the static methods.
     *
     * @param ps a printer instance.
     * @return
     */
    public static PrinterIns getInstance(OutputStream ps) {
        return new PrinterIns(ps);
    }

    /**
     * Gets an instance of the printer,which has all the static methods.
     *
     * @param ps a printer instance.
     * @return
     */
    public static PrinterIns getInstance(Writer w) {
        return new PrinterIns(w);
    }

    /**
     * An printer instance that is used for multiple output tasks.
     *
     * @author lyc
     */
    public static class PrinterIns {
        private final PrintWriter out;

        /**
         * @param pw
         */
        private PrinterIns(OutputStream pw) {
            out = new PrintWriter(pw, true);
        }

        /**
         * @param pw
         */
        private PrinterIns(Writer pw) {
            out = new PrintWriter(pw, true);
        }

        /**
         * System.out.println(obj.toString)
         *
         * @param obj
         */
        public void print(Object obj) {
            out.println(obj);
        }

        public void print() {
            out.println();
        }

        /**
         * System.out.println(Arrays.toString(obj))
         *
         * @param obj
         */
        public void print(Object[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(double num) {
            print(SNFSupport.DF.format(num));
        }

        public void print(int n) {
            out.println(n);
        }

        public void print(long n) {
            out.println(n);
        }

        public void print(char c) {
            out.println(c);
        }

        public void printf(String format, Object... args) {
            out.printf(format, args);
        }

        public void print(int[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(byte[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(char[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(long[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(float[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(double[] obj) {
            out.println(Arrays.toString(obj));
        }

        /**
         * print the double numbers with limited digit number.
         *
         * @param obj
         * @param shownDigit
         */
        public void print(double[] obj, int shownDigit) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            getMiddle(sb, obj, shownDigit);
            sb.append(']');
            print(sb.toString());
        }

        private StringBuilder getMiddle(StringBuilder sb, double[] obj, int shownDigit) {
            DecimalFormat df = SNFSupport.dfByDigit(shownDigit);
            for (int i = 0; i < obj.length; i++) {
                sb.append(df.format(obj[i]));
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            return sb;
        }

        public void print(short[] obj) {
            out.println(Arrays.toString(obj));
        }

        public void print(boolean[] obj) {
            out.println(Arrays.toString(obj));
        }


        public void printnb(Object obj) {
            out.print(obj);
        }

        public void print_() {
            print_(16);
        }

        public void print_(int count) {
            char[] buf = new char[count];
            for (int i = 0; i < count; i++) {
                buf[i] = '-';
            }
            out.println(buf);
        }

        /**
         * print the char c for {@code count} times and decides whether to change line.
         *
         * @param count
         * @param c
         * @param nb
         */
        public void print_(int count, char c, boolean nb) {
            char[] buf = new char[count];
            for (int i = 0; i < count; i++) {
                buf[i] = c;
            }
            if (nb) {
                out.print(buf);
            } else {
                out.println(buf);
            }

        }

        /**
         * print the matrix and the method will choose the best way to show it
         * a Matrix should be shown like following
         * ┌ 1 2 3 4 5 6 7 ┐
         * │ 2 3 4 5 6 7 8 │
         * └ 3 4 5 2 0 8 9 ┘
         * <p>
         * ┌ 23  45 23 31 32 312241241 ┐
         * │ 231 23 34 23 13 23        │
         * └ 012 78 62 -1 3  2         ┘
         *
         * @param mat
         * @param setWidth : the size for each integer
         * @throws IllegalArgumentException: if the width is less than the width that one of the integer should have
         */
        public void printMatrix(int[][] mat, int setWidth) {
            int maxCount = 0;
            for (int[] arr : mat) {
                maxCount = Math.max(arr.length, maxCount);
            }
            int[] width = fillArr(maxCount, setWidth);
            printMatrix(mat, width);
        }

        /**
         * Print the given matrix.The matrix will be printed properly and make sure number in each column is aligned.
         *
         * @param mat
         */
        public void printMatrix(Matrix<Fraction> mat) {
            if (mat != null)
                printMatrix(mat.getValues());
            else
                print("Null");
        }


        /**
         * print the Matrix by given width for each column
         *
         * @param mat
         * @param setWidth
         * @throws IllegalArgumentException: if the width is less than the width that one of the integer should have
         */
        public void printMatrix(int[][] mat, int[] setWidth) {
            String[][] ma = ArraySup.mapTo2(mat, Integer::toString, String.class);
            print(StringSup.formatMatrix(ma, setWidth));
        }

        /**
         * Print the given matrix.The matrix will be printed properly and make sure number in each column is aligned.
         *
         * @param mat
         */
        public void printMatrix(int[][] mat) {
            String[][] ma = ArraySup.mapTo2(mat, Integer::toString, String.class);
            print(StringSup.formatMatrix(ma));
        }

        public void printMatrix(int[][] mat, boolean turn) {
            if (turn) {
                mat = ArraySup.turnMatrix(mat);
            }
            printMatrix(mat);
        }

        /**
         * just like this method whose arguments is int[][],but the
         *
         * @param mat
         * @param setWidth
         */
        public void printMatrix(double[][] mat, int[] setWidth, DecimalFormat df) {
            String[][] ma = ArraySup.mapTo2(mat, df::format, String.class);
            print(StringSup.formatMatrix(ma, setWidth));
        }

        public void printMatrix(double[][] mat, DecimalFormat df) {
            String[][] ma = ArraySup.mapTo2(mat, df::format, String.class);
            print(StringSup.formatMatrix(ma));
        }


        /**
         * A convenient print method which makes strings representing the objects are right-justified.
         *
         * @param mat
         */
        public void printMatrix(Object[][] mat) {
            String[][] ma = ArraySup.mapTo2(mat, Object::toString, String.class);
            print(StringSup.formatMatrix(ma));
        }


    }

//    public static void main(String[] args) {
////		double[][] mat = new double[4][];
////		for(int i=0;i<mat.length;i++){
////			mat[i] = ArraySup.ranDoubleArr(4,10);
////		}
////		DecimalFormat df = new DecimalFormat("0.###");
////		printMatrix(mat,df);
////		print(mat[0][2]);
//        print(Math.round(2.4d));
//    }


}

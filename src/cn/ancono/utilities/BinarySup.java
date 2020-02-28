package cn.ancono.utilities;

import java.util.BitSet;

public class BinarySup {
    private static final byte MASK = 0x0F;
    private static final char[] list_hex =
            "0123456789abcdef".toCharArray();

    /**
     * Converts the array to Hex String,with leading zeros.
     *
     * @param bytes
     * @return
     */
    public static String convertToHex(byte[] bytes) {
        int len = bytes.length;
        char[] re = new char[len * 2];
        for (int i = 0; i < len; i++) {
            re[2 * i + 1] = list_hex[bytes[i] & MASK];
            re[2 * i] = list_hex[(bytes[i] >>> 4) & MASK];
        }
        return String.copyValueOf(re);
    }

    /**
     * Converts the array to Hex String,with spliterator and a given part length.
     *
     * @param bytes       an byte array
     * @param spliterator the spliterator.
     * @param partLength  the length of hex numbers for every part.
     * @return a String.
     */
    public static String convertToHexWithSplit(byte[] bytes, char spliterator, int partLength) {
        if (partLength <= 0) {
            throw new IllegalArgumentException("Illegal part length");
        }
        int len = bytes.length;

        char[] re = new char[len * 2 + len * 2 / partLength + 1];
        int p = 0;
        for (int i = 0, c = 0; i < len; i++, c++) {
            if (c == partLength) {
                c = 0;
                re[p++] = spliterator;
            }
            re[p++] = list_hex[(bytes[i] >>> 4) & MASK];
            c++;
            if (c == partLength) {
                c = 0;
                re[p++] = spliterator;
            }
            re[p++] = list_hex[bytes[i] & MASK];
        }
        return String.copyValueOf(re, 0, p);
    }

    /**
     * Converts the long array to Hex String ,with leading zeros.
     *
     * @param arr
     * @return
     */
    public static String convertToHex(long[] arr) {
        int len = arr.length;
        char[] re = new char[len * 16];
        int pos;
        for (int i = 0; i < len; i++) {
            pos = i * 16;
            for (int j = 0; j < 16; j++) {
                re[pos + j] = list_hex[(int) ((arr[i] >>> (60 - j * 4) & MASK))];
            }
        }
        return String.copyValueOf(re);
    }

    /**
     * Converts the long array to Hex String,with spliterator and a given part length.
     *
     * @param arr         an long array
     * @param spliterator the spliterator.
     * @param partLength  the length of hex numbers for every part.
     * @return a String.
     */
    public static String convertToHexWithSplit(long[] arr, char spliterator, int partLength) {
        if (partLength <= 0) {
            throw new IllegalArgumentException("Illegal part length");
        }
        int len = arr.length;
        char[] re = new char[len * 16 + len * 16 / partLength + 1];
        int pos = 0;
        for (int i = 0, c = 0; i < len; i++) {
            for (int j = 0; j < 16; j++, c++) {
                if (c == partLength) {
                    c = 0;
                    re[pos++] = spliterator;
                }
                re[pos++] = list_hex[(int) ((arr[i] >>> (60 - j * 4) & MASK))];
            }
        }
        return String.copyValueOf(re, 0, pos);
    }


    /**
     * Converts the BitSet to Hex String,this method is equal to {@code convertToHex(bits.toByteArray)}.
     *
     * @param bits
     * @return
     */
    public static String convertToHex(BitSet bits) {
        byte[] arr = bits.toByteArray();
        return convertToHex(arr);
    }

    /**
     * Converts to byte array to a long array,the new long array will have a length of
     * {@code (arr.length+7)/8},and the bits' order in the {@code arr} and the returned long
     * array is the identity,but there may be some remaining empty bits at the end.
     * <p>
     * For example,byte array {0x0f,0x0f,0x0f} will have a returned long for {@code f0f0f00000000000}
     *
     * @param arr an array.
     * @return an new array,with the identity order of bits.
     */
    public static long[] byteToLong(byte[] arr) {
        //long = 64 bits, byte = 8 bits.
        long[] re = new long[(arr.length + 7) / 8];
        for (int i = 0, p = 0, r = 0; i < arr.length; i++, r++) {
            if (r == 16) {
                r = 0;
                p++;
            }
            re[p] |= ((long) arr[i]) << (60 - r * 8);
            //p in the long array
        }
        return re;
    }

    public static void bitSetInc(BitSet bs) {
        int index = 0;
        while (bs.get(index)) {
            index++;
        }
        bs.set(index);
        bs.clear(0, index);
    }


    public static boolean bitOf(long n, int index) {
        return (n & (1 << index)) != 0;
    }

    public static long setBitOf(long n, int index) {
        return (n | (1 << index));
    }

//    public static void main(String[] args) {
//        print(bitOf(8,4));
//    }


//	public static void main(String[] args) {
//		byte[] bytes = new byte[]{0x0f,0x0f,0x0f,0,
//				0,0,0,0,
//				0,0,0,0,
//				0,0,0,0,
//				0,0,0,0,
//				0,0,0,0x0f};
//		Printer.print(convertToHexWithSplit(bytes, '|', 4));
//		Printer.print(bytes);
//		long[] l = {Long.parseUnsignedLong("abcd1232", 16)};
//		Printer.print(convertToHexWithSplit(byteToLong(bytes),'|',4));
//		for(byte b : bytes){
//			printnb((long)b);
//		}
//	}
}

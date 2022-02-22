/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;
    private static final int W = 8;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] chars = new char[R];
        int[] idx = new int[R];
        for (int i = 0; i < R; i++) {
            chars[i] = (char) i;
            idx[chars[i]] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char in = BinaryStdIn.readChar(W);
            int out = idx[in];
            BinaryStdOut.write(out, W);

            for (int i = out; i >= 1; i--) {
                chars[i] = chars[i - 1];
                idx[chars[i]] = i;
            }
            chars[0] = in;
            idx[chars[0]] = 0;
        }
        BinaryStdOut.close();

    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] chars = new char[R];
        for (int i = 0; i < R; i++) {
            chars[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            int in = BinaryStdIn.readInt(W);
            char out = chars[in];
            BinaryStdOut.write(out, W);

            for (int i = in; i >= 1; i--) {
                chars[i] = chars[i - 1];
            }
            chars[0] = out;

        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        if (args[0].equals("+")) decode();
    }

}

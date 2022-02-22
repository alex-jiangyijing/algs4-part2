/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;
    private static final int W = 8;


    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        int N = circularSuffixArray.length();

        char[] out = new char[N];
        for (int i = 0; i < N; i++) {
            int shift = circularSuffixArray.index(i);
            out[i] = s.charAt((shift + N - 1) % N);
            if (shift == 0) BinaryStdOut.write(i, 4 * W);
        }
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(out[i], W);
        }
        BinaryStdOut.close();
    }


    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt(4 * W);
        String t = BinaryStdIn.readString();
        int N = t.length();

        int[] next = new int[N];
        char[] aux = new char[N];
        int[] count = new int[R + 1];
        for (int i = 0; i < N; i++) {
            count[t.charAt(i) + 1]++;
        }
        for (int r = 0; r < R; r++) {
            count[r + 1] += count[r];
        }
        for (int i = 0; i < N; i++) {
            aux[count[t.charAt(i)]] = t.charAt(i);
            next[count[t.charAt(i)]] = i;
            count[t.charAt(i)]++;
        }

        int cur = first;
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(aux[cur], W);
            cur = next[cur];
        }
        BinaryStdOut.close();


    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        if (args[0].equals("+")) inverseTransform();
    }


}

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private static final int R = 256;
    private final int N;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        N = s.length();
        index = new int[N];
        for (int i = 0; i < N; i++) {
            index[i] = i;
        }
        int[] aux = new int[N];
        sort(s, aux, 0, N - 1, 0);

    }

    private void sort(String s, int[] aux, int lo, int hi, int d) {
        if (lo >= hi) return;
        if (d >= N) return;
        int[] count = new int[R + 2];
        for (int i = lo; i <= hi; i++) {
            count[s.charAt((index[i] + d) % N) + 2]++;
        }
        for (int r = 0; r < R; r++) {
            count[r + 1] += count[r];
        }
        for (int i = lo; i <= hi; i++) {
            aux[count[s.charAt((index[i] + d) % N) + 1]++] = index[i];
        }
        for (int i = lo; i <= hi; i++) {
            index[i] = aux[i - lo];
        }

        for (int r = 0; r < R; r++) {
            sort(s, aux, lo + count[r], lo + count[r + 1] - 1, d + 1);
        }

    }

    // length of s
    public int length() {
        return N;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if ((i < 0) || (i > (N - 1))) throw new IllegalArgumentException();
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        String s = in.readAll();
        StdOut.println(s);
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        int n = circularSuffixArray.length();
        StdOut.println(n);
        for (int i = 0; i < n; i++) {
            StdOut.println(circularSuffixArray.index(i));
        }

    }

}

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;


public class BoggleSolver {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    private static final int R = 26;
    private Node root = new Node();

    private int rows;
    private int cols;
    private boolean[][] marked;
    private Set<String> validWords;

    private Cube[][] adj;
    private char[][] charBoard;

    public BoggleSolver(String[] dictionary) {
        int N = dictionary.length;
        for (int i = 0; i < N; i++) {
            root = put(root, dictionary[i], 1, 0);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        rows = board.rows();
        cols = board.cols();
        precomputeAdj(board);
        validWords = new HashSet<String>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dfs(i, j);
            }
        }
        return validWords;

    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        Node x = get(root, word, 0);
        if (x == null) return 0;
        if (x.value != 1) return 0;
        
        int n = word.length();
        if (n < 3) return 0;
        if (n <= 4) return 1;
        if (n == 5) return 2;
        if (n == 6) return 3;
        if (n == 7) return 5;
        return 11;
    }

    private void dfs(int i, int j) {
        marked = new boolean[rows][cols];
        dfs(new StringBuilder(), i, j, root);
    }

    private void dfs(StringBuilder pre, int i, int j, Node x) {
        char c = charBoard[i][j];

        Node y = x.next[c - 'A'];
        if (y == null) return;

        if (c == 'Q') {
            y = y.next['U' - 'A'];
            if (y == null) return;

            pre.append("QU");
        }
        else pre.append(c);


        String word = pre.toString();

        if ((word.length() >= 3) && (y.value == 1)) {

            validWords.add(word);
        }

        marked[i][j] = true;
        for (int idx : adj[i][j].neighbor) {
            int iNext = idx / cols;
            int jNext = idx % cols;
            if (!marked[iNext][jNext]) dfs(new StringBuilder(pre), iNext, jNext, y);
        }
        marked[i][j] = false;

    }

    private static class Cube {
        private Set<Integer> neighbor = new HashSet<Integer>();
    }

    private void precomputeAdj(BoggleBoard board) {
        charBoard = new char[rows][cols];
        adj = new Cube[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                charBoard[i][j] = board.getLetter(i, j);
                adj[i][j] = new Cube();
                // up, left up
                if (i != 0) {
                    adj[i][j].neighbor.add((i - 1) * cols + j);
                    if (j != 0) {
                        adj[i][j].neighbor.add((i - 1) * cols + j - 1);
                    }
                }
                // down, right down
                if (i != (rows - 1)) {
                    adj[i][j].neighbor.add((i + 1) * cols + j);
                    if (j != (cols - 1)) {
                        adj[i][j].neighbor.add((i + 1) * cols + j + 1);
                    }
                }
                // left, left down
                if (j != 0) {
                    adj[i][j].neighbor.add(i * cols + j - 1);
                    if (i != (rows - 1)) {
                        adj[i][j].neighbor.add((i + 1) * cols + j - 1);
                    }
                }
                // right,right up
                if (j != (cols - 1)) {
                    adj[i][j].neighbor.add(i * cols + j + 1);
                    if (i != 0) {
                        adj[i][j].neighbor.add((i - 1) * cols + j + 1);
                    }
                }
            }
        }
    }

    private static class Node {
        private int value;
        private Node[] next = new Node[R];
    }


    private Node put(Node x, String key, int val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.value = val;
            return x;
        }
        char c = key.charAt(d);
        x.next[c - 'A'] = put(x.next[c - 'A'], key, val, d + 1);
        return x;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - 'A'], key, d + 1);
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;

        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}



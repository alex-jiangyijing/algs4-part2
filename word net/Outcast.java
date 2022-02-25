import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }        // constructor takes a WordNet object

    public String outcast(String[] nouns) {
        int dist = Integer.MIN_VALUE;
        String result = null;
        for (String noun : nouns) {
            int temp = 0;
            for (String other : nouns) {
                temp = temp + wordNet.distance(noun, other);
            }
            if (temp > dist) {
                dist = temp;
                result = noun;
            }
        }
        return result;
    }  // given an array of WordNet nouns, return an outcast

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }  // see test client below
}

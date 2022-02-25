import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class WordNet {
    private final HashMap<Integer, String> intToStr = new HashMap<Integer, String>();
    private final HashMap<String, Set<Integer>> strToInt = new HashMap<String, Set<Integer>>();
    private final Digraph graph;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if ((synsets == null) || (hypernyms == null)) throw new IllegalArgumentException();
        In syn = new In(synsets);
        while (syn.hasNextLine()) {
            String[] synLine = syn.readLine().split(",");
            int id = Integer.parseInt(synLine[0]);
            intToStr.put(id, synLine[1]);

            String[] words = synLine[1].split(" ");
            for (String word : words) {
                if (!strToInt.containsKey(word)) strToInt.put(word, new HashSet<Integer>());
                strToInt.get(word).add(id);
            }
        }

        int v = intToStr.size();
        graph = new Digraph(v);


        In hyper = new In(hypernyms);
        while (hyper.hasNextLine()) {
            String[] hyperLine = hyper.readLine().split(",");
            if (hyperLine.length < 2) continue;
            int id = Integer.parseInt(hyperLine[0]);
            for (int i = 1; i < hyperLine.length; i++) {
                int u = Integer.parseInt(hyperLine[i]);
                graph.addEdge(id, u);
            }

        }
        if (!rootedDAG()) throw new IllegalArgumentException();
        sap = new SAP(graph);
    }

    private boolean rootedDAG() {

        ArrayList<Integer> rootArr = new ArrayList<Integer>();
        for (int i = 0; i < graph.V(); i++) {
            if (graph.outdegree(i) == 0) rootArr.add(i);
        }

        if ((rootArr.isEmpty()) || (rootArr.size() >= 2)) return false;

        DirectedCycle cycle = new DirectedCycle(graph);
        if (cycle.hasCycle()) return false;
        return true;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return strToInt.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return strToInt.containsKey(word);
    }


    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if ((nounA == null) || (nounB == null)) throw new IllegalArgumentException();
        if ((!isNoun(nounA)) || (!isNoun(nounB))) throw new IllegalArgumentException();
        if ((!strToInt.containsKey(nounA)) || (!strToInt.containsKey(nounB)))
            throw new IllegalArgumentException();
        Set<Integer> synsetsA = strToInt.get(nounA);
        Set<Integer> synsetsB = strToInt.get(nounB);

        return sap.length(synsetsA, synsetsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if ((nounA == null) || (nounB == null)) throw new IllegalArgumentException();
        if ((!isNoun(nounA)) || (!isNoun(nounB))) throw new IllegalArgumentException();
        if ((!strToInt.containsKey(nounA)) || (!strToInt.containsKey(nounB)))
            throw new IllegalArgumentException();
        Set<Integer> synsetsA = strToInt.get(nounA);
        Set<Integer> synsetsB = strToInt.get(nounB);

        return intToStr.get(sap.ancestor(synsetsA, synsetsB));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        /*
        WordNet wordNet = new WordNet(args[0], args[1]);
        //System.out.println(wordNet.nouns());
        System.out.println(wordNet.distance("gluten", "factor_XI"));
        System.out.println(wordNet.sap("gluten", "factor_XI"));
        */

    }
}

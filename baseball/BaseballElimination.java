/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {
    private final int numberOfTeams;
    private final HashMap<String, Integer> teams = new HashMap<String, Integer>();
    private final HashMap<Integer, String> intToString = new HashMap<Integer, String>();
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;

    private boolean mathElim;
    private String mathElimCertificate;
    private int[] shift;
    private FordFulkerson fordFulkerson;


    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n = in.readInt();

        this.numberOfTeams = n;

        this.wins = new int[n];
        this.losses = new int[n];
        this.remaining = new int[n];
        this.against = new int[n][n];


        for (int i = 0; i < n; i++) {
            String team = in.readString();
            this.teams.put(team, i);
            this.intToString.put(i, team);

            this.wins[i] = in.readInt();

            this.losses[i] = in.readInt();
            this.remaining[i] = in.readInt();
            for (int j = 0; j < n; j++) {
                this.against[i][j] = in.readInt();
            }
        }


    }                 // create a baseball division from given filename in format specified below

    public int numberOfTeams() {
        return this.numberOfTeams;
    }                    // number of teams

    public Iterable<String> teams() {
        return this.teams.keySet();
    }                             // all teams

    public int wins(String team) {
        if (!this.teams.containsKey(team)) throw new IllegalArgumentException();

        return this.wins[this.teams.get(team)];

    }                    // number of wins for given team

    public int losses(String team) {
        if (!this.teams.containsKey(team)) throw new IllegalArgumentException();

        return this.losses[this.teams.get(team)];
    }                 // number of losses for given team

    public int remaining(String team) {
        if (!this.teams.containsKey(team)) throw new IllegalArgumentException();

        return this.remaining[this.teams.get(team)];
    }              // number of remaining games for given team

    public int against(String team1, String team2) {
        if (!this.teams.containsKey(team1)) throw new IllegalArgumentException();
        if (!this.teams.containsKey(team2)) throw new IllegalArgumentException();

        return this.against[this.teams.get(team1)][this.teams.get(team2)];
    }  // number of remaining games between team1 and team2

    public boolean isEliminated(String team) {
        if (!this.teams.containsKey(team)) throw new IllegalArgumentException();

        double maxCap = this.wins(team) + this.remaining(team);
        for (String t : this.teams()) {
            if ((!team.equals(t)) && (maxCap < this.wins(t))) {
                this.mathElim = true;
                this.mathElimCertificate = t;
                return true;
            }
        }

        this.mathElim = false;
        int ix = this.teams.get(team);

        int n = this.numberOfTeams;
        int m = (n - 1) * (n - 2) / 2;
        FlowNetwork flowNetwork = new FlowNetwork(2 + (n - 1) + m);

        this.shift = new int[n - 1];
        for (int i = 0; i < n; i++) {
            if (i < ix) this.shift[i] = i;
            if (i > ix) this.shift[i - 1] = i;
        }

        int count = 1;
        double full = 0.0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n - 1; j++) {

                String team1 = this.intToString.get(this.shift[i]);
                String team2 = this.intToString.get(this.shift[j]);
                double g = this.against(team1, team2);
                full += g;

                FlowEdge flowEdge = new FlowEdge(0, count, g);
                flowNetwork.addEdge(flowEdge);

                FlowEdge flowEdge1 = new FlowEdge(count, (m + 1) + i, Double.POSITIVE_INFINITY);
                flowNetwork.addEdge(flowEdge1);
                FlowEdge flowEdge2 = new FlowEdge(count, (m + 1) + j, Double.POSITIVE_INFINITY);
                flowNetwork.addEdge(flowEdge2);

                count++;
            }
        }

        for (int i = 0; i < (n - 1); i++) {
            String t = this.intToString.get(this.shift[i]);

            FlowEdge flowEdge = new FlowEdge((m + 1) + i, 1 + (n - 1) + m, maxCap - this.wins(t));
            flowNetwork.addEdge(flowEdge);
        }

        this.fordFulkerson = new FordFulkerson(flowNetwork, 0, 1 + m + (n - 1));

        double value = this.fordFulkerson.value();

        return value < full;

    }          // is given team eliminated?

    public Iterable<String> certificateOfElimination(String team) {
        if (!this.teams.containsKey(team)) throw new IllegalArgumentException();

        if (!this.isEliminated(team)) return null;

        Stack<String> certificate = new Stack<String>();
        if (this.mathElim) {
            certificate.push(this.mathElimCertificate);
            return certificate;
        }

        int n = this.numberOfTeams;
        int m = (n - 1) * (n - 2) / 2;

        for (int i = 0; i < n - 1; i++) {
            if (this.fordFulkerson.inCut((m + 1) + i)) {
                certificate.push(this.intToString.get(this.shift[i]));
            }
        }
        return certificate;

    } // subset R of teams that eliminates given team; null if not eliminated

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {

            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}

import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /** Mission 1. Take walls in file order and break one only if it joins two different components. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int used = 0;
        for (int k = 0; k < w.count() && used < chosen.length; k++) {
            if (dsu.union(w.a()[k], w.b()[k])) chosen[used++] = k;
        }
        return Arrays.copyOf(chosen, used);
    }

    /** Mission 2. Kruskal: process walls from cheapest to most expensive. */
    public static int[] cheapestMaze(Input.Walls w) {
        long[] order = new long[w.count()];
        for (int i = 0; i < order.length; i++) {
            order[i] = ((long) w.cost()[i] << 32) | (i & 0xffffffffL);
        }
        Arrays.sort(order);

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int used = 0;
        for (long key : order) {
            int k = (int) key;
            if (dsu.union(w.a()[k], w.b()[k])) {
                chosen[used++] = k;
                if (used == chosen.length) break;
            }
        }
        return Arrays.copyOf(chosen, used);
    }

    /** Mission 2. Prim with a priority queue. Returns the total MST cost. */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n <= 1) return 0;

        int[] degree = new int[n];
        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++;
            degree[w.b()[k]]++;
        }
        int[][] incident = new int[n][];
        for (int i = 0; i < n; i++) incident[i] = new int[degree[i]];
        int[] fill = new int[n];
        for (int k = 0; k < w.count(); k++) {
            incident[w.a()[k]][fill[w.a()[k]]++] = k;
            incident[w.b()[k]][fill[w.b()[k]]++] = k;
        }

        boolean[] inTree = new boolean[n];
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(k -> w.cost()[k]));
        inTree[0] = true;
        for (int k : incident[0]) pq.add(k);

        long total = 0;
        int added = 1;
        while (!pq.isEmpty() && added < n) {
            int k = pq.poll();
            int a = w.a()[k], b = w.b()[k];
            if (inTree[a] && inTree[b]) continue;
            int next = inTree[a] ? b : a;
            if (inTree[next]) continue;
            inTree[next] = true;
            total += w.cost()[k];
            added++;
            for (int e : incident[next]) {
                int other = w.a()[e] == next ? w.b()[e] : w.a()[e];
                if (!inTree[other]) pq.add(e);
            }
        }
        return total;
    }
}

import java.util.*;

/** Missions 4 and 5. Strongly connected slide zones and routes in the zone DAG. */
public class Zones {

    private static void finishDfs(int start, Input.Slides s, int[][] out, boolean[] seen, int[] finish, int[] fp) {
        int n = s.n();
        int[] stack = new int[n];
        int[] next = new int[n];
        int top = 0;
        stack[0] = start;
        seen[start] = true;

        while (top >= 0) {
            int u = stack[top];
            if (next[top] < out[u].length) {
                int slide = out[u][next[top]++];
                int v = s.to()[slide];
                if (!seen[v]) {
                    seen[v] = true;
                    stack[++top] = v;
                    next[top] = 0;
                }
            } else {
                finish[fp[0]++] = u;
                top--;
            }
        }
    }

    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] finish = new int[n];
        int[] fp = {0};

        for (int v = 0; v < n; v++) if (!seen[v]) finishDfs(v, s, out, seen, finish, fp);

        int[] zone = new int[n];
        Arrays.fill(zone, -1);
        int zoneId = 0;
        int[] stack = new int[n];

        for (int pos = n - 1; pos >= 0; pos--) {
            int start = finish[pos];
            if (zone[start] != -1) continue;
            int top = 0;
            stack[0] = start;
            zone[start] = zoneId;
            while (top >= 0) {
                int u = stack[top--];
                for (int slide : in[u]) {
                    int v = s.from()[slide];
                    if (zone[v] == -1) {
                        zone[v] = zoneId;
                        stack[++top] = v;
                    }
                }
            }
            zoneId++;
        }
        return zone;
    }

    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        if (zoneCount == 0) return new long[]{0, 0};

        @SuppressWarnings("unchecked")
        ArrayList<int[]>[] edges = new ArrayList[zoneCount];
        for (int z = 0; z < zoneCount; z++) edges[z] = new ArrayList<>();
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a != b) edges[a].add(new int[]{b, s.time()[i]});
        }

        long[] longest = new long[zoneCount];
        Arrays.fill(longest, 1);
        long[] dist = new long[zoneCount];
        long INF = Long.MAX_VALUE / 4;
        Arrays.fill(dist, INF);
        int start = zoneOf[0], target = zoneOf[s.n() - 1];
        dist[start] = 0;

        long flowStages = 1;
        for (int z = 0; z < zoneCount; z++) {
            flowStages = Math.max(flowStages, longest[z]);
            for (int[] e : edges[z]) {
                int to = e[0], cost = e[1];
                longest[to] = Math.max(longest[to], longest[z] + 1);
                flowStages = Math.max(flowStages, longest[to]);
                if (dist[z] != INF) dist[to] = Math.min(dist[to], dist[z] + cost);
            }
        }
        return new long[]{flowStages, dist[target]};
    }
}

import java.util.*;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * The search is iterative so a very large maze does not overflow the Java call stack.
 */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}

    private static boolean contains(int[] a, int x) {
        for (int v : a) if (v == x) return true;
        return false;
    }

    public static Result escape(Input.Walls w, int[] broken) {
        int[][] g = Graphs.open(w, broken);
        int n = w.cells(), target = n - 1;

        int deadEnds = 0;
        for (int[] neighbours : g) if (neighbours.length == 1) deadEnds++;

        boolean[] seen = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        int[] nextDir = new int[n];
        int[] stack = new int[n];
        int top = 0;
        stack[0] = 0;
        seen[0] = true;
        int visited = 1;

        while (top >= 0 && !seen[target]) {
            int u = stack[top];
            int dir = nextDir[u]++;
            if (dir >= 4) {
                top--;
                continue;
            }

            int v;
            if (dir == 0) v = u - w.cols();
            else if (dir == 1) v = u + 1;
            else if (dir == 2) v = u + w.cols();
            else v = u - 1;

            int r = u / w.cols(), c = u % w.cols();
            if (dir == 0 && r == 0) continue;
            if (dir == 1 && c + 1 >= w.cols()) continue;
            if (dir == 2 && r + 1 >= w.rows()) continue;
            if (dir == 3 && c == 0) continue;
            if (seen[v] || !contains(g[u], v)) continue;

            seen[v] = true;
            parent[v] = u;
            visited++;
            stack[++top] = v;
        }

        if (!seen[target]) return new Result(new int[0], visited, deadEnds);
        int len = 1;
        for (int x = target; x != 0; x = parent[x]) len++;
        int[] path = new int[len];
        int x = target;
        for (int i = len - 1; i >= 0; i--) {
            path[i] = x;
            if (x != 0) x = parent[x];
        }
        return new Result(path, visited, deadEnds);
    }
}

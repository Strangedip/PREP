import java.util.*;

/**
 * Critical Connections (Bridges) — LeetCode 1192
 * Tarjan DFS to find edges whose removal increases connected components.
 */
public class CriticalConnections {

    private int time;
    private int[] disc, low;
    private List<List<Integer>> result;
    private List<List<Integer>> graph;

    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (List<Integer> edge : connections) {
            int u = edge.get(0), v = edge.get(1);
            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        disc = new int[n];
        low = new int[n];
        Arrays.fill(disc, -1);
        result = new ArrayList<>();
        time = 0;

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, -1);
            }
        }
        return result;
    }

    private void dfs(int u, int parent) {
        disc[u] = low[u] = time++;

        for (int v : graph.get(u)) {
            if (v == parent) {
                continue;
            }
            if (disc[v] == -1) {
                dfs(v, u);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > disc[u]) {
                    result.add(Arrays.asList(Math.min(u, v), Math.max(u, v)));
                }
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    public static void main(String[] args) {
        CriticalConnections sol = new CriticalConnections();
        List<List<Integer>> edges = Arrays.asList(
                Arrays.asList(0, 1), Arrays.asList(1, 2),
                Arrays.asList(2, 0), Arrays.asList(1, 3));
        System.out.println(sol.criticalConnections(4, edges)); // [[1,3]]
    }
}

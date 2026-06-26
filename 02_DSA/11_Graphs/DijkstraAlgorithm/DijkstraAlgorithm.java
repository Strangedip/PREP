import java.util.*;

/**
 * Dijkstra's Shortest Path Algorithm
 * Find shortest paths from source to all vertices (non-negative weights).
 */
public class DijkstraAlgorithm {

    static class Edge {
        int to, weight;
        Edge(int to, int weight) { this.to = to; this.weight = weight; }
    }

    /** Adjacency list version — O((V + E) log V) */
    public int[] dijkstra(int n, List<List<Edge>> graph, int source) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], u = curr[1];
            if (d > dist[u]) continue;

            for (Edge e : graph.get(u)) {
                int newDist = d + e.weight;
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    pq.offer(new int[]{newDist, e.to});
                }
            }
        }
        return dist;
    }

    /** Matrix adjacency version */
    public int[] dijkstraMatrix(int[][] graph, int source) {
        int n = graph.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && (u == -1 || dist[v] < dist[u])) u = v;
            }
            if (dist[u] == Integer.MAX_VALUE) break;
            visited[u] = true;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[u][v] != 0) {
                    dist[v] = Math.min(dist[v], dist[u] + graph[u][v]);
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        DijkstraAlgorithm sol = new DijkstraAlgorithm();
        int n = 4;
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        graph.get(0).add(new Edge(1, 4));
        graph.get(0).add(new Edge(3, 2));
        graph.get(1).add(new Edge(2, 3));
        graph.get(3).add(new Edge(2, 1));
        System.out.println(Arrays.toString(sol.dijkstra(n, graph, 0))); // [0, 4, 5, 2]
    }
}

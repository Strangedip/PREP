import java.util.*;

/**
 * LeetCode 743: Network Delay Time
 *
 * You are given a network of n nodes, labeled from 1 to n. You are also given times,
 * a list of travel times as directed edges times[i] = [ui, vi, wi], where there is a
 * directed edge from node ui to node vi that takes wi time to traverse.
 *
 * You are starting at node k. Send a signal from k to all other nodes. How long will
 * it take for all nodes to receive the signal? If it is impossible for all nodes to
 * receive the signal, return -1.
 *
 * Time Complexity: O(E log V) with Dijkstra, O(V * E) with Bellman-Ford
 * Space Complexity: O(V + E) for adjacency list and priority queue
 */
public class NetworkDelayTime {

    /**
     * Approach 1: Dijkstra's Algorithm with Min-Heap
     * Single-source shortest paths from k. Answer is the maximum distance among all nodes.
     * Skip stale priority queue entries when a shorter path was already found.
     */
    public int networkDelayTimeDijkstra(int[][] times, int n, int k) {
        // Build adjacency list (nodes are 1-indexed)
        List<int[]>[] graph = new List[n + 1];
        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int[] edge : times) {
            int u = edge[0], v = edge[1], w = edge[2];
            graph[u].add(new int[]{v, w});
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        // Min-heap: (node, distance) — always expand closest unvisited node
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{k, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0], d = current[1];

            // Lazy deletion: skip if we already found a shorter path to u
            if (d > dist[u]) continue;

            for (int[] edge : graph[u]) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{v, dist[v]});
                }
            }
        }

        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                return -1; // Node i never received the signal
            }
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }

    /**
     * Approach 2: Bellman-Ford Algorithm
     * Relax all edges V-1 times. Handles negative weights (not needed for LC 743,
     * but demonstrates knowledge of SSSP alternatives in interviews).
     */
    public int networkDelayTimeBellmanFord(int[][] times, int n, int k) {
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        // V-1 relaxation rounds guarantee shortest paths in graphs without negative cycles
        for (int round = 0; round < n - 1; round++) {
            for (int[] edge : times) {
                int u = edge[0], v = edge[1], w = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                }
            }
        }

        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                return -1;
            }
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }

    /**
     * Approach 3: Dijkstra with Explicit Visited Set
     * Alternative formulation that marks nodes visited after finalizing their distance.
     * Equivalent correctness; avoids stale entries by never re-processing a node.
     */
    public int networkDelayTimeDijkstraVisited(int[][] times, int n, int k) {
        List<int[]>[] graph = new List[n + 1];
        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int[] edge : times) {
            graph[edge[0]].add(new int[]{edge[1], edge[2]});
        }

        int[] dist = new int[n + 1];
        boolean[] visited = new boolean[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{k, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];

            if (visited[u]) continue;
            visited[u] = true;

            for (int[] edge : graph[u]) {
                int v = edge[0], w = edge[1];
                if (!visited[v] && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{v, dist[v]});
                }
            }
        }

        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }

    /** Default entry point — delegates to Dijkstra. */
    public int networkDelayTime(int[][] times, int n, int k) {
        return networkDelayTimeDijkstra(times, n, k);
    }

    public static void main(String[] args) {
        NetworkDelayTime solution = new NetworkDelayTime();

        // Test case 1: All nodes reachable, answer is max distance
        int[][] times1 = {{2, 1, 1}, {2, 3, 1}, {3, 4, 1}};
        int n1 = 4, k1 = 2;
        System.out.println("Test case 1: times = " + Arrays.deepToString(times1)
                + ", n = " + n1 + ", k = " + k1);
        System.out.println("Dijkstra: " + solution.networkDelayTimeDijkstra(times1, n1, k1));       // 2
        System.out.println("Bellman-Ford: " + solution.networkDelayTimeBellmanFord(times1, n1, k1)); // 2
        System.out.println("Dijkstra (visited): " + solution.networkDelayTimeDijkstraVisited(times1, n1, k1)); // 2
        System.out.println();

        // Test case 2: Node 1 unreachable from source k=2
        int[][] times2 = {{1, 2, 1}};
        int n2 = 2, k2 = 2;
        System.out.println("Test case 2: times = " + Arrays.deepToString(times2)
                + ", n = " + n2 + ", k = " + k2);
        System.out.println("Dijkstra: " + solution.networkDelayTimeDijkstra(times2, n2, k2));       // -1
        System.out.println("Bellman-Ford: " + solution.networkDelayTimeBellmanFord(times2, n2, k2)); // -1
        System.out.println();

        // Test case 3: Parallel edges — keep minimum weight
        int[][] times3 = {{1, 2, 5}, {1, 2, 3}, {2, 3, 1}};
        int n3 = 3, k3 = 1;
        System.out.println("Test case 3: times = " + Arrays.deepToString(times3)
                + ", n = " + n3 + ", k = " + k3);
        System.out.println("Dijkstra: " + solution.networkDelayTimeDijkstra(times3, n3, k3));       // 4
        System.out.println("Bellman-Ford: " + solution.networkDelayTimeBellmanFord(times3, n3, k3)); // 4
    }
}

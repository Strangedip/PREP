import java.util.*;

/**
 * LeetCode 1584: Min Cost to Connect All Points
 *
 * You are given an array points representing integer coordinates of a set of points on a 2D plane,
 * where points[i] = [xi, yi]. The cost of connecting two points is the Manhattan distance between
 * them. Return the minimum cost to connect all points such that there is exactly one simple path
 * between any two points.
 *
 * Time Complexity: O(n^2 log n) for both Kruskal and Prim
 * Space Complexity: O(n^2) for Kruskal (edge list), O(n) for Prim (heap)
 */
public class MinCostToConnectPoints {

    /**
     * Approach 1: Kruskal's Algorithm with Union Find
     * Generate all pairwise edges, sort by cost, greedily add edges that don't form cycles.
     * Stop after adding n-1 edges (a tree on n nodes has exactly n-1 edges).
     */
    public int minCostConnectPointsKruskal(int[][] points) {
        int n = points.length;
        List<int[]> edges = new ArrayList<>();

        // Build complete graph: every pair of points is an edge
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int cost = manhattanDistance(points[i], points[j]);
                edges.add(new int[]{cost, i, j});
            }
        }
        edges.sort(Comparator.comparingInt(e -> e[0]));

        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int totalCost = 0;
        int edgesUsed = 0;
        for (int[] edge : edges) {
            if (union(parent, edge[1], edge[2])) {
                totalCost += edge[0];
                if (++edgesUsed == n - 1) {
                    break; // MST complete
                }
            }
        }
        return totalCost;
    }

    /**
     * Approach 2: Prim's Algorithm with Min-Heap
     * Grow MST from a starting node. Always pick the cheapest edge to an unvisited point.
     * No need to pre-generate all edges — compute Manhattan distance on the fly.
     */
    public int minCostConnectPointsPrim(int[][] points) {
        int n = points.length;
        boolean[] inMST = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        // Start from point 0 with cost 0
        pq.offer(new int[]{0, 0});
        int totalCost = 0;
        int nodesAdded = 0;

        while (!pq.isEmpty() && nodesAdded < n) {
            int[] current = pq.poll();
            int cost = current[0];
            int u = current[1];

            if (inMST[u]) continue; // Already in MST — skip stale heap entry

            inMST[u] = true;
            totalCost += cost;
            nodesAdded++;

            // Add all edges from u to points not yet in MST
            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    int dist = manhattanDistance(points[u], points[v]);
                    pq.offer(new int[]{dist, v});
                }
            }
        }
        return totalCost;
    }

    /**
     * Approach 3: Prim's Algorithm with Array (O(n^2), no heap)
     * For each step, scan all non-MST nodes for the minimum distance to the MST.
     * Optimal when the graph is dense and heap overhead isn't worth it.
     */
    public int minCostConnectPointsPrimArray(int[][] points) {
        int n = points.length;
        boolean[] inMST = new boolean[n];
        int[] minDist = new int[n];
        Arrays.fill(minDist, Integer.MAX_VALUE);
        minDist[0] = 0;

        int totalCost = 0;
        for (int count = 0; count < n; count++) {
            // Find the unvisited node with smallest minDist
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!inMST[i] && (u == -1 || minDist[i] < minDist[u])) {
                    u = i;
                }
            }

            inMST[u] = true;
            totalCost += minDist[u];

            // Update minDist for all non-MST neighbors
            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    int dist = manhattanDistance(points[u], points[v]);
                    minDist[v] = Math.min(minDist[v], dist);
                }
            }
        }
        return totalCost;
    }

    private int manhattanDistance(int[] a, int[] b) {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]); // Path compression
        }
        return parent[x];
    }

    private boolean union(int[] parent, int a, int b) {
        int rootA = find(parent, a);
        int rootB = find(parent, b);
        if (rootA == rootB) {
            return false; // Same component — would form a cycle
        }
        parent[rootA] = rootB;
        return true;
    }

    /** Default entry point — delegates to Kruskal. */
    public int minCostConnectPoints(int[][] points) {
        return minCostConnectPointsKruskal(points);
    }

    public static void main(String[] args) {
        MinCostToConnectPoints solution = new MinCostToConnectPoints();

        // Test case 1: Five points on a plane
        int[][] points1 = {{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}};
        System.out.println("Test case 1: points = " + Arrays.deepToString(points1));
        System.out.println("Kruskal: " + solution.minCostConnectPointsKruskal(points1));       // 20
        System.out.println("Prim (heap): " + solution.minCostConnectPointsPrim(points1));       // 20
        System.out.println("Prim (array): " + solution.minCostConnectPointsPrimArray(points1)); // 20
        System.out.println();

        // Test case 2: Three points with negative coordinates
        int[][] points2 = {{3, 12}, {-2, 5}, {-4, 1}};
        System.out.println("Test case 2: points = " + Arrays.deepToString(points2));
        System.out.println("Kruskal: " + solution.minCostConnectPointsKruskal(points2));       // 18
        System.out.println("Prim (heap): " + solution.minCostConnectPointsPrim(points2));       // 18
        System.out.println("Prim (array): " + solution.minCostConnectPointsPrimArray(points2)); // 18
        System.out.println();

        // Test case 3: Two points — single edge
        int[][] points3 = {{0, 0}, {1, 1}};
        System.out.println("Test case 3: points = " + Arrays.deepToString(points3));
        System.out.println("Kruskal: " + solution.minCostConnectPointsKruskal(points3));       // 2
        System.out.println("Prim (heap): " + solution.minCostConnectPointsPrim(points3));       // 2
    }
}

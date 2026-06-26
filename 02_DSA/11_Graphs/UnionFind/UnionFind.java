/**
 * Union Find (Disjoint Set Union) with path compression and union by rank.
 */
public class UnionFind {
    private int[] parent;
    private int[] rank;
    private int count;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        count = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX == rootY) return false;
        if (rank[rootX] < rank[rootY]) parent[rootX] = rootY;
        else if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
        else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        count--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int getCount() {
        return count;
    }

    /** Number of Islands — classic Union Find application */
    public static int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;
        UnionFind uf = new UnionFind(rows * cols);
        int water = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '0') {
                    water++;
                    continue;
                }
                int idx = r * cols + c;
                if (r > 0 && grid[r - 1][c] == '1') uf.union(idx, (r - 1) * cols + c);
                if (c > 0 && grid[r][c - 1] == '1') uf.union(idx, r * cols + c - 1);
            }
        }
        return uf.getCount() - water;
    }

    public static void main(String[] args) {
        UnionFind uf = new UnionFind(5);
        uf.union(0, 1);
        uf.union(2, 3);
        System.out.println(uf.connected(0, 1));  // true
        System.out.println(uf.connected(0, 2));  // false
        uf.union(1, 2);
        System.out.println(uf.connected(0, 3));  // true
        System.out.println("Components: " + uf.getCount()); // 2
    }
}

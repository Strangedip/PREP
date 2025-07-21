import java.util.*;

/**
 * LeetCode 200: Number of Islands
 * 
 * Given an m x n 2D binary grid which represents a map of '1's (land) and '0's (water),
 * return the number of islands.
 * 
 * An island is surrounded by water and is formed by connecting adjacent lands
 * horizontally or vertically. You may assume all four edges of the grid are surrounded by water.
 * 
 * Time Complexity: O(m * n) where m = rows, n = columns
 * Space Complexity: O(m * n) for recursion stack (DFS) or queue (BFS)
 */
public class NumberOfIslands {
    
    /**
     * Approach 1: Depth-First Search (DFS)
     * When we find a '1', we explore all connected '1's and mark them as visited
     */
    public int numIslandsDFS(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        
        int rows = grid.length;
        int cols = grid[0].length;
        int numIslands = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    numIslands++;
                    dfs(grid, i, j);
                }
            }
        }
        
        return numIslands;
    }
    
    private void dfs(char[][] grid, int row, int col) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Base cases: out of bounds or already visited/water
        if (row < 0 || row >= rows || col < 0 || col >= cols || grid[row][col] != '1') {
            return;
        }
        
        // Mark current cell as visited by changing '1' to '0'
        grid[row][col] = '0';
        
        // Explore all 4 directions
        dfs(grid, row + 1, col); // Down
        dfs(grid, row - 1, col); // Up
        dfs(grid, row, col + 1); // Right
        dfs(grid, row, col - 1); // Left
    }
    
    /**
     * Approach 2: Breadth-First Search (BFS)
     * Use a queue to explore all connected cells level by level
     */
    public int numIslandsBFS(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        
        int rows = grid.length;
        int cols = grid[0].length;
        int numIslands = 0;
        
        // Directions: up, down, left, right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    numIslands++;
                    
                    // BFS to mark all connected land
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});
                    grid[i][j] = '0'; // Mark as visited
                    
                    while (!queue.isEmpty()) {
                        int[] current = queue.poll();
                        int currentRow = current[0];
                        int currentCol = current[1];
                        
                        // Explore all 4 directions
                        for (int[] dir : directions) {
                            int newRow = currentRow + dir[0];
                            int newCol = currentCol + dir[1];
                            
                            // Check bounds and if it's unvisited land
                            if (newRow >= 0 && newRow < rows && 
                                newCol >= 0 && newCol < cols && 
                                grid[newRow][newCol] == '1') {
                                
                                grid[newRow][newCol] = '0'; // Mark as visited
                                queue.offer(new int[]{newRow, newCol});
                            }
                        }
                    }
                }
            }
        }
        
        return numIslands;
    }
    
    /**
     * Approach 3: Union-Find (Disjoint Set Union)
     * Group connected components using union-find data structure
     */
    public int numIslandsUnionFind(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        
        int rows = grid.length;
        int cols = grid[0].length;
        
        UnionFind uf = new UnionFind(grid);
        
        // Directions: down and right (to avoid double counting)
        int[][] directions = {{1, 0}, {0, 1}};
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    for (int[] dir : directions) {
                        int newRow = i + dir[0];
                        int newCol = j + dir[1];
                        
                        if (newRow < rows && newCol < cols && grid[newRow][newCol] == '1') {
                            uf.union(i * cols + j, newRow * cols + newCol);
                        }
                    }
                }
            }
        }
        
        return uf.getCount();
    }
    
    static class UnionFind {
        private int[] parent;
        private int[] rank;
        private int count;
        
        public UnionFind(char[][] grid) {
            int rows = grid.length;
            int cols = grid[0].length;
            parent = new int[rows * cols];
            rank = new int[rows * cols];
            count = 0;
            
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == '1') {
                        int id = i * cols + j;
                        parent[id] = id;
                        count++;
                    }
                }
            }
        }
        
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }
        
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX != rootY) {
                // Union by rank
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
                count--;
            }
        }
        
        public int getCount() {
            return count;
        }
    }
    
    // Helper method to create a copy of grid for testing multiple approaches
    private char[][] copyGrid(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
    
    public static void main(String[] args) {
        NumberOfIslands solution = new NumberOfIslands();
        
        // Test case 1
        char[][] grid1 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        
        System.out.println("Test case 1:");
        System.out.println("DFS approach: " + solution.numIslandsDFS(solution.copyGrid(grid1))); // 1
        System.out.println("BFS approach: " + solution.numIslandsBFS(solution.copyGrid(grid1))); // 1
        System.out.println("Union-Find approach: " + solution.numIslandsUnionFind(solution.copyGrid(grid1))); // 1
        System.out.println();
        
        // Test case 2
        char[][] grid2 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        
        System.out.println("Test case 2:");
        System.out.println("DFS approach: " + solution.numIslandsDFS(solution.copyGrid(grid2))); // 3
        System.out.println("BFS approach: " + solution.numIslandsBFS(solution.copyGrid(grid2))); // 3
        System.out.println("Union-Find approach: " + solution.numIslandsUnionFind(solution.copyGrid(grid2))); // 3
    }
} 
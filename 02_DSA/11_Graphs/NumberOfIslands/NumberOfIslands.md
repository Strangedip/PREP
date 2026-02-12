# Number of Islands

## Problem Statement
Given an `m x n` 2D binary grid which represents a map of '1's (land) and '0's (water), return the number of islands.

An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically.

**Example:**
```
Input: grid = [
  ["1","1","1","1","0"],
  ["1","1","0","1","0"],
  ["1","1","0","0","0"],
  ["0","0","0","0","0"]
]
Output: 1
```

## Approaches

### Approach 1: Depth-First Search (DFS) ⭐ (Most Common)

#### Key Insight
When we find a '1' (land), we explore all connected land using DFS and mark them as visited to avoid counting them again.

#### Algorithm
1. Iterate through each cell in the grid
2. When we find a '1':
   - Increment island count
   - Use DFS to mark all connected '1's as '0' (visited)
3. DFS explores all 4 directions (up, down, left, right)

#### Time Complexity
- **O(m × n)** - We visit each cell at most once

#### Space Complexity
- **O(m × n)** - Worst case recursion depth (when all cells are '1')

```java
public int numIslandsDFS(char[][] grid) {
    int numIslands = 0;
    
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == '1') {
                numIslands++;
                dfs(grid, i, j);
            }
        }
    }
    
    return numIslands;
}

private void dfs(char[][] grid, int row, int col) {
    // Base case: out of bounds or water/visited
    if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || 
        grid[row][col] != '1') {
        return;
    }
    
    // Mark as visited
    grid[row][col] = '0';
    
    // Explore 4 directions
    dfs(grid, row + 1, col);
    dfs(grid, row - 1, col);
    dfs(grid, row, col + 1);
    dfs(grid, row, col - 1);
}
```

### Approach 2: Breadth-First Search (BFS)

#### Key Insight
Instead of recursion, use a queue to explore connected land level by level.

#### Algorithm
1. Iterate through each cell
2. When we find a '1':
   - Increment island count
   - Use BFS with queue to mark all connected '1's as '0'

#### Time Complexity
- **O(m × n)** - Each cell visited once

#### Space Complexity
- **O(min(m, n))** - Queue size in worst case

```java
public int numIslandsBFS(char[][] grid) {
    int numIslands = 0;
    int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
    
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == '1') {
                numIslands++;
                
                Queue<int[]> queue = new LinkedList<>();
                queue.offer(new int[]{i, j});
                grid[i][j] = '0';
                
                while (!queue.isEmpty()) {
                    int[] current = queue.poll();
                    
                    for (int[] dir : directions) {
                        int newRow = current[0] + dir[0];
                        int newCol = current[1] + dir[1];
                        
                        if (newRow >= 0 && newRow < grid.length && 
                            newCol >= 0 && newCol < grid[0].length && 
                            grid[newRow][newCol] == '1') {
                            
                            grid[newRow][newCol] = '0';
                            queue.offer(new int[]{newRow, newCol});
                        }
                    }
                }
            }
        }
    }
    
    return numIslands;
}
```

### Approach 3: Union-Find (Disjoint Set Union)

#### Key Insight
Use Union-Find to group connected components. Each island becomes one connected component.

#### Algorithm
1. Initialize Union-Find with each '1' cell as separate component
2. For each '1' cell, union it with adjacent '1' cells
3. Return the number of connected components

#### Time Complexity
- **O(m × n × α(m × n))** where α is inverse Ackermann function (nearly constant)

#### Space Complexity
- **O(m × n)** - For parent and rank arrays

## Comparison

| Approach | Time | Space | When to Use |
|----------|------|-------|-------------|
| DFS | O(m×n) | O(m×n) | Most intuitive, good for interviews |
| BFS | O(m×n) | O(min(m,n)) | Better space complexity, iterative |
| Union-Find | O(m×n×α(m×n)) | O(m×n) | When you need to track connected components dynamically |

## Example Trace (DFS)

Grid:
```
[['1','1','0'],
 ['0','1','0'],
 ['0','0','1']]
```

1. **Visit (0,0)**: Found '1' → islands = 1
   - DFS marks: (0,0)→'0', (0,1)→'0', (1,1)→'0'
   
2. **Visit (0,1)**: Already '0' (visited) → skip

3. **Visit (0,2)**: '0' → skip

4. **Continue until (2,2)**: Found '1' → islands = 2
   - DFS marks: (2,2)→'0'

**Result**: 2 islands

## Key Points

### DFS vs BFS Choice
- **DFS**: More intuitive, easier to code recursively
- **BFS**: Better for finding shortest path in unweighted graphs
- **Both**: Same time complexity for this problem

### Edge Cases
- Empty grid: return 0
- All water: return 0  
- All land: return 1
- Single cell: return 1 if '1', 0 if '0'

### Optimization Notes
- We modify the input grid to mark visited cells (space optimization)
- If you can't modify input, use a separate `visited` boolean array
- For very large grids, consider iterative DFS to avoid stack overflow

## Interview Tips
1. **Start with DFS** - most interviewers expect this approach
2. **Explain the marking strategy** - why we change '1' to '0'
3. **Handle edge cases** - empty grid, single cell
4. **Discuss space optimization** - modifying input vs separate visited array
5. **Know when to use each approach** - DFS for simplicity, BFS for level-order, Union-Find for dynamic connectivity 
import java.util.*;

/**
 * LeetCode 207: Course Schedule
 * 
 * There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1.
 * You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must
 * take course bi first if you want to take course ai.
 * 
 * Return true if you can finish all courses. Otherwise, return false.
 * 
 * Time Complexity: O(V + E) where V = courses, E = prerequisites
 * Space Complexity: O(V + E) for adjacency list and recursion/queue
 */
public class CourseSchedule {
    
    /**
     * Approach 1: DFS with Cycle Detection
     * Use DFS to detect cycles in the directed graph. If cycle exists, impossible to finish all courses.
     */
    public boolean canFinishDFS(int numCourses, int[][] prerequisites) {
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            graph.get(prerequisite).add(course); // prerequisite -> course
        }
        
        // 0: unvisited, 1: visiting (in current path), 2: visited (completed)
        int[] state = new int[numCourses];
        
        for (int course = 0; course < numCourses; course++) {
            if (state[course] == 0) {
                if (hasCycleDFS(graph, course, state)) {
                    return false; // Cycle detected
                }
            }
        }
        
        return true; // No cycle, can finish all courses
    }
    
    private boolean hasCycleDFS(List<List<Integer>> graph, int course, int[] state) {
        if (state[course] == 1) {
            return true; // Back edge found - cycle detected
        }
        
        if (state[course] == 2) {
            return false; // Already processed
        }
        
        // Mark as visiting
        state[course] = 1;
        
        // Check all neighbors
        for (int nextCourse : graph.get(course)) {
            if (hasCycleDFS(graph, nextCourse, state)) {
                return true;
            }
        }
        
        // Mark as completed
        state[course] = 2;
        return false;
    }
    
    /**
     * Approach 2: Topological Sorting using Kahn's Algorithm (BFS)
     * Use in-degree counting and BFS to perform topological sort.
     */
    public boolean canFinishTopologicalSort(int numCourses, int[][] prerequisites) {
        // Build adjacency list and calculate in-degrees
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            graph.get(prerequisite).add(course);
            inDegree[course]++;
        }
        
        // Start with courses having no prerequisites (in-degree = 0)
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        int processedCourses = 0;
        
        while (!queue.isEmpty()) {
            int currentCourse = queue.poll();
            processedCourses++;
            
            // Remove current course and update in-degrees of dependent courses
            for (int nextCourse : graph.get(currentCourse)) {
                inDegree[nextCourse]--;
                if (inDegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }
        
        return processedCourses == numCourses; // All courses processed = no cycle
    }
    
    /**
     * Approach 3: Topological Sorting using DFS (with ordering)
     * Perform DFS and build topological order using post-order traversal.
     */
    public boolean canFinishDFSTopological(int numCourses, int[][] prerequisites) {
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            graph.get(prerequisite).add(course);
        }
        
        int[] state = new int[numCourses]; // 0: unvisited, 1: visiting, 2: visited
        Stack<Integer> topologicalOrder = new Stack<>();
        
        for (int course = 0; course < numCourses; course++) {
            if (state[course] == 0) {
                if (!dfsTopological(graph, course, state, topologicalOrder)) {
                    return false; // Cycle detected
                }
            }
        }
        
        return topologicalOrder.size() == numCourses;
    }
    
    private boolean dfsTopological(List<List<Integer>> graph, int course, int[] state, Stack<Integer> topologicalOrder) {
        if (state[course] == 1) {
            return false; // Cycle detected
        }
        
        if (state[course] == 2) {
            return true; // Already processed
        }
        
        state[course] = 1; // Mark as visiting
        
        for (int nextCourse : graph.get(course)) {
            if (!dfsTopological(graph, nextCourse, state, topologicalOrder)) {
                return false;
            }
        }
        
        state[course] = 2; // Mark as completed
        topologicalOrder.push(course); // Add to topological order (post-order)
        return true;
    }
    
    /**
     * Extension: Course Schedule II - Return the ordering of courses
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // Build adjacency list and calculate in-degrees
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            graph.get(prerequisite).add(course);
            inDegree[course]++;
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        int[] result = new int[numCourses];
        int index = 0;
        
        while (!queue.isEmpty()) {
            int currentCourse = queue.poll();
            result[index++] = currentCourse;
            
            for (int nextCourse : graph.get(currentCourse)) {
                inDegree[nextCourse]--;
                if (inDegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }
        
        return index == numCourses ? result : new int[0]; // Return empty if cycle exists
    }
    
    public static void main(String[] args) {
        CourseSchedule solution = new CourseSchedule();
        
        // Test case 1: Can finish courses
        int numCourses1 = 2;
        int[][] prerequisites1 = {{1, 0}}; // Take course 0 before course 1
        
        System.out.println("Test case 1: numCourses = " + numCourses1 + ", prerequisites = " + Arrays.deepToString(prerequisites1));
        System.out.println("DFS approach: " + solution.canFinishDFS(numCourses1, prerequisites1)); // true
        System.out.println("Topological Sort (BFS): " + solution.canFinishTopologicalSort(numCourses1, prerequisites1)); // true
        System.out.println("DFS Topological: " + solution.canFinishDFSTopological(numCourses1, prerequisites1)); // true
        System.out.println("Course order: " + Arrays.toString(solution.findOrder(numCourses1, prerequisites1))); // [0, 1]
        System.out.println();
        
        // Test case 2: Circular dependency (cycle)
        int numCourses2 = 2;
        int[][] prerequisites2 = {{1, 0}, {0, 1}}; // Course 0 depends on 1, and 1 depends on 0
        
        System.out.println("Test case 2: numCourses = " + numCourses2 + ", prerequisites = " + Arrays.deepToString(prerequisites2));
        System.out.println("DFS approach: " + solution.canFinishDFS(numCourses2, prerequisites2)); // false
        System.out.println("Topological Sort (BFS): " + solution.canFinishTopologicalSort(numCourses2, prerequisites2)); // false
        System.out.println("DFS Topological: " + solution.canFinishDFSTopological(numCourses2, prerequisites2)); // false
        System.out.println("Course order: " + Arrays.toString(solution.findOrder(numCourses2, prerequisites2))); // []
        System.out.println();
        
        // Test case 3: More complex case
        int numCourses3 = 4;
        int[][] prerequisites3 = {{1, 0}, {2, 0}, {3, 1}, {3, 2}}; // 0 -> {1,2} -> 3
        
        System.out.println("Test case 3: numCourses = " + numCourses3 + ", prerequisites = " + Arrays.deepToString(prerequisites3));
        System.out.println("DFS approach: " + solution.canFinishDFS(numCourses3, prerequisites3)); // true
        System.out.println("Topological Sort (BFS): " + solution.canFinishTopologicalSort(numCourses3, prerequisites3)); // true
        System.out.println("Course order: " + Arrays.toString(solution.findOrder(numCourses3, prerequisites3))); // [0, 1, 2, 3] or [0, 2, 1, 3]
    }
} 
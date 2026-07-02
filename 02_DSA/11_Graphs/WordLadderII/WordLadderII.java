import java.util.*;

/**
 * LeetCode 126: Word Ladder II
 *
 * A transformation sequence from word beginWord to word endWord using a dictionary wordList
 * is a sequence of words beginWord -> s1 -> s2 -> ... -> sk such that every adjacent pair
 * differs by exactly one letter, every intermediate word is in wordList, and sk == endWord.
 *
 * Return all the shortest transformation sequences from beginWord to endWord, or an empty list
 * if no such sequence exists.
 *
 * Time Complexity: O(N * L^2 * 26) for BFS + O(P * L) for backtracking
 *   where N = wordList size, L = word length, P = number of shortest paths
 * Space Complexity: O(N * L) for graph, visited sets, and queue
 */
public class WordLadderII {

    /**
     * Approach 1: BFS Layer Graph + Backtracking
     * Phase 1: BFS level-by-level to build a graph of edges on shortest paths only.
     * Phase 2: DFS backtracking from beginWord to endWord through the graph.
     */
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        List<List<String>> result = new ArrayList<>();
        if (!dict.contains(endWord)) {
            return result;
        }

        // Directed graph: parent -> list of children on shortest paths
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        visited.add(beginWord);
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            int levelSize = queue.size();
            // Per-level visited: allows multiple parents to link to same child within a level
            Set<String> levelVisited = new HashSet<>();

            for (int i = 0; i < levelSize; i++) {
                String word = queue.poll();
                char[] chars = word.toCharArray();

                for (int j = 0; j < chars.length; j++) {
                    char original = chars[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[j] = c;
                        String next = new String(chars);

                        if (!dict.contains(next)) continue;

                        // Only add edges to unvisited nodes (next level) — prevents cycles
                        if (!visited.contains(next)) {
                            graph.computeIfAbsent(word, k -> new ArrayList<>()).add(next);
                            levelVisited.add(next);
                            if (next.equals(endWord)) {
                                found = true;
                            }
                        }
                    }
                    chars[j] = original;
                }
            }

            visited.addAll(levelVisited);
            queue.addAll(levelVisited);
        }

        if (!graph.containsKey(beginWord)) {
            return result;
        }

        List<String> path = new ArrayList<>();
        path.add(beginWord);
        backtrack(graph, beginWord, endWord, path, result);
        return result;
    }

    /**
     * DFS backtracking: enumerate all paths from current word to endWord through the BFS graph.
     */
    private void backtrack(Map<String, List<String>> graph, String current, String endWord,
                           List<String> path, List<List<String>> result) {
        if (current.equals(endWord)) {
            result.add(new ArrayList<>(path));
            return;
        }
        if (!graph.containsKey(current)) {
            return;
        }
        for (String next : graph.get(current)) {
            path.add(next);
            backtrack(graph, next, endWord, path, result);
            path.remove(path.size() - 1); // Backtrack
        }
    }

    /**
     * Approach 2: Bidirectional BFS + Backtracking
     * Search from both beginWord and endWord simultaneously. Expand the smaller frontier
     * each round. When frontiers meet, merge graphs and backtrack.
     */
    public List<List<String>> findLaddersBidirectional(String beginWord, String endWord,
                                                      List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        List<List<String>> result = new ArrayList<>();
        if (!dict.contains(endWord)) {
            return result;
        }

        Map<String, List<String>> forwardGraph = new HashMap<>();
        Map<String, List<String>> backwardGraph = new HashMap<>();
        Set<String> forwardVisited = new HashSet<>();
        Set<String> backwardVisited = new HashSet<>();
        forwardVisited.add(beginWord);
        backwardVisited.add(endWord);

        Queue<String> forwardQueue = new LinkedList<>();
        Queue<String> backwardQueue = new LinkedList<>();
        forwardQueue.offer(beginWord);
        backwardQueue.offer(endWord);

        boolean found = false;
        boolean expandForward = true;

        while (!forwardQueue.isEmpty() && !backwardQueue.isEmpty() && !found) {
            // Always expand the smaller frontier for efficiency
            if (forwardQueue.size() <= backwardQueue.size()) {
                found = expandBFSLevel(forwardQueue, forwardGraph, forwardVisited,
                        backwardVisited, dict, false);
                expandForward = true;
            } else {
                found = expandBFSLevel(backwardQueue, backwardGraph, backwardVisited,
                        forwardVisited, dict, true);
                expandForward = false;
            }
        }

        if (!found) {
            return result;
        }

        // Merge bidirectional graphs into a single forward graph for backtracking
        Map<String, List<String>> mergedGraph = mergeGraphs(forwardGraph, backwardGraph,
                forwardVisited, backwardVisited);

        if (!mergedGraph.containsKey(beginWord)) {
            return result;
        }

        List<String> path = new ArrayList<>();
        path.add(beginWord);
        backtrack(mergedGraph, beginWord, endWord, path, result);
        return result;
    }

    /**
     * Expand one BFS level. Returns true if the opposite frontier is reached (meeting point).
     */
    private boolean expandBFSLevel(Queue<String> queue, Map<String, List<String>> graph,
                                    Set<String> visited, Set<String> otherVisited,
                                    Set<String> dict, boolean isBackward) {
        int levelSize = queue.size();
        Set<String> levelVisited = new HashSet<>();
        boolean met = false;

        for (int i = 0; i < levelSize; i++) {
            String word = queue.poll();
            char[] chars = word.toCharArray();

            for (int j = 0; j < chars.length; j++) {
                char original = chars[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == original) continue;
                    chars[j] = c;
                    String next = new String(chars);

                    if (!dict.contains(next)) continue;

                    if (isBackward) {
                        graph.computeIfAbsent(next, k -> new ArrayList<>()).add(word);
                    } else {
                        graph.computeIfAbsent(word, k -> new ArrayList<>()).add(next);
                    }

                    if (otherVisited.contains(next)) {
                        met = true;
                    }
                    if (!visited.contains(next)) {
                        levelVisited.add(next);
                    }
                }
                chars[j] = original;
            }
        }

        visited.addAll(levelVisited);
        queue.addAll(levelVisited);
        return met;
    }

    /**
     * Merge forward and backward graphs at meeting points into a single directed graph.
     */
    private Map<String, List<String>> mergeGraphs(Map<String, List<String>> forward,
                                                   Map<String, List<String>> backward,
                                                   Set<String> forwardVisited,
                                                   Set<String> backwardVisited) {
        Map<String, List<String>> merged = new HashMap<>(forward);

        // Add backward edges (reversed direction) for nodes in the meeting zone
        for (Map.Entry<String, List<String>> entry : backward.entrySet()) {
            for (String child : entry.getValue()) {
                merged.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
                if (!merged.get(entry.getKey()).contains(child)) {
                    merged.get(entry.getKey()).add(child);
                }
            }
        }

        // Connect meeting points: words visited by both frontiers
        for (String word : forwardVisited) {
            if (backwardVisited.contains(word) && backward.containsKey(word)) {
                merged.put(word, backward.get(word));
            }
        }

        return merged;
    }

    public static void main(String[] args) {
        WordLadderII solution = new WordLadderII();

        // Test case 1: Two shortest paths
        String beginWord1 = "hit";
        String endWord1 = "cog";
        List<String> wordList1 = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");

        System.out.println("Test case 1: beginWord = \"" + beginWord1 + "\", endWord = \""
                + endWord1 + "\", wordList = " + wordList1);
        List<List<String>> result1 = solution.findLadders(beginWord1, endWord1, wordList1);
        System.out.println("BFS + Backtrack (" + result1.size() + " paths):");
        for (List<String> path : result1) {
            System.out.println("  " + path);
        }
        // Expected: [["hit","hot","dot","dog","cog"], ["hit","hot","lot","log","cog"]]
        System.out.println();

        // Test case 2: endWord not in wordList
        String beginWord2 = "hit";
        String endWord2 = "cog";
        List<String> wordList2 = Arrays.asList("hot", "dot", "dog", "lot", "log");

        System.out.println("Test case 2: beginWord = \"" + beginWord2 + "\", endWord = \""
                + endWord2 + "\", wordList = " + wordList2);
        List<List<String>> result2 = solution.findLadders(beginWord2, endWord2, wordList2);
        System.out.println("BFS + Backtrack: " + result2); // Expected: []
        System.out.println();

        // Test case 3: Single shortest path
        String beginWord3 = "a";
        String endWord3 = "c";
        List<String> wordList3 = Arrays.asList("a", "b", "c");

        System.out.println("Test case 3: beginWord = \"" + beginWord3 + "\", endWord = \""
                + endWord3 + "\", wordList = " + wordList3);
        List<List<String>> result3 = solution.findLadders(beginWord3, endWord3, wordList3);
        System.out.println("BFS + Backtrack (" + result3.size() + " paths):");
        for (List<String> path : result3) {
            System.out.println("  " + path);
        }
        // Expected: [["a","c"]] or [["a","b","c"]] depending on dictionary
    }
}

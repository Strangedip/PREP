import java.util.*;

/**
 * Problem: Word Break
 * 
 * Given a string s and a dictionary of strings wordDict, return true if s can be 
 * segmented into a space-separated sequence of one or more dictionary words.
 * 
 * Note that the same word in the dictionary may be reused multiple times in the segmentation.
 * 
 * Example 1:
 * Input: s = "leetcode", wordDict = ["leet","code"]
 * Output: true
 * Explanation: Return true because "leetcode" can be segmented as "leet code".
 * 
 * Example 2:
 * Input: s = "applepenapple", wordDict = ["apple","pen"]
 * Output: true
 * Explanation: Return true because "applepenapple" can be segmented as "apple pen apple".
 * Note that you are allowed to reuse a dictionary word.
 * 
 * Example 3:
 * Input: s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
 * Output: false
 */
public class WordBreak {
    
    /**
     * APPROACH 1: RECURSIVE BRUTE FORCE
     * Time Complexity: O(2^n) - Exponential due to overlapping subproblems
     * Space Complexity: O(n) - Recursion stack depth
     * 
     * Try all possible ways to break the string.
     */
    public boolean wordBreakRecursive(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        return canBreak(s, 0, wordSet);
    }
    
    private boolean canBreak(String s, int start, Set<String> wordSet) {
        // Base case: reached end of string
        if (start == s.length()) {
            return true;
        }
        
        // Try all possible substrings starting from 'start'
        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (wordSet.contains(word) && canBreak(s, end, wordSet)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * APPROACH 2: MEMOIZATION (TOP-DOWN DP)
     * Time Complexity: O(n^3) - n subproblems, each taking O(n^2) for substring operations
     * Space Complexity: O(n) - Memoization array + recursion stack
     * 
     * Cache results to avoid recomputation.
     */
    public boolean wordBreakMemo(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Boolean[] memo = new Boolean[s.length()];
        return canBreakMemo(s, 0, wordSet, memo);
    }
    
    private boolean canBreakMemo(String s, int start, Set<String> wordSet, Boolean[] memo) {
        if (start == s.length()) {
            return true;
        }
        
        if (memo[start] != null) {
            return memo[start];
        }
        
        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (wordSet.contains(word) && canBreakMemo(s, end, wordSet, memo)) {
                memo[start] = true;
                return true;
            }
        }
        
        memo[start] = false;
        return false;
    }
    
    /**
     * APPROACH 3: BOTTOM-UP DP (TABULATION)
     * Time Complexity: O(n^3) - Two nested loops + substring operation
     * Space Complexity: O(n) - DP array
     * 
     * Build solution from smaller subproblems.
     */
    public boolean wordBreakDP(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();
        
        // dp[i] = true if s[0...i-1] can be segmented
        boolean[] dp = new boolean[n + 1];
        dp[0] = true; // Empty string can always be segmented
        
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                // Check if s[0...j-1] can be segmented AND s[j...i-1] is in dictionary
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break; // Found one valid segmentation
                }
            }
        }
        
        return dp[n];
    }
    
    /**
     * APPROACH 4: OPTIMIZED DP WITH WORD LENGTH FILTERING
     * Time Complexity: O(n^2 * k) where k is average word length
     * Space Complexity: O(n + m) where m is total characters in wordDict
     * 
     * Only check substrings that match word lengths in dictionary.
     */
    public boolean wordBreakOptimized(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Set<Integer> wordLengths = new HashSet<>();
        
        // Precompute all possible word lengths
        for (String word : wordDict) {
            wordLengths.add(word.length());
        }
        
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        
        for (int i = 1; i <= n; i++) {
            for (int len : wordLengths) {
                if (len <= i && dp[i - len] && wordSet.contains(s.substring(i - len, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        
        return dp[n];
    }
    
    /**
     * APPROACH 5: BFS SOLUTION
     * Time Complexity: O(n^3) - Similar to DP but with BFS traversal
     * Space Complexity: O(n) - Queue and visited set
     * 
     * Treat as graph problem where each position is a node.
     */
    public boolean wordBreakBFS(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        
        queue.offer(0);
        visited.add(0);
        
        while (!queue.isEmpty()) {
            int start = queue.poll();
            
            for (int end = start + 1; end <= s.length(); end++) {
                if (visited.contains(end)) {
                    continue; // Already processed this position
                }
                
                if (wordSet.contains(s.substring(start, end))) {
                    if (end == s.length()) {
                        return true; // Reached end
                    }
                    
                    queue.offer(end);
                    visited.add(end);
                }
            }
        }
        
        return false;
    }
    
    /**
     * VARIATION: WORD BREAK II - RETURN ALL POSSIBLE SENTENCES
     * Time Complexity: O(2^n) in worst case (exponential number of solutions)
     * Space Complexity: O(2^n) to store all solutions
     * 
     * Returns all possible ways to break the string.
     */
    public List<String> wordBreakII(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<Integer, List<String>> memo = new HashMap<>();
        return wordBreakHelper(s, 0, wordSet, memo);
    }
    
    private List<String> wordBreakHelper(String s, int start, Set<String> wordSet, 
                                       Map<Integer, List<String>> memo) {
        if (memo.containsKey(start)) {
            return memo.get(start);
        }
        
        List<String> result = new ArrayList<>();
        
        if (start == s.length()) {
            result.add(""); // Empty string for base case
            return result;
        }
        
        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (wordSet.contains(word)) {
                List<String> suffixes = wordBreakHelper(s, end, wordSet, memo);
                for (String suffix : suffixes) {
                    if (suffix.isEmpty()) {
                        result.add(word);
                    } else {
                        result.add(word + " " + suffix);
                    }
                }
            }
        }
        
        memo.put(start, result);
        return result;
    }
    
    /**
     * VARIATION: MINIMUM WORD BREAKS
     * Time Complexity: O(n^3)
     * Space Complexity: O(n)
     * 
     * Returns minimum number of words needed to break the string.
     */
    public int minWordBreaks(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();
        
        // dp[i] = minimum words to break s[0...i-1], -1 if impossible
        int[] dp = new int[n + 1];
        Arrays.fill(dp, -1);
        dp[0] = 0; // Empty string needs 0 words
        
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] != -1 && wordSet.contains(s.substring(j, i))) {
                    if (dp[i] == -1) {
                        dp[i] = dp[j] + 1;
                    } else {
                        dp[i] = Math.min(dp[i], dp[j] + 1);
                    }
                }
            }
        }
        
        return dp[n];
    }
    
    /**
     * VARIATION: WORD BREAK WITH CONCATENATED WORDS
     * Time Complexity: O(n^3)
     * Space Complexity: O(n)
     * 
     * Check if string can be broken using words that are concatenations of dictionary words.
     */
    public boolean wordBreakConcatenated(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        
        // Generate all possible concatenations of 2 words
        Set<String> concatenated = new HashSet<>(wordDict);
        for (String word1 : wordDict) {
            for (String word2 : wordDict) {
                concatenated.add(word1 + word2);
            }
        }
        
        return wordBreakDP(s, new ArrayList<>(concatenated));
    }
    
    /**
     * HELPER: Check if word break is possible using Trie for efficiency
     * Time Complexity: O(n^2) - Better substring checking with Trie
     * Space Complexity: O(m) where m is total characters in wordDict
     */
    public boolean wordBreakTrie(String s, List<String> wordDict) {
        Trie trie = new Trie();
        for (String word : wordDict) {
            trie.insert(word);
        }
        
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && trie.search(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        
        return dp[n];
    }
    
    /**
     * Trie data structure for efficient word lookup
     */
    class Trie {
        class TrieNode {
            TrieNode[] children = new TrieNode[26];
            boolean isEnd = false;
        }
        
        private TrieNode root;
        
        public Trie() {
            root = new TrieNode();
        }
        
        public void insert(String word) {
            TrieNode curr = root;
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (curr.children[index] == null) {
                    curr.children[index] = new TrieNode();
                }
                curr = curr.children[index];
            }
            curr.isEnd = true;
        }
        
        public boolean search(String word) {
            TrieNode curr = root;
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (curr.children[index] == null) {
                    return false;
                }
                curr = curr.children[index];
            }
            return curr.isEnd;
        }
    }
    
    /**
     * HELPER: Print DP table for visualization
     */
    public void printDPTable(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        
        System.out.println("Word Break DP Table for: \"" + s + "\"");
        System.out.println("Dictionary: " + wordDict);
        System.out.println();
        
        System.out.print("Index: ");
        for (int i = 0; i <= n; i++) {
            System.out.printf("%3d", i);
        }
        System.out.println();
        
        System.out.print("Char:  ");
        for (int i = 0; i <= n; i++) {
            if (i == 0) {
                System.out.print("  ε");
            } else {
                System.out.printf("%3c", s.charAt(i - 1));
            }
        }
        System.out.println();
        
        System.out.print("DP:    ");
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
            System.out.printf("%3s", dp[i] ? "T" : "F");
        }
        System.out.println();
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        WordBreak solution = new WordBreak();
        
        // Test case 1
        String s1 = "leetcode";
        List<String> wordDict1 = Arrays.asList("leet", "code");
        
        System.out.println("=== Test Case 1 ===");
        System.out.println("String: \"" + s1 + "\"");
        System.out.println("Dictionary: " + wordDict1);
        System.out.println("Recursive: " + solution.wordBreakRecursive(s1, wordDict1));
        System.out.println("Memoization: " + solution.wordBreakMemo(s1, wordDict1));
        System.out.println("DP: " + solution.wordBreakDP(s1, wordDict1));
        System.out.println("Optimized: " + solution.wordBreakOptimized(s1, wordDict1));
        System.out.println("BFS: " + solution.wordBreakBFS(s1, wordDict1));
        System.out.println("Trie: " + solution.wordBreakTrie(s1, wordDict1));
        System.out.println("Min breaks: " + solution.minWordBreaks(s1, wordDict1));
        System.out.println("All sentences: " + solution.wordBreakII(s1, wordDict1));
        System.out.println();
        solution.printDPTable(s1, wordDict1);
        
        // Test case 2
        System.out.println("\n=== Test Case 2 ===");
        String s2 = "applepenapple";
        List<String> wordDict2 = Arrays.asList("apple", "pen");
        
        System.out.println("String: \"" + s2 + "\"");
        System.out.println("Dictionary: " + wordDict2);
        System.out.println("Result: " + solution.wordBreakDP(s2, wordDict2));
        System.out.println("Min breaks: " + solution.minWordBreaks(s2, wordDict2));
        System.out.println("All sentences: " + solution.wordBreakII(s2, wordDict2));
        
        // Test case 3
        System.out.println("\n=== Test Case 3 ===");
        String s3 = "catsandog";
        List<String> wordDict3 = Arrays.asList("cats", "dog", "sand", "and", "cat");
        
        System.out.println("String: \"" + s3 + "\"");
        System.out.println("Dictionary: " + wordDict3);
        System.out.println("Result: " + solution.wordBreakDP(s3, wordDict3));
        System.out.println("Min breaks: " + solution.minWordBreaks(s3, wordDict3));
        
        // Performance test
        System.out.println("\n=== Performance Test ===");
        String longString = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        List<String> longDict = Arrays.asList("a", "aa", "aaa", "aaaa", "aaaaa");
        
        long startTime = System.currentTimeMillis();
        boolean result1 = solution.wordBreakDP(longString, longDict);
        long endTime = System.currentTimeMillis();
        System.out.println("DP: " + result1 + " (Time: " + (endTime - startTime) + " ms)");
        
        startTime = System.currentTimeMillis();
        boolean result2 = solution.wordBreakOptimized(longString, longDict);
        endTime = System.currentTimeMillis();
        System.out.println("Optimized: " + result2 + " (Time: " + (endTime - startTime) + " ms)");
    }
} 
import java.util.*;

/**
 * Problem: Word Ladder
 * 
 * A transformation sequence from word beginWord to word endWord using a dictionary wordList 
 * is a sequence of words beginWord -> s1 -> s2 -> ... -> sk such that:
 * - Every adjacent pair of words differs by exactly one letter.
 * - Every si for 1 <= i <= k is in wordList. Note that beginWord does not need to be in wordList.
 * - sk == endWord
 * 
 * Given two words, beginWord and endWord, and a dictionary wordList, return the number of words 
 * in the shortest transformation sequence from beginWord to endWord, or 0 if no such sequence exists.
 * 
 * Example:
 * Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
 * Output: 5
 * Explanation: One shortest transformation sequence is "hit" -> "hot" -> "dot" -> "dog" -> "cog", which is 5 words long.
 */
public class WordLadder {
    
    /**
     * APPROACH 1: BFS (BREADTH-FIRST SEARCH)
     * Time Complexity: O(M^2 * N) where M = word length, N = number of words in wordList
     * Space Complexity: O(M^2 * N) for the graph and queue
     * 
     * BFS guarantees finding the shortest path in unweighted graphs.
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (!wordList.contains(endWord)) {
            return 0;
        }
        
        Set<String> wordSet = new HashSet<>(wordList);
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(beginWord);
        visited.add(beginWord);
        int level = 1;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            
            // Process all words at current level
            for (int i = 0; i < size; i++) {
                String currentWord = queue.poll();
                
                if (currentWord.equals(endWord)) {
                    return level;
                }
                
                // Try all possible single character changes
                char[] chars = currentWord.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char originalChar = chars[j];
                    
                    // Try all 26 possible characters
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == originalChar) continue;
                        
                        chars[j] = c;
                        String newWord = new String(chars);
                        
                        if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                            queue.offer(newWord);
                            visited.add(newWord);
                        }
                    }
                    
                    chars[j] = originalChar; // Restore original character
                }
            }
            
            level++;
        }
        
        return 0; // No transformation sequence found
    }
    
    /**
     * APPROACH 2: BIDIRECTIONAL BFS (OPTIMIZED)
     * Time Complexity: O(M^2 * N) but faster in practice
     * Space Complexity: O(M^2 * N)
     * 
     * Search from both ends to reduce search space.
     */
    public int ladderLengthBidirectional(String beginWord, String endWord, List<String> wordList) {
        if (!wordList.contains(endWord)) {
            return 0;
        }
        
        Set<String> wordSet = new HashSet<>(wordList);
        Set<String> beginSet = new HashSet<>();
        Set<String> endSet = new HashSet<>();
        
        beginSet.add(beginWord);
        endSet.add(endWord);
        
        int level = 1;
        
        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            // Always expand the smaller set for optimization
            if (beginSet.size() > endSet.size()) {
                Set<String> temp = beginSet;
                beginSet = endSet;
                endSet = temp;
            }
            
            Set<String> nextSet = new HashSet<>();
            
            for (String word : beginSet) {
                char[] chars = word.toCharArray();
                
                for (int i = 0; i < chars.length; i++) {
                    char originalChar = chars[i];
                    
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == originalChar) continue;
                        
                        chars[i] = c;
                        String newWord = new String(chars);
                        
                        if (endSet.contains(newWord)) {
                            return level + 1;
                        }
                        
                        if (wordSet.contains(newWord)) {
                            nextSet.add(newWord);
                            wordSet.remove(newWord); // Avoid revisiting
                        }
                    }
                    
                    chars[i] = originalChar;
                }
            }
            
            beginSet = nextSet;
            level++;
        }
        
        return 0;
    }
    
    /**
     * APPROACH 3: USING GENERIC PATTERNS (PREPROCESSING)
     * Time Complexity: O(M^2 * N) for preprocessing + O(M^2 * N) for BFS
     * Space Complexity: O(M^2 * N)
     * 
     * Build a graph using generic patterns like "*ot" for "hot", "dot", etc.
     */
    public int ladderLengthWithPatterns(String beginWord, String endWord, List<String> wordList) {
        if (!wordList.contains(endWord)) {
            return 0;
        }
        
        int wordLength = beginWord.length();
        
        // Build graph using generic patterns
        Map<String, List<String>> patternMap = new HashMap<>();
        
        // Add all words to pattern map
        wordList.forEach(word -> {
            for (int i = 0; i < wordLength; i++) {
                String pattern = word.substring(0, i) + "*" + word.substring(i + 1);
                patternMap.computeIfAbsent(pattern, k -> new ArrayList<>()).add(word);
            }
        });
        
        // Add beginWord patterns
        for (int i = 0; i < wordLength; i++) {
            String pattern = beginWord.substring(0, i) + "*" + beginWord.substring(i + 1);
            patternMap.computeIfAbsent(pattern, k -> new ArrayList<>()).add(beginWord);
        }
        
        // BFS
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(beginWord);
        visited.add(beginWord);
        int level = 1;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            
            for (int i = 0; i < size; i++) {
                String currentWord = queue.poll();
                
                if (currentWord.equals(endWord)) {
                    return level;
                }
                
                // Check all patterns for current word
                for (int j = 0; j < wordLength; j++) {
                    String pattern = currentWord.substring(0, j) + "*" + currentWord.substring(j + 1);
                    
                    List<String> neighbors = patternMap.getOrDefault(pattern, new ArrayList<>());
                    for (String neighbor : neighbors) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
                        }
                    }
                }
            }
            
            level++;
        }
        
        return 0;
    }
    
    /**
     * HELPER: Check if two words differ by exactly one character
     */
    private boolean isOneCharDiff(String word1, String word2) {
        if (word1.length() != word2.length()) {
            return false;
        }
        
        int diffCount = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                diffCount++;
                if (diffCount > 1) {
                    return false;
                }
            }
        }
        
        return diffCount == 1;
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        WordLadder solution = new WordLadder();
        
        // Test case 1
        String beginWord1 = "hit";
        String endWord1 = "cog";
        List<String> wordList1 = Arrays.asList("hot","dot","dog","lot","log","cog");
        
        System.out.println("Test 1 - BFS: " + solution.ladderLength(beginWord1, endWord1, wordList1));
        System.out.println("Test 1 - Bidirectional: " + solution.ladderLengthBidirectional(beginWord1, endWord1, wordList1));
        System.out.println("Test 1 - Patterns: " + solution.ladderLengthWithPatterns(beginWord1, endWord1, wordList1));
        
        // Test case 2
        String beginWord2 = "hit";
        String endWord2 = "cog";
        List<String> wordList2 = Arrays.asList("hot","dot","dog","lot","log");
        
        System.out.println("Test 2 - BFS: " + solution.ladderLength(beginWord2, endWord2, wordList2));
        System.out.println("Test 2 - Bidirectional: " + solution.ladderLengthBidirectional(beginWord2, endWord2, wordList2));
        System.out.println("Test 2 - Patterns: " + solution.ladderLengthWithPatterns(beginWord2, endWord2, wordList2));
    }
} 
import java.util.*;

/**
 * Problem: Group Anagrams
 * 
 * Given an array of strings strs, group the anagrams together. 
 * You can return the answer in any order.
 * 
 * An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase,
 * typically using all the original letters exactly once.
 * 
 * Example:
 * Input: strs = ["eat","tea","tan","ate","nat","bat"]
 * Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
 * 
 * Example 2:
 * Input: strs = [""]
 * Output: [[""]]
 * 
 * Example 3:
 * Input: strs = ["a"]
 * Output: [["a"]]
 */
public class GroupAnagrams {
    
    /**
     * APPROACH 1: SORTING CHARACTERS (Most Common)
     * Time Complexity: O(n * m log m) where n = number of strings, m = average string length
     * Space Complexity: O(n * m)
     * 
     * Sort characters in each string to create a key for grouping anagrams.
     */
    public List<List<String>> groupAnagramsSorting(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        Map<String, List<String>> anagramGroups = new HashMap<>();
        
        for (String str : strs) {
            // Sort characters to create a unique key for anagrams
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            // Group strings with the same sorted key
            anagramGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramGroups.values());
    }
    
    /**
     * APPROACH 2: CHARACTER FREQUENCY COUNT
     * Time Complexity: O(n * m) where n = number of strings, m = average string length
     * Space Complexity: O(n * m)
     * 
     * Use character frequency count as the key for grouping anagrams.
     * This avoids the sorting overhead.
     */
    public List<List<String>> groupAnagramsFrequency(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        Map<String, List<String>> anagramGroups = new HashMap<>();
        
        for (String str : strs) {
            String key = getFrequencyKey(str);
            anagramGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramGroups.values());
    }
    
    /**
     * Helper method to create frequency-based key
     */
    private String getFrequencyKey(String str) {
        int[] freq = new int[26]; // For lowercase English letters
        
        for (char c : str.toCharArray()) {
            freq[c - 'a']++;
        }
        
        // Create key from frequency array
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (freq[i] > 0) {
                key.append((char)('a' + i)).append(freq[i]);
            }
        }
        
        return key.toString();
    }
    
    /**
     * APPROACH 3: PRIME NUMBER HASH
     * Time Complexity: O(n * m) where n = number of strings, m = average string length
     * Space Complexity: O(n * m)
     * 
     * Use prime numbers to create unique hash for each character.
     * Anagrams will have the same product of primes.
     * Note: This can overflow for long strings, so use carefully.
     */
    public List<List<String>> groupAnagramsPrime(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        // Prime numbers for each letter a-z
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
        
        Map<Long, List<String>> anagramGroups = new HashMap<>();
        
        for (String str : strs) {
            long hash = 1;
            for (char c : str.toCharArray()) {
                hash *= primes[c - 'a'];
            }
            anagramGroups.computeIfAbsent(hash, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramGroups.values());
    }
    
    /**
     * APPROACH 4: CHARACTER COUNT ARRAY AS KEY
     * Time Complexity: O(n * m) where n = number of strings, m = average string length
     * Space Complexity: O(n * m)
     * 
     * Use the character count array itself as the key by converting to string.
     */
    public List<List<String>> groupAnagramsArray(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        Map<String, List<String>> anagramGroups = new HashMap<>();
        
        for (String str : strs) {
            int[] count = new int[26];
            for (char c : str.toCharArray()) {
                count[c - 'a']++;
            }
            
            // Convert count array to string key
            String key = Arrays.toString(count);
            anagramGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramGroups.values());
    }
    
    /**
     * APPROACH 5: CUSTOM COMPARATOR (Alternative for small datasets)
     * Time Complexity: O(n² * m) in worst case
     * Space Complexity: O(n * m)
     * 
     * Group anagrams by comparing each string with others.
     * Less efficient but demonstrates the anagram checking logic.
     */
    public List<List<String>> groupAnagramsBruteForce(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        List<List<String>> result = new ArrayList<>();
        boolean[] used = new boolean[strs.length];
        
        for (int i = 0; i < strs.length; i++) {
            if (used[i]) continue;
            
            List<String> group = new ArrayList<>();
            group.add(strs[i]);
            used[i] = true;
            
            for (int j = i + 1; j < strs.length; j++) {
                if (!used[j] && areAnagrams(strs[i], strs[j])) {
                    group.add(strs[j]);
                    used[j] = true;
                }
            }
            
            result.add(group);
        }
        
        return result;
    }
    
    /**
     * Helper method to check if two strings are anagrams
     */
    private boolean areAnagrams(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        
        int[] count = new int[26];
        
        for (int i = 0; i < s1.length(); i++) {
            count[s1.charAt(i) - 'a']++;
            count[s2.charAt(i) - 'a']--;
        }
        
        for (int freq : count) {
            if (freq != 0) return false;
        }
        
        return true;
    }
    
    /**
     * APPROACH 6: OPTIMIZED FOR UNICODE (Advanced)
     * Time Complexity: O(n * m) where n = number of strings, m = average string length
     * Space Complexity: O(n * m)
     * 
     * Handles Unicode characters, not just lowercase English letters.
     */
    public List<List<String>> groupAnagramsUnicode(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        Map<Map<Character, Integer>, List<String>> anagramGroups = new HashMap<>();
        
        for (String str : strs) {
            Map<Character, Integer> charCount = new HashMap<>();
            for (char c : str.toCharArray()) {
                charCount.put(c, charCount.getOrDefault(c, 0) + 1);
            }
            
            anagramGroups.computeIfAbsent(charCount, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramGroups.values());
    }
    
    // Helper method to print grouped anagrams
    private void printResult(List<List<String>> result) {
        System.out.print("[");
        for (int i = 0; i < result.size(); i++) {
            System.out.print("[");
            for (int j = 0; j < result.get(i).size(); j++) {
                System.out.print("\"" + result.get(i).get(j) + "\"");
                if (j < result.get(i).size() - 1) System.out.print(",");
            }
            System.out.print("]");
            if (i < result.size() - 1) System.out.print(",");
        }
        System.out.println("]");
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        GroupAnagrams solution = new GroupAnagrams();
        
        // Test case 1: Standard example
        String[] strs1 = {"eat","tea","tan","ate","nat","bat"};
        System.out.println("Test Case 1: [\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]");
        System.out.print("Sorting: ");
        solution.printResult(solution.groupAnagramsSorting(strs1.clone()));
        System.out.print("Frequency: ");
        solution.printResult(solution.groupAnagramsFrequency(strs1.clone()));
        System.out.print("Prime: ");
        solution.printResult(solution.groupAnagramsPrime(strs1.clone()));
        System.out.print("Array: ");
        solution.printResult(solution.groupAnagramsArray(strs1.clone()));
        System.out.print("Brute Force: ");
        solution.printResult(solution.groupAnagramsBruteForce(strs1.clone()));
        System.out.println();
        
        // Test case 2: Empty string
        String[] strs2 = {""};
        System.out.println("Test Case 2: [\"\"]");
        System.out.print("Result: ");
        solution.printResult(solution.groupAnagramsSorting(strs2));
        System.out.println();
        
        // Test case 3: Single character
        String[] strs3 = {"a"};
        System.out.println("Test Case 3: [\"a\"]");
        System.out.print("Result: ");
        solution.printResult(solution.groupAnagramsSorting(strs3));
        System.out.println();
        
        // Test case 4: No anagrams
        String[] strs4 = {"abc", "def", "ghi"};
        System.out.println("Test Case 4: [\"abc\", \"def\", \"ghi\"] (no anagrams)");
        System.out.print("Result: ");
        solution.printResult(solution.groupAnagramsSorting(strs4));
        System.out.println();
        
        // Test case 5: All anagrams
        String[] strs5 = {"abc", "bca", "cab", "acb"};
        System.out.println("Test Case 5: [\"abc\", \"bca\", \"cab\", \"acb\"] (all anagrams)");
        System.out.print("Result: ");
        solution.printResult(solution.groupAnagramsSorting(strs5));
        System.out.println();
        
        // Test case 6: Different lengths
        String[] strs6 = {"a", "aa", "aaa"};
        System.out.println("Test Case 6: [\"a\", \"aa\", \"aaa\"] (different lengths)");
        System.out.print("Result: ");
        solution.printResult(solution.groupAnagramsSorting(strs6));
    }
} 
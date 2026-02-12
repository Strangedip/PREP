import java.util.*;

/**
 * Partition Labels Problem
 * 
 * You are given a string s. We want to partition the string into as many parts as possible 
 * so that each letter appears in at most one part.
 * 
 * Note that the partition is done so that after concatenating all the parts in order, 
 * the resultant string should be the original string.
 * 
 * Return a list of integers representing the size of these parts.
 * 
 * Example:
 * Input: s = "ababcbacadefegdehijhklij"
 * Output: [9,7,8]
 * Explanation:
 * The partition is "ababcbaca", "defegde", "hijhklij".
 * This is a partition so that each letter appears in at most one part.
 * A partition like "ababcbacadefegde", "hijhklij" is incorrect, because it splits s into less parts.
 */
public class PartitionLabels {
    
    /**
     * APPROACH 1: GREEDY WITH LAST OCCURRENCE
     * Time Complexity: O(n)
     * Space Complexity: O(1) - at most 26 characters
     * 
     * Key insight: For each character, find its last occurrence. The current partition
     * must extend at least to the last occurrence of any character seen so far.
     */
    public List<Integer> partitionLabels(String s) {
        if (s == null || s.length() == 0) {
            return new ArrayList<>();
        }
        
        // Record the last occurrence of each character
        int[] lastIndex = new int[26];
        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;
        }
        
        List<Integer> result = new ArrayList<>();
        int start = 0;
        int end = 0;
        
        for (int i = 0; i < s.length(); i++) {
            // Extend the current partition to include the last occurrence of current character
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
            
            // If we've reached the end of current partition
            if (i == end) {
                result.add(end - start + 1);
                start = end + 1;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: GREEDY WITH SET TRACKING
     * Time Complexity: O(n)
     * Space Complexity: O(1) - at most 26 characters in set
     * 
     * Alternative approach: Track characters in current partition and their remaining occurrences.
     */
    public List<Integer> partitionLabelsWithSet(String s) {
        if (s == null || s.length() == 0) {
            return new ArrayList<>();
        }
        
        // Count frequency of each character
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        
        List<Integer> result = new ArrayList<>();
        Set<Character> currentPartition = new HashSet<>();
        int start = 0;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            currentPartition.add(c);
            count[c - 'a']--;
            
            // If count becomes 0, this character won't appear again
            if (count[c - 'a'] == 0) {
                currentPartition.remove(c);
            }
            
            // If set is empty, all characters in current partition are complete
            if (currentPartition.isEmpty()) {
                result.add(i - start + 1);
                start = i + 1;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: INTERVAL MERGING APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Think of each character as an interval [first_occurrence, last_occurrence].
     * Merge overlapping intervals to find partitions.
     */
    public List<Integer> partitionLabelsIntervals(String s) {
        if (s == null || s.length() == 0) {
            return new ArrayList<>();
        }
        
        // Find first and last occurrence of each character
        int[] firstIndex = new int[26];
        int[] lastIndex = new int[26];
        Arrays.fill(firstIndex, -1);
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (firstIndex[c - 'a'] == -1) {
                firstIndex[c - 'a'] = i;
            }
            lastIndex[c - 'a'] = i;
        }
        
        List<Integer> result = new ArrayList<>();
        int start = 0;
        int end = 0;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            // If this is the first occurrence of a character, extend partition
            if (firstIndex[c - 'a'] == i) {
                end = Math.max(end, lastIndex[c - 'a']);
            }
            
            // If we've reached the end of current partition
            if (i == end) {
                result.add(end - start + 1);
                start = end + 1;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: TWO-PASS SOLUTION
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * First pass: record last occurrence. Second pass: determine partitions.
     */
    public List<Integer> partitionLabelsTwoPass(String s) {
        if (s == null || s.length() == 0) {
            return new ArrayList<>();
        }
        
        Map<Character, Integer> lastOccurrence = new HashMap<>();
        
        // First pass: record last occurrence of each character
        for (int i = 0; i < s.length(); i++) {
            lastOccurrence.put(s.charAt(i), i);
        }
        
        // Second pass: determine partitions
        List<Integer> result = new ArrayList<>();
        int start = 0;
        int end = 0;
        
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, lastOccurrence.get(s.charAt(i)));
            
            if (i == end) {
                result.add(end - start + 1);
                start = end + 1;
            }
        }
        
        return result;
    }
    
    /**
     * EXTENSION: MINIMUM PARTITIONS WITH K DISTINCT CHARACTERS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Partition string such that each partition has at most k distinct characters.
     */
    public List<Integer> partitionWithKDistinct(String s, int k) {
        if (s == null || s.length() == 0 || k <= 0) {
            return new ArrayList<>();
        }
        
        List<Integer> result = new ArrayList<>();
        int[] count = new int[26];
        int distinctCount = 0;
        int start = 0;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (count[c - 'a'] == 0) {
                distinctCount++;
            }
            count[c - 'a']++;
            
            // If we exceed k distinct characters, end current partition
            if (distinctCount > k) {
                result.add(i - start);
                start = i;
                
                // Reset counts for new partition
                Arrays.fill(count, 0);
                count[c - 'a'] = 1;
                distinctCount = 1;
            }
        }
        
        // Add the last partition
        if (start < s.length()) {
            result.add(s.length() - start);
        }
        
        return result;
    }
    
    /**
     * EXTENSION: MAXIMUM PARTITIONS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Find maximum number of partitions such that each character appears in exactly one partition.
     */
    public int maxPartitions(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int[] lastIndex = new int[26];
        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;
        }
        
        int partitions = 0;
        int end = 0;
        
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
            
            if (i == end) {
                partitions++;
            }
        }
        
        return partitions;
    }
    
    /**
     * EXTENSION: LEXICOGRAPHICALLY SMALLEST PARTITIONS
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Among all valid partitions, return the lexicographically smallest one.
     */
    public List<String> lexicographicallySmallestPartition(String s) {
        if (s == null || s.length() == 0) {
            return new ArrayList<>();
        }
        
        List<Integer> partitionSizes = partitionLabels(s);
        List<String> result = new ArrayList<>();
        
        int index = 0;
        for (int size : partitionSizes) {
            String partition = s.substring(index, index + size);
            char[] chars = partition.toCharArray();
            Arrays.sort(chars); // Sort to get lexicographically smallest
            result.add(new String(chars));
            index += size;
        }
        
        return result;
    }
    
    /**
     * EXTENSION: COUNT VALID PARTITIONS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Count how many ways we can partition the string following the rules.
     * (In this case, there's only one valid way, but this shows the pattern)
     */
    public int countValidPartitions(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        // For this specific problem, there's exactly one valid partition
        // But we can count the number of partition points
        int[] lastIndex = new int[26];
        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;
        }
        
        int validPartitions = 1; // At least one way (the entire string)
        int end = 0;
        
        for (int i = 0; i < s.length() - 1; i++) { // Don't count the last position
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
            
            if (i == end) {
                validPartitions++; // Can make a cut here
            }
        }
        
        return validPartitions;
    }
    
    /**
     * Utility method to visualize partitions
     */
    private void visualizePartitions(String s, List<Integer> partitions) {
        System.out.println("String: " + s);
        System.out.print("Partitions: ");
        
        int index = 0;
        for (int i = 0; i < partitions.size(); i++) {
            int size = partitions.get(i);
            System.out.print("\"" + s.substring(index, index + size) + "\"");
            if (i < partitions.size() - 1) {
                System.out.print(", ");
            }
            index += size;
        }
        System.out.println();
        System.out.println("Sizes: " + partitions);
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        PartitionLabels solution = new PartitionLabels();
        
        // Test Case 1: Standard example
        System.out.println("=== Test Case 1: Standard Example ===");
        String s1 = "ababcbacadefegdehijhklij";
        List<Integer> result1 = solution.partitionLabels(s1);
        solution.visualizePartitions(s1, result1);
        
        System.out.println("Greedy: " + result1);
        System.out.println("With Set: " + solution.partitionLabelsWithSet(s1));
        System.out.println("Intervals: " + solution.partitionLabelsIntervals(s1));
        System.out.println("Two Pass: " + solution.partitionLabelsTwoPass(s1));
        System.out.println("Max Partitions: " + solution.maxPartitions(s1));
        System.out.println();
        
        // Test Case 2: Single character repeated
        System.out.println("=== Test Case 2: Single Character ===");
        String s2 = "aaaa";
        List<Integer> result2 = solution.partitionLabels(s2);
        solution.visualizePartitions(s2, result2);
        
        // Test Case 3: All different characters
        System.out.println("=== Test Case 3: All Different ===");
        String s3 = "abcdef";
        List<Integer> result3 = solution.partitionLabels(s3);
        solution.visualizePartitions(s3, result3);
        
        // Test Case 4: Complex pattern
        System.out.println("=== Test Case 4: Complex Pattern ===");
        String s4 = "abcabc";
        List<Integer> result4 = solution.partitionLabels(s4);
        solution.visualizePartitions(s4, result4);
        
        // Test Case 5: Extensions
        System.out.println("=== Test Case 5: Extensions ===");
        String s5 = "abcdefghijklmn";
        System.out.println("String: " + s5);
        System.out.println("K=3 distinct partitions: " + solution.partitionWithKDistinct(s5, 3));
        System.out.println("Count valid partitions: " + solution.countValidPartitions(s5));
        System.out.println("Lexicographically smallest: " + 
                          solution.lexicographicallySmallestPartition(s5));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(PartitionLabels solution) {
        System.out.println("=== Performance Test ===");
        
        int[] sizes = {1000, 10000, 100000};
        
        for (int size : sizes) {
            // Generate test string with repeated patterns
            StringBuilder sb = new StringBuilder();
            Random rand = new Random(42);
            
            for (int i = 0; i < size; i++) {
                char c = (char) ('a' + rand.nextInt(10)); // Use 10 different characters
                sb.append(c);
            }
            String testString = sb.toString();
            
            System.out.println("String length: " + size);
            
            long startTime, endTime;
            
            // Standard greedy approach
            startTime = System.nanoTime();
            List<Integer> result1 = solution.partitionLabels(testString);
            endTime = System.nanoTime();
            System.out.println("Greedy: " + result1.size() + " partitions (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Set tracking approach
            startTime = System.nanoTime();
            List<Integer> result2 = solution.partitionLabelsWithSet(testString);
            endTime = System.nanoTime();
            System.out.println("Set tracking: " + result2.size() + " partitions (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Intervals approach
            startTime = System.nanoTime();
            List<Integer> result3 = solution.partitionLabelsIntervals(testString);
            endTime = System.nanoTime();
            System.out.println("Intervals: " + result3.size() + " partitions (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Two pass approach
            startTime = System.nanoTime();
            List<Integer> result4 = solution.partitionLabelsTwoPass(testString);
            endTime = System.nanoTime();
            System.out.println("Two pass: " + result4.size() + " partitions (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Max partitions count
            startTime = System.nanoTime();
            int maxPartitions = solution.maxPartitions(testString);
            endTime = System.nanoTime();
            System.out.println("Max partitions: " + maxPartitions + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            System.out.println("All results match: " + 
                             (result1.equals(result2) && result2.equals(result3) && 
                              result3.equals(result4) && result1.size() == maxPartitions));
            System.out.println();
        }
    }
} 
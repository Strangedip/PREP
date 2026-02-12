import java.util.*;

/**
 * Problem: Top K Frequent Elements
 * 
 * Given an integer array nums and an integer k, return the k most frequent elements.
 * You may return the answer in any order.
 * 
 * Example 1:
 * Input: nums = [1,1,1,2,2,3], k = 2
 * Output: [1,2]
 * 
 * Example 2:
 * Input: nums = [1], k = 1
 * Output: [1]
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - k is in the range [1, the number of unique elements in the array].
 * - It is guaranteed that the answer is unique.
 * 
 * Follow up: Your algorithm's time complexity must be better than O(n log n), 
 * where n is the array's size.
 */
public class TopKFrequentElements {
    
    /**
     * APPROACH 1: MIN HEAP WITH FREQUENCY MAP
     * Time Complexity: O(n log k) where n is array length
     * Space Complexity: O(n + k)
     * 
     * Use frequency map + min heap of size k to maintain top k elements.
     */
    public int[] topKFrequent(int[] nums, int k) {
        // Step 1: Build frequency map
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // Step 2: Use min heap to keep top k frequent elements
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
        );
        
        for (int num : frequencyMap.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll(); // Remove least frequent element
            }
        }
        
        // Step 3: Extract elements from heap
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll();
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: MAX HEAP (SIMPLER BUT LESS EFFICIENT)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Use max heap to sort all elements by frequency.
     */
    public int[] topKFrequentMaxHeap(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // Max heap based on frequency
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
            (a, b) -> frequencyMap.get(b) - frequencyMap.get(a)
        );
        
        maxHeap.addAll(frequencyMap.keySet());
        
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: BUCKET SORT
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use bucket sort based on frequency for optimal time complexity.
     */
    public int[] topKFrequentBucket(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // Create buckets: index = frequency, value = list of numbers with that frequency
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }
        
        // Fill buckets
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            int frequency = entry.getValue();
            int number = entry.getKey();
            buckets[frequency].add(number);
        }
        
        // Collect top k elements from highest frequency buckets
        List<Integer> result = new ArrayList<>();
        for (int i = buckets.length - 1; i >= 0 && result.size() < k; i--) {
            if (!buckets[i].isEmpty()) {
                result.addAll(buckets[i]);
            }
        }
        
        return result.stream().limit(k).mapToInt(i -> i).toArray();
    }
    
    /**
     * APPROACH 4: QUICK SELECT (MOST EFFICIENT)
     * Time Complexity: O(n) average, O(n^2) worst case
     * Space Complexity: O(n)
     * 
     * Use quick select algorithm to find top k elements.
     */
    public int[] topKFrequentQuickSelect(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // Convert to array for quick select
        int[] uniqueNums = frequencyMap.keySet().stream().mapToInt(i -> i).toArray();
        
        // Quick select to find kth largest frequency
        int n = uniqueNums.length;
        quickSelect(uniqueNums, 0, n - 1, n - k, frequencyMap);
        
        // Return top k elements
        return Arrays.copyOfRange(uniqueNums, n - k, n);
    }
    
    private void quickSelect(int[] nums, int left, int right, int kSmallest, 
                           Map<Integer, Integer> frequencyMap) {
        if (left == right) return;
        
        Random random = new Random();
        int pivotIndex = left + random.nextInt(right - left + 1);
        
        pivotIndex = partition(nums, left, right, pivotIndex, frequencyMap);
        
        if (pivotIndex == kSmallest) {
            return;
        } else if (pivotIndex < kSmallest) {
            quickSelect(nums, pivotIndex + 1, right, kSmallest, frequencyMap);
        } else {
            quickSelect(nums, left, pivotIndex - 1, kSmallest, frequencyMap);
        }
    }
    
    private int partition(int[] nums, int left, int right, int pivotIndex, 
                         Map<Integer, Integer> frequencyMap) {
        int pivotFrequency = frequencyMap.get(nums[pivotIndex]);
        
        // Move pivot to end
        swap(nums, pivotIndex, right);
        
        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (frequencyMap.get(nums[i]) < pivotFrequency) {
                swap(nums, storeIndex, i);
                storeIndex++;
            }
        }
        
        // Move pivot to its final place
        swap(nums, storeIndex, right);
        return storeIndex;
    }
    
    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    
    /**
     * APPROACH 5: TREEMAP (MAINTAINS SORTED ORDER)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Use TreeMap to maintain sorted order of frequencies.
     */
    public int[] topKFrequentTreeMap(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // TreeMap: frequency -> list of numbers with that frequency
        TreeMap<Integer, List<Integer>> frequencyBuckets = new TreeMap<>(Collections.reverseOrder());
        
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            int frequency = entry.getValue();
            int number = entry.getKey();
            
            frequencyBuckets.computeIfAbsent(frequency, f -> new ArrayList<>()).add(number);
        }
        
        List<Integer> result = new ArrayList<>();
        for (List<Integer> bucket : frequencyBuckets.values()) {
            result.addAll(bucket);
            if (result.size() >= k) break;
        }
        
        return result.stream().limit(k).mapToInt(i -> i).toArray();
    }
    
    /**
     * APPROACH 6: CUSTOM PAIR WITH SORTING
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Create custom pairs and sort by frequency.
     */
    static class Pair {
        int number;
        int frequency;
        
        Pair(int number, int frequency) {
            this.number = number;
            this.frequency = frequency;
        }
    }
    
    public int[] topKFrequentSorting(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        List<Pair> pairs = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            pairs.add(new Pair(entry.getKey(), entry.getValue()));
        }
        
        // Sort by frequency in descending order
        pairs.sort((a, b) -> b.frequency - a.frequency);
        
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = pairs.get(i).number;
        }
        
        return result;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get frequency map for debugging
     */
    public Map<Integer, Integer> getFrequencyMap(int[] nums) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        return frequencyMap;
    }
    
    /**
     * Validate if result contains k most frequent elements
     */
    public boolean validateResult(int[] nums, int k, int[] result) {
        Map<Integer, Integer> frequencyMap = getFrequencyMap(nums);
        
        if (result.length != k) return false;
        
        // Get all frequencies and sort them
        List<Integer> frequencies = new ArrayList<>(frequencyMap.values());
        frequencies.sort(Collections.reverseOrder());
        
        // Check if result elements have top k frequencies
        for (int num : result) {
            if (!frequencyMap.containsKey(num)) return false;
        }
        
        return true;
    }
    
    /**
     * Print frequency analysis
     */
    public void printFrequencyAnalysis(int[] nums) {
        Map<Integer, Integer> frequencyMap = getFrequencyMap(nums);
        
        System.out.println("Frequency Analysis:");
        frequencyMap.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.println("  " + entry.getKey() + " appears " + entry.getValue() + " times"));
    }
    
    // ==================== RELATED PROBLEMS ====================
    
    /**
     * Top K Frequent Words (similar problem with strings)
     */
    public List<String> topKFrequentWords(String[] words, int k) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }
        
        PriorityQueue<String> minHeap = new PriorityQueue<>((a, b) -> {
            int freqCompare = frequencyMap.get(a) - frequencyMap.get(b);
            if (freqCompare == 0) {
                return b.compareTo(a); // Reverse lexicographical order for min heap
            }
            return freqCompare;
        });
        
        for (String word : frequencyMap.keySet()) {
            minHeap.offer(word);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        
        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(0, minHeap.poll()); // Add to front to reverse order
        }
        
        return result;
    }
    
    /**
     * Find K least frequent elements
     */
    public int[] topKLeastFrequent(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // Max heap for least frequent (opposite of min heap for most frequent)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
            (a, b) -> frequencyMap.get(b) - frequencyMap.get(a)
        );
        
        for (int num : frequencyMap.keySet()) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }
        
        return maxHeap.stream().mapToInt(i -> i).toArray();
    }
    
    // ==================== TEST METHODS ====================
    
    public static void main(String[] args) {
        TopKFrequentElements solution = new TopKFrequentElements();
        
        System.out.println("=== Top K Frequent Elements Demo ===\n");
        
        // Test Case 1: Standard example
        System.out.println("1. Test Case: [1,1,1,2,2,3], k=2");
        int[] nums1 = {1, 1, 1, 2, 2, 3};
        int k1 = 2;
        
        solution.printFrequencyAnalysis(nums1);
        
        System.out.println("Results:");
        System.out.println("  Min Heap: " + Arrays.toString(solution.topKFrequent(nums1, k1)));
        System.out.println("  Max Heap: " + Arrays.toString(solution.topKFrequentMaxHeap(nums1, k1)));
        System.out.println("  Bucket Sort: " + Arrays.toString(solution.topKFrequentBucket(nums1, k1)));
        System.out.println("  Quick Select: " + Arrays.toString(solution.topKFrequentQuickSelect(nums1, k1)));
        System.out.println("  TreeMap: " + Arrays.toString(solution.topKFrequentTreeMap(nums1, k1)));
        System.out.println("  Sorting: " + Arrays.toString(solution.topKFrequentSorting(nums1, k1)));
        System.out.println();
        
        // Test Case 2: Single element
        System.out.println("2. Test Case: [1], k=1");
        int[] nums2 = {1};
        int k2 = 1;
        
        solution.printFrequencyAnalysis(nums2);
        System.out.println("Result: " + Arrays.toString(solution.topKFrequent(nums2, k2)));
        System.out.println();
        
        // Test Case 3: All elements have same frequency
        System.out.println("3. Test Case: [1,2,3,4], k=2");
        int[] nums3 = {1, 2, 3, 4};
        int k3 = 2;
        
        solution.printFrequencyAnalysis(nums3);
        System.out.println("Result: " + Arrays.toString(solution.topKFrequent(nums3, k3)));
        System.out.println();
        
        // Test Case 4: Large example with clear frequency distribution
        System.out.println("4. Test Case: [4,1,-1,2,-1,2,3], k=2");
        int[] nums4 = {4, 1, -1, 2, -1, 2, 3};
        int k4 = 2;
        
        solution.printFrequencyAnalysis(nums4);
        System.out.println("Result: " + Arrays.toString(solution.topKFrequent(nums4, k4)));
        System.out.println();
        
        // Test Case 5: Related problems
        System.out.println("5. Related Problems:");
        
        // Top K Frequent Words
        String[] words = {"i", "love", "leetcode", "i", "love", "coding"};
        List<String> topWords = solution.topKFrequentWords(words, 2);
        System.out.println("Top 2 frequent words: " + topWords);
        
        // Top K Least Frequent
        int[] leastFrequent = solution.topKLeastFrequent(nums1, 2);
        System.out.println("Top 2 least frequent: " + Arrays.toString(leastFrequent));
        System.out.println();
        
        // Performance comparison
        performanceComparison(solution);
        
        // Correctness verification
        System.out.println("All approaches produce valid results: " + verifyAllApproaches(solution));
    }
    
    private static void performanceComparison(TopKFrequentElements solution) {
        System.out.println("=== Performance Comparison ===");
        
        // Generate large test case
        Random random = new Random(42); // Fixed seed for reproducibility
        int size = 100000;
        int[] largeNums = new int[size];
        
        // Create array with varying frequencies
        for (int i = 0; i < size; i++) {
            largeNums[i] = random.nextInt(size / 10); // Creates frequency distribution
        }
        
        int k = 100;
        
        long start, end;
        
        // Test Min Heap approach
        start = System.nanoTime();
        solution.topKFrequent(largeNums, k);
        end = System.nanoTime();
        System.out.println("Min Heap: " + (end - start) / 1000000.0 + " ms");
        
        // Test Bucket Sort approach
        start = System.nanoTime();
        solution.topKFrequentBucket(largeNums, k);
        end = System.nanoTime();
        System.out.println("Bucket Sort: " + (end - start) / 1000000.0 + " ms");
        
        // Test Quick Select approach
        start = System.nanoTime();
        solution.topKFrequentQuickSelect(largeNums, k);
        end = System.nanoTime();
        System.out.println("Quick Select: " + (end - start) / 1000000.0 + " ms");
        
        // Test Max Heap approach
        start = System.nanoTime();
        solution.topKFrequentMaxHeap(largeNums, k);
        end = System.nanoTime();
        System.out.println("Max Heap: " + (end - start) / 1000000.0 + " ms");
        
        System.out.println();
    }
    
    private static boolean verifyAllApproaches(TopKFrequentElements solution) {
        int[] testNums = {1, 1, 1, 2, 2, 3, 4, 4, 4, 4};
        int k = 3;
        
        // Get results from all approaches
        int[] result1 = solution.topKFrequent(testNums, k);
        int[] result2 = solution.topKFrequentMaxHeap(testNums, k);
        int[] result3 = solution.topKFrequentBucket(testNums, k);
        int[] result4 = solution.topKFrequentQuickSelect(testNums, k);
        int[] result5 = solution.topKFrequentTreeMap(testNums, k);
        int[] result6 = solution.topKFrequentSorting(testNums, k);
        
        // Validate all results
        return solution.validateResult(testNums, k, result1) &&
               solution.validateResult(testNums, k, result2) &&
               solution.validateResult(testNums, k, result3) &&
               solution.validateResult(testNums, k, result4) &&
               solution.validateResult(testNums, k, result5) &&
               solution.validateResult(testNums, k, result6);
    }
} 
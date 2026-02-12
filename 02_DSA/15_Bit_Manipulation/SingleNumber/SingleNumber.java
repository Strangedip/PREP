import java.util.*;

/**
 * LeetCode 136: Single Number (and variations)
 * 
 * Given a non-empty array of integers nums, every element appears twice except for one.
 * Find that single one.
 * 
 * You must implement a solution with a linear runtime complexity and use only constant extra space.
 * 
 * Example:
 * Input: nums = [2,2,1]
 * Output: 1
 * 
 * Input: nums = [4,1,2,1,2]
 * Output: 4
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class SingleNumber {
    
    /**
     * Single Number I: One element appears once, all others appear twice
     * Approach: XOR all elements - duplicates cancel out, single remains
     */
    public int singleNumber(int[] nums) {
        int result = 0;
        
        // XOR all elements: a ^ a = 0, a ^ 0 = a
        for (int num : nums) {
            result ^= num;
        }
        
        return result;
    }
    
    /**
     * Single Number II (LeetCode 137): One element appears once, all others appear three times
     * Approach: Use bit counting and modular arithmetic
     */
    public int singleNumberII(int[] nums) {
        int ones = 0, twos = 0;
        
        for (int num : nums) {
            // Update twos: elements that appeared twice
            twos |= (ones & num);
            
            // Update ones: elements that appeared once
            ones ^= num;
            
            // Clear bits that appeared three times
            int threes = ones & twos;
            ones &= ~threes;
            twos &= ~threes;
        }
        
        return ones;
    }
    
    /**
     * Single Number II - Alternative approach using bit counting
     */
    public int singleNumberIIBitCount(int[] nums) {
        int result = 0;
        
        // Count bits at each position
        for (int i = 0; i < 32; i++) {
            int count = 0;
            
            for (int num : nums) {
                // Count how many numbers have bit i set
                if ((num >> i & 1) == 1) {
                    count++;
                }
            }
            
            // If count is not divisible by 3, the single number has this bit set
            if (count % 3 != 0) {
                result |= (1 << i);
            }
        }
        
        return result;
    }
    
    /**
     * Single Number III (LeetCode 260): Two elements appear once, all others appear twice
     * Approach: Use XOR and bit manipulation to separate two unique numbers
     */
    public int[] singleNumberIII(int[] nums) {
        // Step 1: XOR all numbers to get xor of two unique numbers
        int xor = 0;
        for (int num : nums) {
            xor ^= num;
        }
        
        // Step 2: Find any set bit in xor (rightmost set bit)
        int rightmostSetBit = xor & (-xor);
        
        // Step 3: Divide numbers into two groups based on this bit
        int num1 = 0, num2 = 0;
        for (int num : nums) {
            if ((num & rightmostSetBit) != 0) {
                num1 ^= num; // Group 1: bit is set
            } else {
                num2 ^= num; // Group 2: bit is not set
            }
        }
        
        return new int[]{num1, num2};
    }
    
    /**
     * Extension: Single Number with different frequencies
     * k elements appear m times, one element appears n times (n != m)
     */
    public int singleNumberGeneral(int[] nums, int k, int m) {
        // Use bit counting approach
        int result = 0;
        
        for (int i = 0; i < 32; i++) {
            int count = 0;
            
            for (int num : nums) {
                if ((num >> i & 1) == 1) {
                    count++;
                }
            }
            
            // If count is not divisible by m, the single number has this bit set
            if (count % m != 0) {
                result |= (1 << i);
            }
        }
        
        return result;
    }
    
    /**
     * Alternative solution using HashMap (not bit manipulation, but for comparison)
     */
    public int singleNumberHashMap(int[] nums) {
        Map<Integer, Integer> frequency = new HashMap<>();
        
        // Count frequency of each number
        for (int num : nums) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }
        
        // Find the number with frequency 1
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        
        return -1; // Should never reach here
    }
    
    /**
     * Alternative solution using Set (not bit manipulation, but for comparison)
     */
    public int singleNumberSet(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        
        for (int num : nums) {
            if (seen.contains(num)) {
                seen.remove(num); // Remove duplicate
            } else {
                seen.add(num); // Add new number
            }
        }
        
        return seen.iterator().next();
    }
    
    /**
     * Helper method to demonstrate XOR properties
     */
    public void demonstrateXORProperties() {
        System.out.println("XOR Properties Demonstration:");
        System.out.println("a ^ a = 0: " + (5 ^ 5)); // 0
        System.out.println("a ^ 0 = a: " + (5 ^ 0)); // 5
        System.out.println("XOR is commutative: " + (3 ^ 5) + " = " + (5 ^ 3)); // Same result
        System.out.println("XOR is associative: " + ((3 ^ 5) ^ 7) + " = " + (3 ^ (5 ^ 7))); // Same result
        System.out.println();
    }
    
    /**
     * Helper method to show bit counting approach step by step
     */
    public void demonstrateBitCounting(int[] nums) {
        System.out.println("Bit Counting Demonstration for: " + Arrays.toString(nums));
        
        for (int bit = 0; bit < 4; bit++) { // Show first 4 bits
            int count = 0;
            System.out.print("Bit " + bit + ": ");
            
            for (int num : nums) {
                int bitValue = (num >> bit) & 1;
                System.out.print(bitValue + " ");
                count += bitValue;
            }
            
            System.out.println("(count = " + count + ", count % 3 = " + (count % 3) + ")");
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        SingleNumber solution = new SingleNumber();
        
        // Demonstrate XOR properties
        solution.demonstrateXORProperties();
        
        // Test Single Number I
        int[] nums1 = {2, 2, 1};
        System.out.println("Single Number I:");
        System.out.println("Input: " + Arrays.toString(nums1));
        System.out.println("XOR approach: " + solution.singleNumber(nums1));
        System.out.println("HashMap approach: " + solution.singleNumberHashMap(nums1));
        System.out.println("Set approach: " + solution.singleNumberSet(nums1));
        System.out.println();
        
        // Test Single Number II
        int[] nums2 = {2, 2, 3, 2};
        System.out.println("Single Number II (appears 3 times):");
        System.out.println("Input: " + Arrays.toString(nums2));
        System.out.println("State machine approach: " + solution.singleNumberII(nums2));
        System.out.println("Bit counting approach: " + solution.singleNumberIIBitCount(nums2));
        
        // Demonstrate bit counting
        solution.demonstrateBitCounting(nums2);
        
        // Test Single Number III
        int[] nums3 = {1, 2, 1, 3, 2, 5};
        System.out.println("Single Number III (two unique numbers):");
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Result: " + Arrays.toString(solution.singleNumberIII(nums3)));
        System.out.println();
        
        // Test larger example
        int[] nums4 = {4, 1, 2, 1, 2};
        System.out.println("Larger example:");
        System.out.println("Input: " + Arrays.toString(nums4));
        System.out.println("Result: " + solution.singleNumber(nums4));
        System.out.println();
        
        // Test edge cases
        int[] nums5 = {1};
        System.out.println("Single element:");
        System.out.println("Input: " + Arrays.toString(nums5));
        System.out.println("Result: " + solution.singleNumber(nums5));
        
        // Test general case
        int[] nums6 = {5, 7, 5, 4, 7, 5, 7}; // 4 appears once, others appear 3 times
        System.out.println("\nGeneral case (k=3 elements appear m=3 times, 1 element appears n=1 time):");
        System.out.println("Input: " + Arrays.toString(nums6));
        System.out.println("Result: " + solution.singleNumberGeneral(nums6, 3, 3));
        
        // Performance comparison
        System.out.println("\nPerformance comparison for large array:");
        int[] largeArray = new int[1000001];
        for (int i = 0; i < 500000; i++) {
            largeArray[2 * i] = i;
            largeArray[2 * i + 1] = i;
        }
        largeArray[1000000] = 999999; // Single number
        
        long start = System.nanoTime();
        int result1 = solution.singleNumber(largeArray);
        long time1 = System.nanoTime() - start;
        
        start = System.nanoTime();
        int result2 = solution.singleNumberHashMap(largeArray);
        long time2 = System.nanoTime() - start;
        
        System.out.println("XOR approach: " + time1 / 1000000.0 + " ms");
        System.out.println("HashMap approach: " + time2 / 1000000.0 + " ms");
        System.out.println("Results match: " + (result1 == result2));
    }
} 
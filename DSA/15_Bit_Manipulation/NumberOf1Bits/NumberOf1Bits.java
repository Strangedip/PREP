/**
 * LeetCode 191: Number of 1 Bits (Hamming Weight)
 * 
 * Write a function that takes an unsigned integer and returns the number of '1' bits it has
 * (also known as the Hamming weight).
 * 
 * Example 1:
 * Input: n = 00000000000000000000000000001011
 * Output: 3
 * Explanation: The input binary string has a total of three '1' bits.
 * 
 * Example 2:
 * Input: n = 00000000000000000000000010000000
 * Output: 1
 * 
 * Time Complexity: O(log n) or O(1) for 32-bit integers
 * Space Complexity: O(1)
 */
public class NumberOf1Bits {
    
    /**
     * Approach 1: Check Each Bit ⭐ (Most Intuitive)
     * Check each bit position from right to left
     */
    public int hammingWeight(int n) {
        int count = 0;
        
        // Check each of the 32 bits
        for (int i = 0; i < 32; i++) {
            // Check if bit i is set
            if ((n & (1 << i)) != 0) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Approach 2: Right Shift and Check Last Bit
     * Repeatedly check the rightmost bit and shift right
     */
    public int hammingWeightShift(int n) {
        int count = 0;
        
        while (n != 0) {
            count += (n & 1); // Add 1 if last bit is set
            n >>>= 1;         // Unsigned right shift to handle negative numbers
        }
        
        return count;
    }
    
    /**
     * Approach 3: Brian Kernighan's Algorithm ⭐ (Most Efficient)
     * Clear the rightmost set bit in each iteration
     */
    public int hammingWeightOptimal(int n) {
        int count = 0;
        
        while (n != 0) {
            n &= (n - 1); // Clear the rightmost set bit
            count++;
        }
        
        return count;
    }
    
    /**
     * Approach 4: Built-in Method (Language Specific)
     * Use Java's built-in bit counting function
     */
    public int hammingWeightBuiltIn(int n) {
        return Integer.bitCount(n);
    }
    
    /**
     * Approach 5: Lookup Table
     * Pre-compute bit counts for 8-bit values and combine
     */
    private static final int[] BIT_COUNT_TABLE = new int[256];
    
    static {
        // Initialize lookup table for 8-bit values
        for (int i = 0; i < 256; i++) {
            BIT_COUNT_TABLE[i] = (i & 1) + BIT_COUNT_TABLE[i >>> 1];
        }
    }
    
    public int hammingWeightLookup(int n) {
        return BIT_COUNT_TABLE[n & 0xFF] +           // Last 8 bits
               BIT_COUNT_TABLE[(n >>> 8) & 0xFF] +  // Next 8 bits
               BIT_COUNT_TABLE[(n >>> 16) & 0xFF] + // Next 8 bits  
               BIT_COUNT_TABLE[(n >>> 24) & 0xFF];  // First 8 bits
    }
    
    /**
     * Approach 6: Parallel Bit Counting (SWAR - SIMD Within A Register)
     * Count bits in parallel using bit manipulation tricks
     */
    public int hammingWeightParallel(int n) {
        // Count bits in pairs
        n = n - ((n >>> 1) & 0x55555555);
        
        // Count bits in groups of 4
        n = (n & 0x33333333) + ((n >>> 2) & 0x33333333);
        
        // Count bits in groups of 8
        n = (n + (n >>> 4)) & 0x0F0F0F0F;
        
        // Count bits in groups of 16
        n = n + (n >>> 8);
        
        // Count bits in groups of 32
        n = n + (n >>> 16);
        
        return n & 0x3F; // Mask to get the count (max 32)
    }
    
    /**
     * Extension: Count trailing zeros
     */
    public int countTrailingZeros(int n) {
        if (n == 0) return 32;
        
        int count = 0;
        while ((n & 1) == 0) {
            n >>>= 1;
            count++;
        }
        return count;
    }
    
    /**
     * Extension: Count leading zeros
     */
    public int countLeadingZeros(int n) {
        if (n == 0) return 32;
        
        int count = 0;
        while ((n & 0x80000000) == 0) {
            n <<= 1;
            count++;
        }
        return count;
    }
    
    /**
     * Extension: Check if number is power of 2
     */
    public boolean isPowerOfTwo(int n) {
        // Power of 2 has exactly one bit set
        return n > 0 && (n & (n - 1)) == 0;
    }
    
    /**
     * Extension: Find position of single set bit
     */
    public int findSingleBitPosition(int n) {
        if (!isPowerOfTwo(n)) {
            return -1; // Not a power of 2
        }
        
        int position = 0;
        while ((n & 1) == 0) {
            n >>>= 1;
            position++;
        }
        return position;
    }
    
    /**
     * Helper method to visualize bit operations
     */
    public void demonstrateBrianKernighan(int n) {
        System.out.println("Brian Kernighan's Algorithm for n = " + n + " (" + Integer.toBinaryString(n) + "):");
        int original = n;
        int step = 1;
        
        while (n != 0) {
            int prev = n;
            n &= (n - 1);
            System.out.printf("Step %d: %s & %s = %s\n", 
                            step++,
                            String.format("%32s", Integer.toBinaryString(prev)).replace(' ', '0'),
                            String.format("%32s", Integer.toBinaryString(prev - 1)).replace(' ', '0'),
                            String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0'));
        }
        
        System.out.println("Total 1 bits: " + hammingWeightOptimal(original));
        System.out.println();
    }
    
    /**
     * Helper method to show parallel counting step by step
     */
    public void demonstrateParallelCounting(int n) {
        System.out.println("Parallel Bit Counting for n = " + n + " (" + Integer.toBinaryString(n) + "):");
        
        int original = n;
        System.out.printf("Original:     %32s\n", Integer.toBinaryString(n).replace(' ', '0'));
        
        // Step 1: Count bits in pairs
        n = n - ((n >>> 1) & 0x55555555);
        System.out.printf("After step 1: %32s (count in pairs)\n", Integer.toBinaryString(n));
        
        // Step 2: Count bits in groups of 4
        n = (n & 0x33333333) + ((n >>> 2) & 0x33333333);
        System.out.printf("After step 2: %32s (count in 4s)\n", Integer.toBinaryString(n));
        
        // Step 3: Count bits in groups of 8
        n = (n + (n >>> 4)) & 0x0F0F0F0F;
        System.out.printf("After step 3: %32s (count in 8s)\n", Integer.toBinaryString(n));
        
        // Step 4: Count bits in groups of 16
        n = n + (n >>> 8);
        System.out.printf("After step 4: %32s (count in 16s)\n", Integer.toBinaryString(n));
        
        // Step 5: Count bits in groups of 32
        n = n + (n >>> 16);
        System.out.printf("After step 5: %32s (final sum)\n", Integer.toBinaryString(n));
        
        int result = n & 0x3F;
        System.out.println("Final count: " + result);
        System.out.println("Verification: " + hammingWeight(original));
        System.out.println();
    }
    
    public static void main(String[] args) {
        NumberOf1Bits solution = new NumberOf1Bits();
        
        // Test cases
        int[] testCases = {11, 128, (int) 4294967293L}; // Last one is -3 in 32-bit
        
        for (int n : testCases) {
            System.out.println("Testing n = " + n + " (binary: " + Integer.toBinaryString(n) + ")");
            System.out.println("Basic approach: " + solution.hammingWeight(n));
            System.out.println("Shift approach: " + solution.hammingWeightShift(n));
            System.out.println("Brian Kernighan: " + solution.hammingWeightOptimal(n));
            System.out.println("Built-in: " + solution.hammingWeightBuiltIn(n));
            System.out.println("Lookup table: " + solution.hammingWeightLookup(n));
            System.out.println("Parallel counting: " + solution.hammingWeightParallel(n));
            System.out.println();
        }
        
        // Demonstrate Brian Kernighan's algorithm
        solution.demonstrateBrianKernighan(11);
        
        // Demonstrate parallel counting
        solution.demonstrateParallelCounting(11);
        
        // Test extensions
        System.out.println("Extension tests:");
        int testNum = 8; // Binary: 1000
        System.out.println("Number: " + testNum + " (binary: " + Integer.toBinaryString(testNum) + ")");
        System.out.println("Trailing zeros: " + solution.countTrailingZeros(testNum));
        System.out.println("Leading zeros: " + solution.countLeadingZeros(testNum));
        System.out.println("Is power of 2: " + solution.isPowerOfTwo(testNum));
        System.out.println("Single bit position: " + solution.findSingleBitPosition(testNum));
        System.out.println();
        
        // Performance comparison
        System.out.println("Performance comparison for large iterations:");
        int iterations = 10000000;
        int testValue = 0xAAAAAAAA; // Alternating bits
        
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            solution.hammingWeight(testValue);
        }
        long basicTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            solution.hammingWeightOptimal(testValue);
        }
        long optimalTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            solution.hammingWeightBuiltIn(testValue);
        }
        long builtInTime = System.nanoTime() - start;
        
        System.out.println("Basic approach: " + basicTime / 1000000.0 + " ms");
        System.out.println("Brian Kernighan: " + optimalTime / 1000000.0 + " ms");
        System.out.println("Built-in method: " + builtInTime / 1000000.0 + " ms");
    }
} 
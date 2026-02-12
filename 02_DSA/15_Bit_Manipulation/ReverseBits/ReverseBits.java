/**
 * 190. Reverse Bits
 * 
 * Reverse bits of a 32-bit unsigned integer.
 * Time: O(1) — fixed 32 iterations.
 * Space: O(1)
 */
public class ReverseBits {

    /**
     * Bit-by-bit reversal.
     */
    public int reverseBits(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);
            n >>>= 1;  // Unsigned right shift
        }
        return result;
    }

    /**
     * Divide and conquer: swap progressively smaller chunks.
     */
    public int reverseBitsDnC(int n) {
        n = ((n & 0xFFFF0000) >>> 16) | ((n & 0x0000FFFF) << 16);
        n = ((n & 0xFF00FF00) >>> 8)  | ((n & 0x00FF00FF) << 8);
        n = ((n & 0xF0F0F0F0) >>> 4)  | ((n & 0x0F0F0F0F) << 4);
        n = ((n & 0xCCCCCCCC) >>> 2)  | ((n & 0x33333333) << 2);
        n = ((n & 0xAAAAAAAA) >>> 1)  | ((n & 0x55555555) << 1);
        return n;
    }

    public static void main(String[] args) {
        ReverseBits solution = new ReverseBits();

        int input = 43261596;
        System.out.println("Input:  " + Integer.toBinaryString(input));
        int result = solution.reverseBits(input);
        System.out.println("Output: " + Integer.toBinaryString(result));
        System.out.println("Value:  " + Integer.toUnsignedString(result));
        // Expected: 964176192
    }
}


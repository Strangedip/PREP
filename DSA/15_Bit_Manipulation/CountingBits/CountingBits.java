import java.util.Arrays;

/**
 * 338. Counting Bits
 * 
 * Given n, return array where ans[i] = number of 1's in binary of i.
 * 
 * Approach: DP using dp[i] = dp[i & (i-1)] + 1
 * Time: O(n)
 * Space: O(1) extra
 */
public class CountingBits {

    /**
     * DP with Last Set Bit removal: i & (i-1)
     */
    public int[] countBits(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i & (i - 1)] + 1;
        }
        return dp;
    }

    /**
     * DP with Right Shift
     */
    public int[] countBitsShift(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i >> 1] + (i & 1);
        }
        return dp;
    }

    public static void main(String[] args) {
        CountingBits solution = new CountingBits();

        System.out.println(Arrays.toString(solution.countBits(5)));
        // [0, 1, 1, 2, 1, 2]

        System.out.println(Arrays.toString(solution.countBits(2)));
        // [0, 1, 1]

        System.out.println(Arrays.toString(solution.countBitsShift(8)));
        // [0, 1, 1, 2, 1, 2, 2, 3, 1]
    }
}


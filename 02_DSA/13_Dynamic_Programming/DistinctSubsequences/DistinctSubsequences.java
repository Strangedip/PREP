/**
 * LeetCode 115: Distinct Subsequences
 *
 * Given strings s and t, return the number of distinct subsequences of s
 * which equal t. Two subsequences are distinct if they use different index sets.
 *
 * Optimal: 2D DP — O(m*n) time, O(n) space with optimization.
 */
public class DistinctSubsequences {

    /**
     * Approach 1: 2D Bottom-Up DP.
     *
     * dp[i][j] = number of ways to form t[0..j) as a subsequence of s[0..i).
     *
     * Recurrence:
     *   dp[i][j] = dp[i-1][j]                    (skip s[i-1])
     *            + dp[i-1][j-1]  if s[i-1]==t[j-1] (take s[i-1])
     *
     * Base: dp[i][0] = 1 (empty t is always achievable)
     */
    public int numDistinct(String s, String t) {
        int m = s.length();
        int n = t.length();
        long[][] dp = new long[m + 1][n + 1];

        // Empty target string: one way (delete all characters)
        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Option 1: skip current character of s
                dp[i][j] = dp[i - 1][j];

                // Option 2: take current character if it matches
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }

        return (int) dp[m][n];
    }

    /**
     * Approach 2: Space-Optimized 1D DP.
     *
     * Only the previous row is needed. Iterate j in reverse to preserve
     * dp[j-1] before it gets overwritten.
     */
    public int numDistinctOptimized(String s, String t) {
        int m = s.length();
        int n = t.length();
        long[] dp = new long[n + 1];
        dp[0] = 1;

        for (int i = 1; i <= m; i++) {
            // Reverse order: dp[j] depends on old dp[j-1]
            for (int j = n; j >= 1; j--) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[j] += dp[j - 1];
                }
            }
        }

        return (int) dp[n];
    }

    /**
     * Approach 3: Top-Down Memoization.
     *
     * solve(si, ti) = ways to form t[ti..] from s[si..].
     */
    public int numDistinctMemo(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] memo = new int[m][n];
        for (int[] row : memo) {
            java.util.Arrays.fill(row, -1);
        }
        return dfs(0, 0, s, t, memo);
    }

    private int dfs(int si, int ti, String s, String t, int[][] memo) {
        // Formed entire target
        if (ti == t.length()) {
            return 1;
        }
        // Exhausted source but target remains
        if (si == s.length()) {
            return 0;
        }
        if (memo[si][ti] != -1) {
            return memo[si][ti];
        }

        long count = dfs(si + 1, ti, s, t, memo); // skip s[si]
        if (s.charAt(si) == t.charAt(ti)) {
            count += dfs(si + 1, ti + 1, s, t, memo); // take s[si]
        }

        memo[si][ti] = (int) count;
        return memo[si][ti];
    }

    public static void main(String[] args) {
        DistinctSubsequences solution = new DistinctSubsequences();

        // Test case 1: Classic "rabbbit" example
        String s1 = "rabbbit";
        String t1 = "rabbit";
        System.out.println("Test 1: s=\"" + s1 + "\", t=\"" + t1 + "\"");
        System.out.println("  2D DP:          " + solution.numDistinct(s1, t1));          // 3
        System.out.println("  1D Optimized:   " + solution.numDistinctOptimized(s1, t1)); // 3
        System.out.println("  Memoization:    " + solution.numDistinctMemo(s1, t1));     // 3
        System.out.println();

        // Test case 2: "babgbag" → "bag"
        String s2 = "babgbag";
        String t2 = "bag";
        System.out.println("Test 2: s=\"" + s2 + "\", t=\"" + t2 + "\"");
        System.out.println("  2D DP:          " + solution.numDistinct(s2, t2));          // 5
        System.out.println("  1D Optimized:   " + solution.numDistinctOptimized(s2, t2)); // 5
        System.out.println();

        // Test case 3: Empty target
        String s3 = "abc";
        String t3 = "";
        System.out.println("Test 3: s=\"" + s3 + "\", t=\"" + t3 + "\" (empty target)");
        System.out.println("  2D DP:          " + solution.numDistinct(s3, t3));          // 1
        System.out.println();

        // Test case 4: Empty source, non-empty target
        String s4 = "";
        String t4 = "a";
        System.out.println("Test 4: s=\"" + s4 + "\", t=\"" + t4 + "\"");
        System.out.println("  2D DP:          " + solution.numDistinct(s4, t4));          // 0
        System.out.println();

        // Test case 5: Repeated characters
        String s5 = "aaa";
        String t5 = "aa";
        System.out.println("Test 5: s=\"" + s5 + "\", t=\"" + t5 + "\"");
        System.out.println("  2D DP:          " + solution.numDistinct(s5, t5));          // 3
        System.out.println("  Memoization:    " + solution.numDistinctMemo(s5, t5));     // 3
        System.out.println();

        // Test case 6: No match possible
        String s6 = "abc";
        String t6 = "def";
        System.out.println("Test 6: s=\"" + s6 + "\", t=\"" + t6 + "\" (no match)");
        System.out.println("  2D DP:          " + solution.numDistinct(s6, t6));          // 0
        System.out.println();

        // Test case 7: Identical strings
        String s7 = "abc";
        String t7 = "abc";
        System.out.println("Test 7: s=\"" + s7 + "\", t=\"" + t7 + "\" (identical)");
        System.out.println("  2D DP:          " + solution.numDistinct(s7, t7));          // 1
    }
}

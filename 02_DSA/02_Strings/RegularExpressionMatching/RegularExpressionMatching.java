/**
 * LeetCode 10: Regular Expression Matching
 *
 * Implement regex matching for string s and pattern p with support for:
 *   '.' — matches any single character
 *   '*' — matches zero or more of the preceding element
 *
 * Matching must cover the entire string s.
 *
 * Optimal: 2D DP — O(m*n) time, O(n) space with optimization.
 */
public class RegularExpressionMatching {

    /**
     * Approach 1: 2D Bottom-Up DP.
     *
     * dp[i][j] = true if s[0..i) matches p[0..j).
     *
     * If p[j-1] != '*':
     *   dp[i][j] = dp[i-1][j-1] && chars match (or p[j-1] == '.')
     *
     * If p[j-1] == '*':
     *   dp[i][j] = dp[i][j-2]                          (zero occurrences)
     *            OR dp[i-1][j] if preceding char matches  (one or more)
     */
    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        // Empty string matches patterns like a*b*c* (all char-star pairs)
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);

                if (pc != '*') {
                    // Direct character or dot match
                    dp[i][j] = dp[i - 1][j - 1]
                            && (pc == '.' || sc == pc);
                } else {
                    // Star case: look at the character before '*'
                    char prev = p.charAt(j - 2);

                    // Option 1: treat x* as zero occurrences of x
                    dp[i][j] = dp[i][j - 2];

                    // Option 2: treat x* as one or more (if x matches current s char)
                    if (prev == '.' || prev == sc) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                }
            }
        }

        return dp[m][n];
    }

    /**
     * Approach 2: Top-Down Memoization.
     *
     * match(i, j) = does s[i..] match p[j..]?
     */
    public boolean isMatchMemo(String s, String p) {
        int m = s.length();
        int n = p.length();
        Boolean[][] memo = new Boolean[m + 1][n + 1];
        return dfs(0, 0, s, p, memo);
    }

    private boolean dfs(int i, int j, String s, String p, Boolean[][] memo) {
        if (j == p.length()) {
            return i == s.length();
        }
        if (memo[i][j] != null) {
            return memo[i][j];
        }

        boolean firstMatch = i < s.length()
                && (p.charAt(j) == '.' || p.charAt(j) == s.charAt(i));

        boolean result;
        // Lookahead: next pattern char is '*' → x* group
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            // Zero occurrences: skip "x*"
            result = dfs(i, j + 2, s, p, memo);
            // One or more: consume one char from s, keep pattern at j
            if (firstMatch) {
                result = result || dfs(i + 1, j, s, p, memo);
            }
        } else {
            // Plain char or dot: must match and advance both
            result = firstMatch && dfs(i + 1, j + 1, s, p, memo);
        }

        memo[i][j] = result;
        return result;
    }

    /**
     * Approach 3: Space-Optimized 1D DP.
     *
     * Only the previous row is needed. Iterate j in reverse to preserve
     * dependencies on dp[j-1] and dp[j-2].
     */
    public boolean isMatchOptimized(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[] dp = new boolean[n + 1];
        boolean[] prev = new boolean[n + 1];
        dp[0] = true;

        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[j] = dp[j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            boolean[] next = new boolean[n + 1];
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);

                if (pc != '*') {
                    next[j] = prev[j - 1] && (pc == '.' || sc == pc);
                } else {
                    char prevChar = p.charAt(j - 2);
                    next[j] = next[j - 2]; // zero occurrences
                    if (prevChar == '.' || prevChar == sc) {
                        next[j] = next[j] || dp[j]; // one or more (dp = current row so far)
                    }
                }
            }
            prev = dp;
            dp = next;
        }

        return dp[n];
    }

    public static void main(String[] args) {
        RegularExpressionMatching solution = new RegularExpressionMatching();

        // Test case 1: s="aa", p="a" → false
        System.out.println("Test 1: s=\"aa\", p=\"a\"");
        System.out.println("  2D DP:        " + solution.isMatch("aa", "a"));         // false
        System.out.println("  Memo:         " + solution.isMatchMemo("aa", "a"));     // false
        System.out.println();

        // Test case 2: s="aa", p="a*" → true
        System.out.println("Test 2: s=\"aa\", p=\"a*\"");
        System.out.println("  2D DP:        " + solution.isMatch("aa", "a*"));        // true
        System.out.println("  Memo:         " + solution.isMatchMemo("aa", "a*"));    // true
        System.out.println("  Optimized:    " + solution.isMatchOptimized("aa", "a*")); // true
        System.out.println();

        // Test case 3: s="ab", p=".*" → true
        System.out.println("Test 3: s=\"ab\", p=\".*\"");
        System.out.println("  2D DP:        " + solution.isMatch("ab", ".*"));       // true
        System.out.println();

        // Test case 4: s="aab", p="c*a*b" → true
        System.out.println("Test 4: s=\"aab\", p=\"c*a*b\"");
        System.out.println("  2D DP:        " + solution.isMatch("aab", "c*a*b"));    // true
        System.out.println("  Memo:         " + solution.isMatchMemo("aab", "c*a*b")); // true
        System.out.println();

        // Test case 5: s="mississippi", p="mis*is*p*." → false
        System.out.println("Test 5: s=\"mississippi\", p=\"mis*is*p*.\"");
        System.out.println("  2D DP:        " + solution.isMatch("mississippi", "mis*is*p*.")); // false
        System.out.println();

        // Test case 6: empty string with star pattern
        System.out.println("Test 6: s=\"\", p=\"a*b*\"");
        System.out.println("  2D DP:        " + solution.isMatch("", "a*b*"));       // true
        System.out.println();

        // Test case 7: dot matching
        System.out.println("Test 7: s=\"abc\", p=\"...\"");
        System.out.println("  2D DP:        " + solution.isMatch("abc", "..."));      // true
        System.out.println();

        // Test case 8: pattern too short
        System.out.println("Test 8: s=\"aaa\", p=\"ab\"");
        System.out.println("  2D DP:        " + solution.isMatch("aaa", "ab"));      // false
        System.out.println("  Memo:         " + solution.isMatchMemo("aaa", "ab")); // false
    }
}

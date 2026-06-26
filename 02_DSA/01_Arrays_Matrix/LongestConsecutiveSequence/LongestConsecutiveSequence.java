import java.util.*;

/**
 * Longest Consecutive Sequence — O(n) using HashSet.
 * LeetCode 128
 */
public class LongestConsecutiveSequence {

    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);

        int best = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                int current = num;
                int length = 1;
                while (set.contains(current + 1)) {
                    current++;
                    length++;
                }
                best = Math.max(best, length);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        LongestConsecutiveSequence sol = new LongestConsecutiveSequence();
        System.out.println(sol.longestConsecutive(new int[]{100, 4, 200, 1, 3, 2})); // 4
        System.out.println(sol.longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1})); // 9
    }
}

import java.util.*;

/**
 * Advanced Sliding Window patterns — variable window, at-most K, exactly K.
 */
public class AdvancedSlidingWindow {

    /** Longest substring with at most K distinct characters */
    public int longestSubstringKDistinct(String s, int k) {
        if (k == 0) return 0;
        Map<Character, Integer> freq = new HashMap<>();
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            freq.merge(c, 1, Integer::sum);
            while (freq.size() > k) {
                char leftChar = s.charAt(left);
                freq.merge(leftChar, -1, Integer::sum);
                if (freq.get(leftChar) == 0) freq.remove(leftChar);
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    /** Subarrays with exactly K distinct — atMost(K) - atMost(K-1) */
    public int subarraysWithKDistinct(int[] nums, int k) {
        return atMostKDistinct(nums, k) - atMostKDistinct(nums, k - 1);
    }

    private int atMostKDistinct(int[] nums, int k) {
        if (k < 0) return 0;
        Map<Integer, Integer> freq = new HashMap<>();
        int left = 0, count = 0;
        for (int right = 0; right < nums.length; right++) {
            freq.merge(nums[right], 1, Integer::sum);
            while (freq.size() > k) {
                freq.merge(nums[left], -1, Integer::sum);
                if (freq.get(nums[left]) == 0) freq.remove(nums[left]);
                left++;
            }
            count += right - left + 1;
        }
        return count;
    }

    /** Minimum window substring length with sum >= target */
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                best = Math.min(best, right - left + 1);
                sum -= nums[left++];
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    public static void main(String[] args) {
        AdvancedSlidingWindow sol = new AdvancedSlidingWindow();
        System.out.println(sol.longestSubstringKDistinct("eceba", 2)); // 3
        System.out.println(sol.subarraysWithKDistinct(new int[]{1,2,1,2,3}, 2)); // 7
        System.out.println(sol.minSubArrayLen(7, new int[]{2,3,1,2,4,3})); // 2
    }
}

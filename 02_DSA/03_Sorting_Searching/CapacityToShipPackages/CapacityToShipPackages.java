/**
 * LeetCode 1011: Capacity To Ship Packages Within D Days
 *
 * Return the minimum ship capacity such that all packages can be shipped within
 * the given number of days. Packages must be loaded in order; each day's load
 * cannot exceed capacity.
 *
 * Time Complexity:
 *   - shipWithinDays:      O(n log S) where S = sum(weights)
 *   - shipWithinDaysLinear: O(n · S)
 * Space Complexity: O(1)
 */
public class CapacityToShipPackages {

    /**
     * Approach 1: Binary Search on Answer (Optimal)
     *
     * Search capacity in [max(weights), sum(weights)]. For each candidate capacity,
     * greedily simulate day-by-day loading and count days required.
     */
    public int shipWithinDays(int[] weights, int days) {
        int lo = 0;
        int hi = 0;
        for (int w : weights) {
            lo = Math.max(lo, w); // minimum feasible capacity
            hi += w;               // maximum needed (ship everything in one day)
        }

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canShip(weights, days, mid)) {
                hi = mid; // mid works; try smaller capacity
            } else {
                lo = mid + 1; // mid too small
            }
        }
        return lo;
    }

    /**
     * Approach 2: Linear Search on Capacity (Baseline)
     *
     * Increase capacity one unit at a time until the greedy simulation succeeds.
     * Correct but slower — useful to validate the feasibility function.
     */
    public int shipWithinDaysLinear(int[] weights, int days) {
        int cap = 0;
        for (int w : weights) {
            cap = Math.max(cap, w);
        }
        while (!canShip(weights, days, cap)) {
            cap++;
        }
        return cap;
    }

    /**
     * Greedy feasibility check: pack consecutive packages each day without
     * exceeding capacity. Return true if total days needed <= allowed days.
     */
    private boolean canShip(int[] weights, int days, int capacity) {
        int daysUsed = 1;
        int load = 0;

        for (int w : weights) {
            if (load + w > capacity) {
                daysUsed++;
                load = 0; // start fresh day
            }
            load += w;
        }
        return daysUsed <= days;
    }

    public static void main(String[] args) {
        CapacityToShipPackages solution = new CapacityToShipPackages();

        // Test case 1: classic example
        int[] weights1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int days1 = 5;
        System.out.println("Test 1: weights=" + java.util.Arrays.toString(weights1) + ", days=" + days1);
        System.out.println("  Binary search: " + solution.shipWithinDays(weights1, days1));       // 15
        System.out.println("  Linear search: " + solution.shipWithinDaysLinear(weights1, days1)); // 15
        System.out.println();

        // Test case 2: fewer days
        int[] weights2 = {3, 2, 2, 4, 1, 4};
        int days2 = 3;
        System.out.println("Test 2: weights=" + java.util.Arrays.toString(weights2) + ", days=" + days2);
        System.out.println("  Binary search: " + solution.shipWithinDays(weights2, days2));       // 6
        System.out.println("  Linear search: " + solution.shipWithinDaysLinear(weights2, days2)); // 6
        System.out.println();

        // Test case 3: ship everything in one day
        int[] weights3 = {1, 2, 3};
        int days3 = 1;
        System.out.println("Test 3: weights=" + java.util.Arrays.toString(weights3) + ", days=" + days3);
        System.out.println("  Binary search: " + solution.shipWithinDays(weights3, days3));       // 6
        System.out.println("  Linear search: " + solution.shipWithinDaysLinear(weights3, days3)); // 6
        System.out.println();

        // Test case 4: one package per day
        int[] weights4 = {10, 50, 20, 30};
        int days4 = 4;
        System.out.println("Test 4: weights=" + java.util.Arrays.toString(weights4) + ", days=" + days4);
        System.out.println("  Binary search: " + solution.shipWithinDays(weights4, days4));       // 50
        System.out.println("  Linear search: " + solution.shipWithinDaysLinear(weights4, days4)); // 50
    }
}

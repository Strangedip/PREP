/**
 * Fenwick Tree (Binary Indexed Tree)
 *
 * Supports point updates and prefix/range sum queries in O(log n).
 * Typical use: LeetCode 307 (Range Sum Query — Mutable), inversion counting.
 *
 * Time Complexity:
 *   - FenwickTree:          O(log n) update/query
 *   - PrefixSumArray:       O(1) query, O(n) update
 * Space Complexity: O(n)
 */
public class FenwickTree {

    /**
     * Approach 1: Fenwick Tree (Optimal for dynamic range sum)
     *
     * Internally 1-indexed. tree[i] covers a range of length (i & -i).
     */
    private final int[] tree;
    private final int n;

    public FenwickTree(int n) {
        this.n = n;
        this.tree = new int[n + 1]; // index 0 unused
    }

    /** Build Fenwick tree from a 0-indexed source array. */
    public FenwickTree(int[] arr) {
        this.n = arr.length;
        this.tree = new int[n + 1];
        for (int i = 0; i < n; i++) {
            update(i + 1, arr[i]);
        }
    }

    /** Add delta to position i (1-indexed). */
    public void update(int i, int delta) {
        for (; i <= n; i += i & -i) {
            tree[i] += delta;
        }
    }

    /** Prefix sum from index 1 to i (inclusive, 1-indexed). */
    public int prefixSum(int i) {
        int sum = 0;
        for (; i > 0; i -= i & -i) {
            sum += tree[i];
        }
        return sum;
    }

    /** Range sum on [l, r] inclusive (1-indexed). */
    public int rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }

    /**
     * Approach 2: Prefix Sum Array with O(n) Updates (Baseline)
     *
     * Fast range queries but each point update rebuilds the suffix — fine for
     * static or rarely-updated arrays only.
     */
    static class PrefixSumArray {
        private final int[] arr;
        private final int[] prefix;
        private final int n;

        public PrefixSumArray(int[] source) {
            this.n = source.length;
            this.arr = source.clone();
            this.prefix = new int[n + 1];
            for (int i = 0; i < n; i++) {
                prefix[i + 1] = prefix[i] + arr[i];
            }
        }

        public void update(int index, int delta) {
            arr[index] += delta;
            // Rebuild suffix of prefix array — O(n)
            for (int i = index + 1; i <= n; i++) {
                prefix[i] += delta;
            }
        }

        public int rangeSum(int l, int r) {
            // 0-indexed [l, r]
            return prefix[r + 1] - prefix[l];
        }
    }

    public static void main(String[] args) {
        int[] data = {1, 3, 5, 7, 9, 11};

        // --- Fenwick Tree ---
        System.out.println("=== Fenwick Tree ===");
        FenwickTree bit = new FenwickTree(data);
        System.out.println("Initial rangeSum(1..4) [1-indexed 2..5]: " + bit.rangeSum(2, 5)); // 3+5+7+9 = 24

        bit.update(3, 2); // add 2 at 0-indexed position 2 → value 5 becomes 7
        System.out.println("After update(3, +2), rangeSum(2..5): " + bit.rangeSum(2, 5));  // 26
        System.out.println("prefixSum(4): " + bit.prefixSum(4)); // 1+3+7+7 = 18
        System.out.println();

        // --- Prefix Sum Baseline ---
        System.out.println("=== Prefix Sum Array (O(n) update) ===");
        PrefixSumArray psa = new PrefixSumArray(data);
        System.out.println("Initial rangeSum(1..4) [0-indexed]: " + psa.rangeSum(1, 4)); // 24

        psa.update(2, 2);
        System.out.println("After update(2, +2), rangeSum(1..4): " + psa.rangeSum(1, 4)); // 26
        System.out.println();

        // --- Step-by-step Fenwick walkthrough ---
        System.out.println("=== Build from scratch ===");
        FenwickTree small = new FenwickTree(5);
        int[] vals = {2, 4, 1, 3, 5};
        for (int i = 0; i < vals.length; i++) {
            small.update(i + 1, vals[i]);
        }
        System.out.println("Array: " + java.util.Arrays.toString(vals));
        System.out.println("prefixSum(3): " + small.prefixSum(3));   // 2+4+1 = 7
        System.out.println("rangeSum(2..4): " + small.rangeSum(2, 4)); // 4+1+3 = 8
    }
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * LeetCode 380: Insert Delete GetRandom O(1)
 *
 * Design a set supporting insert, remove, and getRandom in average O(1) time.
 *
 * Time Complexity:
 *   - RandomizedSet (hybrid):  O(1) average per operation
 *   - NaiveRandomizedSet:      O(1) insert/remove, O(n) getRandom
 * Space Complexity: O(n)
 */
public class RandomizedSet {

    /**
     * Approach 1: ArrayList + HashMap (Optimal)
     *
     * List provides O(1) random access; map provides O(1) value lookup.
     * Remove uses swap-with-last trick to avoid O(n) middle deletion.
     */
    private final List<Integer> values;
    private final Map<Integer, Integer> indexMap;
    private final Random random;

    public RandomizedSet() {
        this.values = new ArrayList<>();
        this.indexMap = new HashMap<>();
        this.random = new Random();
    }

    /** Insert val if absent. Returns false if already present. */
    public boolean insert(int val) {
        if (indexMap.containsKey(val)) {
            return false;
        }
        indexMap.put(val, values.size());
        values.add(val);
        return true;
    }

    /** Remove val if present. Swap with last element for O(1) deletion. */
    public boolean remove(int val) {
        if (!indexMap.containsKey(val)) {
            return false;
        }

        int idx = indexMap.get(val);
        int lastVal = values.get(values.size() - 1);

        // Move last element into the removed slot
        values.set(idx, lastVal);
        indexMap.put(lastVal, idx);

        // Pop last and drop removed value from map
        values.remove(values.size() - 1);
        indexMap.remove(val);
        return true;
    }

    /** Return a uniformly random element. Assumes set is non-empty. */
    public int getRandom() {
        return values.get(random.nextInt(values.size()));
    }

    /**
     * Approach 2: HashSet + Array Snapshot (Baseline — slow getRandom)
     *
     * Demonstrates why HashSet alone cannot satisfy O(1) getRandom.
     * Included for comparison in main(); not used by LeetCode 380.
     */
    static class NaiveRandomizedSet {
        private final Set<Integer> set = new HashSet<>();
        private final Random random = new Random();

        public boolean insert(int val) {
            return set.add(val);
        }

        public boolean remove(int val) {
            return set.remove(val);
        }

        public int getRandom() {
            // O(n) — must materialize array from set
            Integer[] arr = set.toArray(new Integer[0]);
            return arr[random.nextInt(arr.length)];
        }
    }

    public static void main(String[] args) {
        RandomizedSet set = new RandomizedSet();

        // LeetCode example sequence
        System.out.println("=== Hybrid ArrayList + HashMap ===");
        System.out.println("insert(1): " + set.insert(1));   // true
        System.out.println("remove(2): " + set.remove(2));   // false (not present)
        System.out.println("insert(2): " + set.insert(2));   // true
        System.out.println("getRandom(): " + set.getRandom()); // 1 or 2
        System.out.println("remove(1): " + set.remove(1));   // true
        System.out.println("insert(2): " + set.insert(2));   // false (duplicate)
        System.out.println("getRandom(): " + set.getRandom()); // 2
        System.out.println();

        // Swap-and-pop edge case: remove last element
        RandomizedSet set2 = new RandomizedSet();
        set2.insert(10);
        set2.insert(20);
        set2.insert(30);
        System.out.println("Before remove(30): size=" + set2.values.size());
        System.out.println("remove(30): " + set2.remove(30)); // true — swap with self
        System.out.println("After remove(30): size=" + set2.values.size()
                + ", contents=" + set2.values);
        System.out.println();

        // Naive approach comparison
        NaiveRandomizedSet naive = new NaiveRandomizedSet();
        naive.insert(5);
        naive.insert(15);
        System.out.println("=== Naive HashSet (O(n) getRandom) ===");
        System.out.println("getRandom(): " + naive.getRandom());
    }
}

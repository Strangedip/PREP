/**
 * Segment Tree — range sum query with point updates.
 * Supports O(log n) update and query on array.
 */
public class SegmentTree {
    private int[] tree;
    private int n;

    public SegmentTree(int[] nums) {
        n = nums.length;
        tree = new int[4 * n];
        build(nums, 0, 0, n - 1);
    }

    private void build(int[] nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
            return;
        }
        int mid = (start + end) / 2;
        build(nums, 2 * node + 1, start, mid);
        build(nums, 2 * node + 2, mid + 1, end);
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }

    public void update(int index, int val) {
        update(0, 0, n - 1, index, val);
    }

    private void update(int node, int start, int end, int index, int val) {
        if (start == end) {
            tree[node] = val;
            return;
        }
        int mid = (start + end) / 2;
        if (index <= mid) update(2 * node + 1, start, mid, index, val);
        else update(2 * node + 2, mid + 1, end, index, val);
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }

    public int query(int left, int right) {
        return query(0, 0, n - 1, left, right);
    }

    private int query(int node, int start, int end, int left, int right) {
        if (right < start || left > end) return 0;
        if (left <= start && end <= right) return tree[node];
        int mid = (start + end) / 2;
        return query(2 * node + 1, start, mid, left, right)
                + query(2 * node + 2, mid + 1, end, left, right);
    }

    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 7, 9, 11};
        SegmentTree st = new SegmentTree(nums);
        System.out.println(st.query(1, 3)); // 15 (3+5+7)
        st.update(1, 10);
        System.out.println(st.query(1, 3)); // 22 (10+5+7)
    }
}

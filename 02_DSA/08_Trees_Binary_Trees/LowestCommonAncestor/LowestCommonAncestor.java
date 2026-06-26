/**
 * Lowest Common Ancestor of Binary Tree — LeetCode 236
 */
public class LowestCommonAncestor {
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int x) { val = x; }
    }

    /** Recursive — O(n) */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        TreeNode p = root.left, q = root.right;
        LowestCommonAncestor sol = new LowestCommonAncestor();
        System.out.println(sol.lowestCommonAncestor(root, p, q).val); // 3
    }
}

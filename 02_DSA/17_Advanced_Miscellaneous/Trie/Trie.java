import java.util.*;

/**
 * Trie (Prefix Tree) — insert, search, startsWith, plus autocomplete.
 */
public class Trie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        String word; // for autocomplete — store word at terminal node
    }

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEnd = true;
        node.word = word;
    }

    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;
        }
        return node;
    }

    /** Return all words with given prefix (autocomplete) */
    public List<String> autocomplete(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node == null) return result;
        dfs(node, result);
        return result;
    }

    private void dfs(TrieNode node, List<String> result) {
        if (node.isEnd) result.add(node.word);
        for (TrieNode child : node.children.values()) {
            dfs(child, result);
        }
    }

    /** XOR maximum pair — Trie with bits (LeetCode 421 variant) */
    public int findMaximumXOR(int[] nums) {
        TrieNode bitRoot = new TrieNode();
        for (int num : nums) {
            TrieNode node = bitRoot;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >> i) & 1;
                node.children.putIfAbsent((char) bit, new TrieNode());
                node = node.children.get((char) bit);
            }
        }
        int max = 0;
        for (int num : nums) {
            TrieNode node = bitRoot;
            int xor = 0;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >> i) & 1;
                int want = (char) (bit ^ 1);
                if (node.children.containsKey((char) want)) {
                    xor |= (1 << i);
                    node = node.children.get((char) want);
                } else {
                    node = node.children.get((char) bit);
                }
            }
            max = Math.max(max, xor);
        }
        return max;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        System.out.println(trie.search("app"));       // true
        System.out.println(trie.startsWith("app"));   // true
        System.out.println(trie.autocomplete("app")); // [app, apple, application]
    }
}

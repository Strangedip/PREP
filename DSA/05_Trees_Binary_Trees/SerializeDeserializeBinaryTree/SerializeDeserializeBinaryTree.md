# Serialize and Deserialize Binary Tree

## Problem Statement

**Serialization** is the process of converting a data structure or object into a sequence of bits so that it can be stored in a file or memory buffer, or transmitted across a network connection link to be reconstructed later in the same or another computer environment.

Design an algorithm to serialize and deserialize a binary tree. There is no restriction on how your serialization/deserialization algorithm should work. You just need to ensure that a binary tree can be serialized to a string and this string can be deserialized to the original tree structure.

## Examples

**Example 1:**
```
Input: root = [1,2,3,null,null,4,5]
Output: [1,2,3,null,null,4,5]

Tree structure:
    1
   / \
  2   3
     / \
    4   5
```

**Example 2:**
```
Input: root = []
Output: []
```

**Example 3:**
```
Input: root = [1]
Output: [1]
```

## Constraints

- The number of nodes in the tree is in the range [0, 10⁴]
- -1000 ≤ Node.val ≤ 1000

## Solutions

### Approach 1: Preorder Traversal (DFS) ⭐ (Most Popular)

**Algorithm:**
1. **Serialize:** Use preorder traversal (root → left → right)
2. **Null Handling:** Use special marker (e.g., "#") for null nodes
3. **Deserialize:** Reconstruct tree using preorder sequence

**Implementation:**
```java
public class Codec {
    private static final String NULL_MARKER = "#";
    private static final String DELIMITER = ",";
    
    // Serialize
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }
    
    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append(NULL_MARKER).append(DELIMITER);
            return;
        }
        
        sb.append(node.val).append(DELIMITER);
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }
    
    // Deserialize
    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(DELIMITER)));
        return deserializeHelper(queue);
    }
    
    private TreeNode deserializeHelper(Queue<String> queue) {
        String val = queue.poll();
        
        if (NULL_MARKER.equals(val)) return null;
        
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }
}
```

**Example Serialization:**
```
Tree: [1,2,3,null,null,4,5]
Serialized: "1,2,#,#,3,4,#,#,5,#,#,"
```

**Time Complexity:** O(n) for both serialize and deserialize  
**Space Complexity:** O(n) for storage, O(h) for recursion stack

**Pros:** Simple, intuitive, efficient  
**Cons:** Includes null markers, slightly verbose

### Approach 2: Level Order Traversal (BFS)

**Algorithm:**
1. **Serialize:** Use level-order traversal (breadth-first)
2. **Null Handling:** Include null nodes in serialization
3. **Deserialize:** Reconstruct level by level

**Implementation:**
```java
public class Codec {
    public String serialize(TreeNode root) {
        if (root == null) return "";
        
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            if (node == null) {
                sb.append("null,");
            } else {
                sb.append(node.val).append(",");
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }
        return sb.toString();
    }
    
    public TreeNode deserialize(String data) {
        if (data.isEmpty()) return null;
        
        String[] values = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(values[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode node = queue.poll();
            
            // Left child
            if (!"null".equals(values[i])) {
                node.left = new TreeNode(Integer.parseInt(values[i]));
                queue.offer(node.left);
            }
            i++;
            
            // Right child
            if (i < values.length && !"null".equals(values[i])) {
                node.right = new TreeNode(Integer.parseInt(values[i]));
                queue.offer(node.right);
            }
            i++;
        }
        return root;
    }
}
```

**Example Serialization:**
```
Tree: [1,2,3,null,null,4,5]
Serialized: "1,2,3,null,null,4,5,null,null,null,null,"
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

**Pros:** Similar to LeetCode format, intuitive level structure  
**Cons:** More null nodes in output, larger serialized string

### Approach 3: Postorder Traversal

**Algorithm:**
1. **Serialize:** Use postorder traversal (left → right → root)
2. **Deserialize:** Build tree from right to left

**Implementation:**
```java
public class Codec {
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }
    
    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("#,");
            return;
        }
        
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
        sb.append(node.val).append(",");
    }
    
    public TreeNode deserialize(String data) {
        List<String> list = new ArrayList<>(Arrays.asList(data.split(",")));
        return deserializeHelper(list);
    }
    
    private TreeNode deserializeHelper(List<String> list) {
        if (list.isEmpty()) return null;
        
        String val = list.remove(list.size() - 1);
        if ("#".equals(val)) return null;
        
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.right = deserializeHelper(list);  // Right first!
        node.left = deserializeHelper(list);
        return node;
    }
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

**Pros:** Alternative traversal approach  
**Cons:** Less intuitive, requires careful ordering

### Approach 4: Compact Encoding (No Delimiters)

**Algorithm:**
1. **Serialize:** Encode value lengths to avoid delimiters
2. **Space Efficient:** No delimiter characters needed

**Implementation:**
```java
public class Codec {
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }
    
    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("X");
            return;
        }
        
        String val = String.valueOf(node.val);
        sb.append(val.length()).append(val);
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }
    
    public TreeNode deserialize(String data) {
        int[] index = {0};
        return deserializeHelper(data, index);
    }
    
    private TreeNode deserializeHelper(String data, int[] index) {
        if (index[0] >= data.length()) return null;
        
        char c = data.charAt(index[0]++);
        if (c == 'X') return null;
        
        int len = c - '0';
        int val = Integer.parseInt(data.substring(index[0], index[0] + len));
        index[0] += len;
        
        TreeNode node = new TreeNode(val);
        node.left = deserializeHelper(data, index);
        node.right = deserializeHelper(data, index);
        return node;
    }
}
```

**Example Serialization:**
```
Tree: [1,2,3]
Serialized: "111121313X"
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

**Pros:** Most space-efficient  
**Cons:** More complex parsing, limited to single-digit lengths

### Approach 5: Bracket Representation

**Algorithm:**
1. **Serialize:** Use nested brackets to represent tree structure
2. **Human Readable:** Easy to understand format

**Implementation:**
```java
public class Codec {
    public String serialize(TreeNode root) {
        if (root == null) return "";
        
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }
    
    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) return;
        
        sb.append(node.val);
        
        if (node.left != null || node.right != null) {
            sb.append("(");
            serializeHelper(node.left, sb);
            sb.append(")(");
            serializeHelper(node.right, sb);
            sb.append(")");
        }
    }
    
    // Deserialize implementation similar to parsing expressions
}
```

**Example Serialization:**
```
Tree: [1,2,3]
Serialized: "1(2)(3)"
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

**Pros:** Human-readable, compact for sparse trees  
**Cons:** Complex parsing logic

### Approach 6: Iterative (No Recursion)

**Algorithm:**
1. **Serialize:** Use stack instead of recursion
2. **Deserialize:** Iteratively build tree

**Implementation:**
```java
public class Codec {
    public String serialize(TreeNode root) {
        if (root == null) return "#";
        
        StringBuilder sb = new StringBuilder();
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            
            if (node == null) {
                sb.append("#,");
            } else {
                sb.append(node.val).append(",");
                stack.push(node.right);  // Right first
                stack.push(node.left);
            }
        }
        return sb.toString();
    }
    
    // Deserialize with careful stack management
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

**Pros:** No recursion, no stack overflow risk  
**Cons:** More complex implementation

## Design Considerations

### Format Choice Factors

1. **Space Efficiency**
   - Compact encoding minimizes storage
   - Consider network transmission costs

2. **Parse Complexity**
   - Simple formats are easier to implement
   - Complex formats may have edge cases

3. **Human Readability**
   - Debugging and logging benefits
   - JSON-like formats are familiar

4. **Performance Requirements**
   - Serialization speed vs size trade-offs
   - Memory usage considerations

### Null Handling Strategies

| Strategy | Example | Pros | Cons |
|----------|---------|------|------|
| Explicit Markers | "#" or "null" | Clear, unambiguous | Increases size |
| Omit Nulls | Skip in serialization | Space efficient | Complex reconstruction |
| Special Values | Use INT_MIN | No extra characters | Value range limited |

### Error Handling

```java
// Robust deserialize with error checking
public TreeNode deserialize(String data) {
    if (data == null || data.isEmpty()) return null;
    
    try {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeHelper(queue);
    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid serialized data");
    }
}
```

## Performance Comparison

| Approach | Serialize Time | Deserialize Time | Space Usage | Readability |
|----------|---------------|------------------|-------------|-------------|
| Preorder | O(n) | O(n) | Moderate | High |
| Level Order | O(n) | O(n) | High | High |
| Postorder | O(n) | O(n) | Moderate | Medium |
| Compact | O(n) | O(n) | Low | Low |
| Bracket | O(n) | O(n) | Variable | High |
| Iterative | O(n) | O(n) | Moderate | Medium |

## Common Mistakes

1. **Delimiter Issues:** Forgetting delimiters or using wrong separators
2. **Null Handling:** Inconsistent null node representation
3. **Index Management:** Off-by-one errors in string parsing
4. **Edge Cases:** Not handling empty trees or single nodes
5. **Data Types:** Integer overflow or parsing errors

## Test Cases

```java
// Test cases to validate implementation
TreeNode test1 = [1,2,3,null,null,4,5];
TreeNode test2 = [1];
TreeNode test3 = null;
TreeNode test4 = [1,2,3,4,5,6,7];  // Complete tree
TreeNode test5 = [1,null,2,null,3];  // Skewed tree
TreeNode test6 = [-1000, 1000];  // Boundary values
```

## Related Problems

- **Construct Binary Tree from Traversals** - Tree reconstruction patterns
- **Binary Tree Level Order Traversal** - BFS traversal techniques
- **Clone Graph** - Serialization concepts for graphs
- **Encode and Decode Strings** - String encoding principles

## Interview Tips

1. **Ask Clarifications:** Understand requirements (space, speed, format)
2. **Start Simple:** Begin with preorder approach
3. **Handle Edge Cases:** Empty trees, single nodes, boundary values
4. **Discuss Trade-offs:** Time vs space, simplicity vs efficiency
5. **Test Thoroughly:** Serialize then deserialize to verify
6. **Error Handling:** Consider malformed input scenarios

## Follow-up Questions

1. **"Make it more space-efficient"** → Discuss compact encoding
2. **"Handle very large trees"** → Iterative approach, streaming
3. **"Support multiple tree types"** → Generic serialization
4. **"Network transmission"** → Compression, binary formats

This problem is excellent for demonstrating understanding of tree traversal algorithms, string manipulation, and system design principles. The variety of valid approaches makes it great for discussing algorithmic trade-offs in technical interviews. 
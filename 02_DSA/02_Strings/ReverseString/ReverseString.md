# Reverse String

> **You are here**: DSA — see [ROADMAP](../../../ROADMAP.md) for level assignment
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Study path**: [StudyGuide](../../StudyGuide.md)
> **Pattern**: [Two Pointers](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-1-two-pointers) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Write a function that reverses a string. The input string is given as an array of characters `s`. You must do this by modifying the input array **in-place** with O(1) extra memory.

**LeetCode**: [344. Reverse String](https://leetcode.com/problems/reverse-string/)

### Examples

```
Input:  s = ["h","e","l","l","o"]
Output: ["o","l","l","e","h"]

Input:  s = ["H","a","n","n","a","h"]
Output: ["h","a","n","n","a","H"]
```

### Constraints
- `1 <= s.length <= 10^5`
- `s[i]` is a printable ASCII character

---

## Approach 1: Two Pointers (Optimal)

**Time**: O(n), **Space**: O(1)

This is the canonical two pointers problem. Place one pointer at the start and one at the end. Swap the characters at both pointers and move them inward until they meet.

### Why This Works
- Each swap puts one character from the front into its reversed position at the back, and vice versa.
- After n/2 swaps, every character is in its final reversed position.
- We only use two integer variables (the pointers), so space is O(1).

### Visual Walkthrough

```
Input: ["h", "e", "l", "l", "o"]

Step 1:  left=0, right=4 → swap h ↔ o → ["o", "e", "l", "l", "h"]
Step 2:  left=1, right=3 → swap e ↔ l → ["o", "l", "l", "e", "h"]
Step 3:  left=2, right=2 → left >= right → STOP

Output: ["o", "l", "l", "e", "h"]
```

### Java Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s=["h","e","l","l","o"]"]
    START --> INIT["Init left=0, right=end"]
    INIT --> WINDOW["Adjust window / pointers"]
    WINDOW --> UPDATE["Update best answer"]
    UPDATE --> MORE{"More elements?"}
    MORE -->|yes| WINDOW
    MORE -->|no| DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: s=["h","e","l","l","o"] → ["o","l","l","e","h"]
Approach: Two Pointers (Optimal)

Initialize two pointers at boundaries
Move pointer that improves constraint
Update best answer each step
```
```java
class Solution {
    public void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left < right) {
            // Swap characters at left and right pointers
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;

            left++;
            right--;
        }
    }
}
```

### XOR Swap Variant (No Temp Variable)

```java
class Solution {
    public void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left < right) {
            s[left] ^= s[right];
            s[right] ^= s[left];
            s[left] ^= s[right];

            left++;
            right--;
        }
    }
}
```

> **Interview Tip**: The XOR swap trick is a nice-to-know but in practice the temp variable version is cleaner and easier to read. Mention the XOR variant as a bonus if time permits.

---

## Approach 2: Recursive

**Time**: O(n), **Space**: O(n) — recursion stack

This approach is less optimal due to stack space but demonstrates recursion understanding.


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s=["h","e","l","l","o"]"]
    START --> VISIT["Visit current state"]
    VISIT --> CHOICE{"More choices?"}
    CHOICE -->|yes| RECUR["Recurse / backtrack"]
    RECUR --> UNDO["Undo choice"]
    UNDO --> CHOICE
    CHOICE -->|no| DONE["Return / collect result"]
```

**Walkthrough (same example):**

```
Example: s=["h","e","l","l","o"] → ["o","l","l","e","h"]
Approach: Recursive

Visit current node/state
Recurse on valid next choices
Backtrack and try alternatives
```
```java
class Solution {
    public void reverseString(char[] s) {
        reverseHelper(s, 0, s.length - 1);
    }

    private void reverseHelper(char[] s, int left, int right) {
        if (left >= right) {
            return;
        }
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;

        reverseHelper(s, left + 1, right - 1);
    }
}
```

> **Caution**: For very long strings (approaching 10^5), the recursive approach can cause a StackOverflowError due to deep recursion. The iterative approach is always preferred.

---

## Complexity Analysis

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Two Pointers (Iterative) | O(n) | O(1) | Optimal — preferred in interviews |
| Recursive | O(n) | O(n) | Stack frames use O(n) space |
| XOR Swap | O(n) | O(1) | Same complexity as Two Pointers, trickier to read |

Where n = length of the character array.

---

## Edge Cases

| Case | Input | Output | Why It Matters |
|------|-------|--------|---------------|
| Single character | `["a"]` | `["a"]` | left >= right immediately, no swap needed |
| Two characters | `["a","b"]` | `["b","a"]` | Single swap, then pointers cross |
| All same characters | `["a","a","a"]` | `["a","a","a"]` | Swaps happen but result is same |
| Palindrome | `["r","a","c","e","c","a","r"]` | `["r","a","c","e","c","a","r"]` | Result equals input — valid edge case |
| Special characters | `["!","@","#"]` | `["#","@","!"]` | Works with any printable ASCII |

---

## Interview Tips

1. **This is a warm-up problem.** Most interviewers use it to get you comfortable. Solve it quickly and cleanly.
2. **Always mention the constraint**: "I will do this in-place with O(1) extra space."
3. **State the complexity before coding**: "Two pointers, O(n) time, O(1) space."
4. **Mention the recursive approach as an alternative** but explain why iterative is better (no stack overflow risk, true O(1) space).
5. **Common follow-up**: "What if you need to reverse only vowels?" — This becomes the [Reverse Vowels of a String](https://leetcode.com/problems/reverse-vowels-of-a-string/) problem, where you use the same two-pointer approach but skip non-vowel characters.

---

## Related Problems

| Problem | Key Difference | LeetCode |
|---------|---------------|----------|
| Reverse String II | Reverse first k chars of every 2k block | [541](https://leetcode.com/problems/reverse-string-ii/) |
| Reverse Words in a String III | Reverse each word individually | [557](https://leetcode.com/problems/reverse-words-in-a-string-iii/) |
| Reverse Words in a String | Reverse word order, not character order | [151](https://leetcode.com/problems/reverse-words-in-a-string/) |
| Reverse Vowels of a String | Only swap vowels, skip consonants | [345](https://leetcode.com/problems/reverse-vowels-of-a-string/) |
| Palindrome Linked List | Reverse second half, then compare | [234](https://leetcode.com/problems/palindrome-linked-list/) |

---

## Real-World Applications

- **Text processing**: Reversing strings is fundamental in text editors, parsers, and compilers.
- **Palindrome checking**: Reverse and compare is one approach to palindrome validation.
- **Array rotation**: The reverse trick is the foundation of the O(1) space array rotation algorithm.
- **Stack-based operations**: Reversing the order of elements mirrors how a stack works (LIFO).

---

**Difficulty**: Easy
**Must-Know**: Yes (warm-up, fundamental two-pointer technique)

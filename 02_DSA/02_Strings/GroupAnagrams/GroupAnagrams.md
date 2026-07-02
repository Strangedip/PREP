# Group Anagrams (LeetCode 49)

> **You are here**: DSA — see [ROADMAP](../../../ROADMAP.md) for level assignment
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Study path**: [StudyGuide](../../StudyGuide.md)
> **Pattern**: [Hash Map / Set](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-1-two-pointers) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Given an array of strings `strs`, group the anagrams together. You can return the answer in any order.

An anagram is a word or phrase formed by rearranging the letters of a different word or phrase, typically using all the original letters exactly once.

**Example 1:**
```
Input: strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
```

**Example 2:**
```
Input: strs = [""]
Output: [[""]]
```

**Example 3:**
```
Input: strs = ["a"]
Output: [["a"]]
```

**Constraints:**
- `1 <= strs.length <= 10^4`
- `0 <= strs[i].length <= 100`
- `strs[i]` consists of lowercase English letters.

---

## Approach 1: Sort Each String as Key

**Time:** O(n × k log k) where n = number of strings, k = max string length
**Space:** O(n × k)

### How It Works

Two strings are anagrams if and only if their sorted forms are identical. For example, sorting "eat" gives "aet", and sorting "tea" also gives "aet". We use the sorted form as a HashMap key to group anagrams together.

### Complete Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: strs=["eat","tea","tan","ate","nat","bat"]"]
    START --> STEP1["Sort Each String as Key: step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: strs=["eat","tea","tan","ate","nat","bat"] → [["bat"],["nat","tan"],["ate","eat","tea"]]
Approach: Sort Each String as Key

Apply Sort Each String as Key on the example input step by step
Final answer from example: see above
```
```java
import java.util.*;

public class GroupAnagrams {
    
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String str : strs) {
            // Sort the string to create a canonical key
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            // Group by sorted key
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(map.values());
    }
}
```

### Dry Run Example

```
Input: ["eat", "tea", "tan", "ate", "nat", "bat"]

Processing "eat": sort → "aet", map = {"aet": ["eat"]}
Processing "tea": sort → "aet", map = {"aet": ["eat", "tea"]}
Processing "tan": sort → "ant", map = {"aet": ["eat", "tea"], "ant": ["tan"]}
Processing "ate": sort → "aet", map = {"aet": ["eat", "tea", "ate"], "ant": ["tan"]}
Processing "nat": sort → "ant", map = {"aet": ["eat", "tea", "ate"], "ant": ["tan", "nat"]}
Processing "bat": sort → "abt", map = {"aet": ["eat", "tea", "ate"], "ant": ["tan", "nat"], "abt": ["bat"]}

Result: [["eat", "tea", "ate"], ["tan", "nat"], ["bat"]]
```

### Why This Works

- Anagrams have the exact same characters, just in different order.
- Sorting gives a unique canonical form for each group of anagrams.
- The HashMap groups all strings that share the same canonical form.

---

## Approach 2: Character Count Array as Key (Optimal)

**Time:** O(n × k) where n = number of strings, k = max string length
**Space:** O(n × k)

### How It Works

Instead of sorting (which costs O(k log k) per string), count the frequency of each character and use the frequency array as the HashMap key. Since we only have lowercase English letters, this is an array of 26 integers.

### Complete Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: strs=["eat","tea","tan","ate","nat","bat"]"]
    START --> STEP1["Character Count Array as Key (Optimal): step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: strs=["eat","tea","tan","ate","nat","bat"] → [["bat"],["nat","tan"],["ate","eat","tea"]]
Approach: Character Count Array as Key (Optimal)

Apply Character Count Array as Key (Optimal) on the example input step by step
Final answer from example: see above
```
```java
import java.util.*;

public class GroupAnagramsOptimal {
    
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String str : strs) {
            // Count character frequencies
            int[] count = new int[26];
            for (char c : str.toCharArray()) {
                count[c - 'a']++;
            }
            
            // Convert count array to string key
            // Use delimiter to avoid collisions like [1,2,3] vs [12,3]
            StringBuilder keyBuilder = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                keyBuilder.append('#');
                keyBuilder.append(count[i]);
            }
            String key = keyBuilder.toString();
            
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(map.values());
    }
}
```

### Dry Run Example

```
Input: ["eat", "tea", "tan"]

Processing "eat":
  count: a=1, e=1, t=1 → key = "#1#0#0#0#1#0#0#...#1#0#0#0#0#0"
  map: {key1: ["eat"]}

Processing "tea":
  count: a=1, e=1, t=1 → key = "#1#0#0#0#1#0#0#...#1#0#0#0#0#0"  (SAME key!)
  map: {key1: ["eat", "tea"]}

Processing "tan":
  count: a=1, n=1, t=1 → key = "#1#0#0#0#0#0#0#...#1#0#0#0#1#0#...#1#0#0#0#0#0"
  map: {key1: ["eat", "tea"], key2: ["tan"]}
```

### Why the Delimiter Is Critical

Without a delimiter, the count array `[1, 12, 3]` and `[11, 2, 3]` would produce the same string "1123". With the `#` delimiter, they become `#1#12#3` and `#11#2#3`, which are distinct.

---

## Approach 3: Prime Number Product (Mathematical)

**Time:** O(n × k), **Space:** O(n × k)

### How It Works

Assign a unique prime number to each letter (a=2, b=3, c=5, d=7, ...). The product of primes for a string is unique for each anagram group (by the Fundamental Theorem of Arithmetic: every integer has a unique prime factorization).


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: strs=["eat","tea","tan","ate","nat","bat"]"]
    START --> UF["Init Union-Find"]
    UF --> EDGE["Process each edge"]
    EDGE --> JOIN{"Same component?"}
    JOIN -->|no| MERGE["Union sets"]
    JOIN -->|yes| SKIP["Skip / record bridge"]
    MERGE --> EDGE
    SKIP --> EDGE
    EDGE --> DONE["Return components / cost"]
```

**Walkthrough (same example):**

```
Example: strs=["eat","tea","tan","ate","nat","bat"] → [["bat"],["nat","tan"],["ate","eat","tea"]]
Approach: Prime Number Product (Mathematical)

Build adjacency from input
Union edges or relax distances
Return components / shortest cost
```
```java
public List<List<String>> groupAnagrams(String[] strs) {
    int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
                    53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
    
    Map<Long, List<String>> map = new HashMap<>();
    
    for (String str : strs) {
        long product = 1;
        for (char c : str.toCharArray()) {
            product *= primes[c - 'a'];
        }
        map.computeIfAbsent(product, k -> new ArrayList<>()).add(str);
    }
    
    return new ArrayList<>(map.values());
}
```

### Caveat

For very long strings, the product can overflow even a `long`. This approach works for strings up to about 20 characters, but is not production-safe for longer strings. Mention this trade-off in an interview.

---

## Approach Comparison

| Approach | Time per String | Total Time | Space | Collision Risk | Best When |
|----------|----------------|-----------|-------|----------------|-----------|
| Sort as Key | O(k log k) | O(n × k log k) | O(n × k) | None | Short strings |
| Count Array Key | O(k) | O(n × k) | O(n × k) | None (with delimiter) | Long strings |
| Prime Product | O(k) | O(n × k) | O(n) | Overflow for long strings | Short strings, space constrained |

---

## Common Mistakes

1. **Missing delimiter in count key**: Without `#` between counts, different frequency arrays produce the same string.
2. **Using `Arrays.toString()` as key**: This works but is slower than building a custom string because `Arrays.toString()` adds brackets and commas.
3. **Not handling empty strings**: `""` is a valid input and should be grouped with other empty strings.
4. **Modifying original array**: Some candidates sort the original string in-place and then cannot return the original. Always work on a copy.
5. **Inefficient key generation**: Using `String.valueOf(count)` does not work — it gives the object reference, not the array contents.

---

## Edge Cases

| Case | Input | Expected Output |
|------|-------|----------------|
| Empty strings | `["", ""]` | `[["", ""]]` |
| Single character | `["a"]` | `[["a"]]` |
| All same string | `["abc", "abc"]` | `[["abc", "abc"]]` |
| No anagrams | `["abc", "def"]` | `[["abc"], ["def"]]` |
| Single element | `["hello"]` | `[["hello"]]` |

---

## Follow-Up Questions

### 1. What if the strings contain Unicode characters, not just lowercase English letters?

Use a `HashMap<Character, Integer>` for frequency counting instead of a fixed-size array. Convert the map to a sorted string representation as the key.

### 2. What if the input is a stream of strings?

Use the same HashMap approach, but process strings one at a time. The HashMap grows incrementally. Consider using a `ConcurrentHashMap` for thread-safe stream processing.

### 3. What if memory is limited?

Use external sorting: sort each string, write to disk with the original string, sort the file by the sorted key, and then group consecutive lines with the same key.

---

## LeetCode Similar Problems

- [242. Valid Anagram](https://leetcode.com/problems/valid-anagram/) — Check if two strings are anagrams
- [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/) — Sliding window + frequency count
- [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/) — Sliding window anagram check
- [1347. Minimum Number of Steps to Make Two Strings Anagram](https://leetcode.com/problems/minimum-number-of-steps-to-make-two-strings-anagram/) — Frequency difference
- [2273. Find Resultant Array After Removing Anagrams](https://leetcode.com/problems/find-resultant-array-after-removing-anagrams/)

---

## Interview Tips

1. **Start with the sorting approach**: It is the most intuitive and easy to code. Show it first, then optimize.
2. **Discuss the time improvement**: "Sorting takes O(k log k) per string. I can improve to O(k) by using character counts."
3. **Know `computeIfAbsent`**: This one-liner `map.computeIfAbsent(key, k -> new ArrayList<>()).add(str)` is cleaner than the traditional `containsKey` check and demonstrates Java 8+ fluency.
4. **Mention the delimiter issue**: If you use the count array approach, proactively explain why the delimiter is needed. This shows attention to correctness.
5. **Be ready for follow-ups**: Unicode handling, streaming input, and memory-constrained scenarios are common follow-up questions.
#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: strs=["eat","tea","tan","ate","nat","bat"]"]
    START --> STEP1["Approach 4: step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: strs=["eat","tea","tan","ate","nat","bat"] → [["bat"],["nat","tan"],["ate","eat","tea"]]
Approach: Approach 4

Apply Approach 4 on the example input step by step
Final answer from example: see above
```


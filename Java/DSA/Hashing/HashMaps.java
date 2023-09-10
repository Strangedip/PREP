package DSA.Hashing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HashMaps {
    public static void main(String[] args) {
        int[] n = { 1, 4, 12, 14, 12, 1, 44, 4 };
        Map<Integer, Integer> a = new HashMap<>();
        for (int i = 0; i < n.length; i++) {
            if (a.containsKey(n[i])) {
                a.put(n[i], a.get(n[i]) + 1);
            } else {
                a.put(n[i], 1);
            }
        }
        ArrayList<Integer> ar = new ArrayList<>();
        Collections.reverse(ar);
    }
}
class Solution {
    public static ArrayList<Integer> addOneToNumber(ArrayList<Integer> arr) {
        int index = arr.size() - 1;
        while (index >= 0 && arr.get(index) == 9) {
            arr.set(index, 0);
            index -= 1;
        }
        if (index < 0) {
            arr.set(0, 1);
            arr.add(arr.size(), 0);
        } else
            arr.set(index, arr.get(index) + 1);
        return arr;
    }

}

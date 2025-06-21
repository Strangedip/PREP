package DSA.Maps;

import java.util.*;

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

        for (Map.Entry<Integer, Integer> mp : a.entrySet()) {
            System.out.println(mp);
        }
    }
}

package Competitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AdmissionInNinjaGram {
    public static void main(String[] args) {
        ArrayList<Integer> ans = new ArrayList<>();
        ans.add(1);
        ans.add(1);
        ans.add(2);
        ans.add(3);
        ans.add(3);
        ans.add(4);
        ans.add(5);
        ans.add(5);
        ans.add(5);
        int k = 2;
        int[] a = { 2, 2, 1, 3, 1, 1, 3, 1, 1 };
        int[] b = { 2, 5, 1, 3 };
        System.out.println((sol(a)));
        // System.out.println(Arrays.toString(rotateArray(a, a.length)));

    }
    public static void sort(int[]a,int low,int high){
        int pivot=a[low];
        sort(a, low, high);
    }

    public static int sol(int[] a) {
        Map<Integer, Integer> mp = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            if (!mp.containsKey(a[i])) {
                mp.put(a[i], 1);
            } else {
                mp.put(a[i], mp.get(a[i]) + 1);
            }
        }
        for (Map.Entry<Integer, Integer> m : mp.entrySet()) {
            if (m.getValue() >= a.length / 2) {
                return m.getKey();
            }
        }
        return -1;
    }
}

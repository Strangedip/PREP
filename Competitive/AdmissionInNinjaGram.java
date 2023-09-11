package Competitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        int k=2;
        int[] a = { 2 ,5, 6, 2 };
        int[] b = { 2 ,5, 1,3 };
        System.out.println((sortedArray(a,b)));
        // System.out.println(Arrays.toString(rotateArray(a, a.length)));

    }

    public static List< Integer > sortedArray(int []a, int []b) {
        // Write your code here
        Set<Integer> s= new TreeSet<>();
        for (int i = 0; i < a.length; i++) {
            s.add(a[i]);
        }
        for (int i = 0; i < b.length; i++) {
            s.add(b[i]);
        }
        return new ArrayList<>(s);
    }
}

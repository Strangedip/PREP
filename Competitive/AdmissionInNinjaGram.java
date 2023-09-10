package Competitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class AdmissionInNinjaGram {
    public static void main(String[] args) {
        int k = 3;
        ArrayList<Integer> ans = new ArrayList<>();
        ans.add(3);
        ans.add(6);
        ans.add(5);
        ans.add(4);
        ans.add(3);
        System.out.println(getMaximumOfSubarrays(ans, k));

    }

    public static ArrayList<Integer> getMaximumOfSubarrays(ArrayList<Integer> arr, int k) {
        ArrayList<Integer> ans = new ArrayList<>();
        for (int i = k; i <= arr.size()-k; i++) {
            int large=0;
            int b=k;
            while(b>0){
                if(arr.get(b)>large)
                {
                    large=arr.get(b);
                }
                b--;
            }
            ans.add(large);
        }
        
        return ans;
    }
}

// **************************
//Bitonic array [ascending-> <-descending]
// Given a mountain array mountainArr, return the minimum index such that mountainArr.get(index) == target. If such an index does not exist, return -1.
// merged ans with another logic to find target in both side of bitonic array
// havent written the ans as it is in question since contained a custom MountainArray class
// can see ans in leetcode problem 1095

import java.util.ArrayList;
import java.util.List;

public class Leetcode1095 {
    public static void main(String[] args) {
        ArrayList<Integer> array = new ArrayList<>();
        array.add(1);
        array.add(2);
        array.add(3);
        array.add(4);
        array.add(5);
        array.add(3);
        array.add(2);
        array.add(1);
        array.add(0);
        System.out.println(array);
        int peak = findPeak(array);
        System.out.println(List.of(ascBinarySearchElement(array, 0, peak, 0), descBinarySearchElement(array, peak, array.size(), 0)));
    }


    public static int findPeak(List<Integer> array) {
        int start = 0;
        int end = array.size();
        int middle;
        while (start <= end) {
            middle = start + (end - start) / 2;
            if (array.get(middle) > array.get(middle + 1)) {
                end = middle - 1;
            } else if (array.get(middle) <= array.get(middle + 1)) {
                start = middle + 1;
            }
        }
        return start;
    }

    public static int ascBinarySearchElement(List<Integer> array, int start, int end, int target) {
        int middle;
        while (start <= end) {
            middle = start + (end - start) / 2;
            if (array.get(middle) == target) {
                return middle;
            }
            if (array.get(middle) > target) {
                end = middle - 1;
            } else if (array.get(middle) <= target) {
                start = middle + 1;
            }
        }
        return -1;
    }

    public static int descBinarySearchElement(List<Integer> array, int start, int end, int target) {
        int middle;
        while (start <= end) {
            middle = start + (end - start) / 2;
            if (array.get(middle) == target) {
                return middle;
            }
            if (array.get(middle) < target) {
                end = middle - 1;
            } else if (array.get(middle) >= target) {
                start = middle + 1;
            }
        }
        return -1;
    }
}

// use binary search to find peak. then find target first in ascending array then in descending, by changing binary search pattern as order changes

import java.util.Arrays;

// sum subarray
public class SlidingWindow {
    public static void main(String[] args) {
        int[] a = { 2, 2, 3, 4, 5, 6, 7 };
        int k = 4;
        System.out.println(Arrays.toString(findIndexOfSubarray(a, k)));
        System.out.println(Arrays.toString(findMinIndex(a, k)));
        System.out.println(findMinLength(a, k));
    }

    public static int[] findIndexOfSubarray(int a[], int k) {
        int[] ans = { -1, -1 };
        int start = 0; // start if the window
        int end = 0; // end of the window
        int currSum = 0;

        while (end < a.length) {
            currSum += a[end]; // add current end element
            end++; // increase window end

            // if sum is getting greater than k substract start element and contract window
            // start++
            while (start < end && currSum > k) {
                currSum -= a[start];
                start++;
            }
            // if element is found return index
            if (currSum == k) {
                return new int[] { start, end - 1 };
            }
        }
        return ans;
    }

    public static int findMinLength(int a[], int k) {
        int minLen = Integer.MAX_VALUE;
        int start = 0; // start if the window
        int end = 0; // end of the window
        int currSum = 0;

        while (end < a.length) {
            currSum += a[end]; // add current end element
            end++; // increase window end

            // if sum is getting greater than k substract start element and contract window
            // start++
            while (start < end && currSum > k) {
                currSum -= a[start];
                start++;
            }
            // if element is found and length is minimum
            if (currSum == k && (end - 1 - start < minLen)) {
                minLen = end - 1 - start;
            }
        }
        return minLen;
    }

    public static int[] findMinIndex(int a[], int k) {
        int minLen = Integer.MAX_VALUE;
        int[] ind = { -1, -1 };
        int start = 0; // start if the window
        int end = 0; // end of the window
        int currSum = 0;

        while (end < a.length) {
            currSum += a[end]; // add current end element
            end++; // increase window end

            // if sum is getting greater than k substract start element and contract window
            // start++
            while (start < end && currSum > k) {
                currSum -= a[start];
                start++;
            }
            // if element is found return index
            if (currSum == k && (end - 1 - start < minLen)) {
                minLen = end - 1 - start;
                ind = new int[] { start, end - 1 };
            }
        }
        return ind;
    }
}

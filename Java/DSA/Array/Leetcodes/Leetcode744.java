// Find ceiling letter in char letters i.e smallest greater char or equal to target

public class Leetcode744 {
    public static void main(String[] args) {
        char[] letters = { 'a', 'c' };
        char target = 'e';
        System.out.println(ceilingChar(letters, target));
    }

    public static char ceilingChar(char[] letters, char target) {
        if(letters.length==0) return '-';
        
        int start = 0;
        int end = letters.length - 1;
        int middle;
        // int ans=target;
        while (start<=end) {
            middle = (start + end) / 2;
            if (letters[middle]>target){
                end=middle-1;
            }
            else{
                start=middle+1;
            }
        }
        // edge case if greater or equal to target element is not present return first char.
        if (start > letters.length - 1) {
            return letters[0];
        }
        return letters[start];
    }
}

// Use binary search to find elements which (middle) is greater than the target. if not make middle = start. to find at larger side.
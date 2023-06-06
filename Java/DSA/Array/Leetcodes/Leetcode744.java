// Find ceiling letter in char letters i.e smallest greater char or equal to target
public class Leetcode744 {
    public static void main(String[] args) {
        char[] letters = { 'a', 'e', 'f', 'g', 'g' };
        char target = 'e';
        System.out.println(ceilingChar(letters, target));
    }

    public static char ceilingChar(char[] letters, char target) {
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
        // edge case if greater or equal to tarhet elemnt is not present
        if (start > letters.length - 1) {
            return letters[0];
        }
        return letters[start];

        // another type to return without applying if to check if variable is out of array range
        // return letters[start % letters.length];  e.g 3%4=3 , 5%4=1
    }
}

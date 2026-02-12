/**
 * 231. Power of Two
 * 
 * Return true if n is a power of two.
 * Approach: n & (n-1) == 0 for powers of two (exactly one bit set).
 * Time: O(1), Space: O(1)
 */
public class PowerOfTwo {

    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    public static void main(String[] args) {
        PowerOfTwo solution = new PowerOfTwo();

        System.out.println(solution.isPowerOfTwo(1));   // true (2^0)
        System.out.println(solution.isPowerOfTwo(16));  // true (2^4)
        System.out.println(solution.isPowerOfTwo(3));   // false
        System.out.println(solution.isPowerOfTwo(0));   // false
        System.out.println(solution.isPowerOfTwo(-8));  // false
    }
}


import java.util.*;

/**
 * Number Theory utilities — GCD, primes, modular exponentiation.
 */
public class NumberTheory {

    /** Euclidean GCD — O(log(min(a,b))) */
    public static int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    /** Extended GCD — returns [g, x, y] where ax + by = g */
    public static long[] extGcd(long a, long b) {
        if (b == 0) return new long[]{a, 1, 0};
        long[] res = extGcd(b, a % b);
        return new long[]{res[0], res[2], res[1] - (a / b) * res[2]};
    }

    /** Sieve of Eratosthenes — all primes up to n */
    public static List<Integer> sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= n; j += i) isPrime[j] = false;
            }
        }
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) if (isPrime[i]) primes.add(i);
        return primes;
    }

    /** Modular exponentiation — (base^exp) % mod */
    public static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    /** nCr mod p using Fermat's little theorem (p prime) */
    public static long nCrModP(int n, int r, int p) {
        if (r > n) return 0;
        long[] fact = new long[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) fact[i] = (fact[i - 1] * i) % p;
        return fact[n] * modPow(fact[r], p - 2, p) % p * modPow(fact[n - r], p - 2, p) % p;
    }

    public static void main(String[] args) {
        System.out.println("GCD(48, 18): " + gcd(48, 18));
        System.out.println("Primes <= 30: " + sieve(30));
        System.out.println("2^10 mod 1000: " + modPow(2, 10, 1000));
        System.out.println("C(10,3) mod 97: " + nCrModP(10, 3, 97));
    }
}

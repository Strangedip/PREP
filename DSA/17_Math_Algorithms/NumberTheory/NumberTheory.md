# Mathematical Algorithms & Number Theory

## Problem Statement
Mathematical algorithms are essential for solving computational problems involving numbers, primes, divisibility, and modular arithmetic. These are increasingly important in advanced interviews and competitive programming.

**Key Topics:**
- **GCD & LCM Algorithms**
- **Prime Number Algorithms**
- **Modular Arithmetic**
- **Fast Exponentiation**
- **Combinatorics**

## 1. GCD & LCM Algorithms

### Euclidean Algorithm for GCD:
```java
public int gcd(int a, int b) {
    if (b == 0) {
        return a;
    }
    return gcd(b, a % b);
}

// Iterative version
public int gcdIterative(int a, int b) {
    while (b != 0) {
        int temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

// Extended Euclidean Algorithm
// Finds x, y such that ax + by = gcd(a, b)
public class ExtendedGCD {
    int x, y;
    
    public int extendedGCD(int a, int b) {
        if (b == 0) {
            x = 1;
            y = 0;
            return a;
        }
        
        int gcd = extendedGCD(b, a % b);
        int temp = x;
        x = y;
        y = temp - (a / b) * y;
        
        return gcd;
    }
}
```

### LCM Calculation:
```java
public long lcm(int a, int b) {
    return (long) a * b / gcd(a, b);
}

// For multiple numbers
public long lcmArray(int[] nums) {
    long result = nums[0];
    for (int i = 1; i < nums.length; i++) {
        result = lcm((int) result, nums[i]);
    }
    return result;
}
```

## 2. Prime Number Algorithms

### Primality Testing:
```java
// Basic primality test
public boolean isPrime(int n) {
    if (n <= 1) return false;
    if (n <= 3) return true;
    if (n % 2 == 0 || n % 3 == 0) return false;
    
    for (int i = 5; i * i <= n; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) {
            return false;
        }
    }
    return true;
}

// Miller-Rabin Primality Test (Probabilistic)
public boolean millerRabin(long n, int k) {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0) return false;
    
    // Write n-1 as d * 2^r
    long d = n - 1;
    int r = 0;
    while (d % 2 == 0) {
        d /= 2;
        r++;
    }
    
    Random random = new Random();
    for (int i = 0; i < k; i++) {
        long a = 2 + random.nextLong() % (n - 3);
        long x = modPow(a, d, n);
        
        if (x == 1 || x == n - 1) continue;
        
        for (int j = 0; j < r - 1; j++) {
            x = modMul(x, x, n);
            if (x == n - 1) break;
        }
        
        if (x != n - 1) return false;
    }
    return true;
}
```

### Sieve of Eratosthenes:
```java
// Generate all primes up to n
public List<Integer> sieveOfEratosthenes(int n) {
    boolean[] isPrime = new boolean[n + 1];
    Arrays.fill(isPrime, true);
    isPrime[0] = isPrime[1] = false;
    
    for (int i = 2; i * i <= n; i++) {
        if (isPrime[i]) {
            for (int j = i * i; j <= n; j += i) {
                isPrime[j] = false;
            }
        }
    }
    
    List<Integer> primes = new ArrayList<>();
    for (int i = 2; i <= n; i++) {
        if (isPrime[i]) {
            primes.add(i);
        }
    }
    return primes;
}

// Segmented Sieve for large ranges
public List<Integer> segmentedSieve(int low, int high) {
    int limit = (int) Math.sqrt(high) + 1;
    List<Integer> primes = sieveOfEratosthenes(limit);
    
    boolean[] isPrime = new boolean[high - low + 1];
    Arrays.fill(isPrime, true);
    
    for (int prime : primes) {
        int start = Math.max(prime * prime, (low + prime - 1) / prime * prime);
        
        for (int j = start; j <= high; j += prime) {
            isPrime[j - low] = false;
        }
    }
    
    List<Integer> result = new ArrayList<>();
    for (int i = Math.max(2, low); i <= high; i++) {
        if (isPrime[i - low]) {
            result.add(i);
        }
    }
    return result;
}
```

### Prime Factorization:
```java
public Map<Integer, Integer> primeFactorization(int n) {
    Map<Integer, Integer> factors = new HashMap<>();
    
    // Handle factor of 2
    while (n % 2 == 0) {
        factors.put(2, factors.getOrDefault(2, 0) + 1);
        n /= 2;
    }
    
    // Handle odd factors
    for (int i = 3; i * i <= n; i += 2) {
        while (n % i == 0) {
            factors.put(i, factors.getOrDefault(i, 0) + 1);
            n /= i;
        }
    }
    
    // If n is still > 1, it's a prime
    if (n > 1) {
        factors.put(n, 1);
    }
    
    return factors;
}

// Count divisors using prime factorization
public int countDivisors(int n) {
    Map<Integer, Integer> factors = primeFactorization(n);
    int count = 1;
    
    for (int exponent : factors.values()) {
        count *= (exponent + 1);
    }
    
    return count;
}
```

## 3. Modular Arithmetic

### Fast Exponentiation:
```java
public long modPow(long base, long exp, long mod) {
    long result = 1;
    base %= mod;
    
    while (exp > 0) {
        if (exp % 2 == 1) {
            result = (result * base) % mod;
        }
        base = (base * base) % mod;
        exp /= 2;
    }
    
    return result;
}

// Modular multiplication (prevents overflow)
public long modMul(long a, long b, long mod) {
    return ((a % mod) * (b % mod)) % mod;
}

// Modular inverse using Extended Euclidean
public long modInverse(long a, long mod) {
    ExtendedGCD egcd = new ExtendedGCD();
    long gcd = egcd.extendedGCD((int) a, (int) mod);
    
    if (gcd != 1) {
        return -1; // Inverse doesn't exist
    }
    
    return (egcd.x % mod + mod) % mod;
}

// Modular inverse using Fermat's Little Theorem (when mod is prime)
public long modInverseFermat(long a, long p) {
    return modPow(a, p - 2, p);
}
```

### Chinese Remainder Theorem:
```java
public class ChineseRemainderTheorem {
    public long solve(int[] remainders, int[] moduli) {
        long product = 1;
        for (int mod : moduli) {
            product *= mod;
        }
        
        long result = 0;
        for (int i = 0; i < remainders.length; i++) {
            long partialProduct = product / moduli[i];
            long inverse = modInverse(partialProduct, moduli[i]);
            result += remainders[i] * partialProduct * inverse;
        }
        
        return result % product;
    }
}
```

## 4. Combinatorics

### Factorial and Combinations:
```java
public class Combinatorics {
    private static final int MOD = 1000000007;
    private long[] factorial;
    private long[] invFactorial;
    
    public Combinatorics(int maxN) {
        factorial = new long[maxN + 1];
        invFactorial = new long[maxN + 1];
        
        factorial[0] = 1;
        for (int i = 1; i <= maxN; i++) {
            factorial[i] = (factorial[i - 1] * i) % MOD;
        }
        
        invFactorial[maxN] = modInverse(factorial[maxN], MOD);
        for (int i = maxN - 1; i >= 0; i--) {
            invFactorial[i] = (invFactorial[i + 1] * (i + 1)) % MOD;
        }
    }
    
    public long nCr(int n, int r) {
        if (r > n || r < 0) return 0;
        
        return (factorial[n] * invFactorial[r] % MOD) * invFactorial[n - r] % MOD;
    }
    
    public long nPr(int n, int r) {
        if (r > n || r < 0) return 0;
        
        return factorial[n] * invFactorial[n - r] % MOD;
    }
}

// Pascal's Triangle for small combinations
public int[][] pascalTriangle(int n) {
    int[][] dp = new int[n + 1][n + 1];
    
    for (int i = 0; i <= n; i++) {
        dp[i][0] = dp[i][i] = 1;
        for (int j = 1; j < i; j++) {
            dp[i][j] = dp[i - 1][j - 1] + dp[i - 1][j];
        }
    }
    
    return dp;
}
```

### Catalan Numbers:
```java
public long catalanNumber(int n) {
    if (n <= 1) return 1;
    
    long[] catalan = new long[n + 1];
    catalan[0] = catalan[1] = 1;
    
    for (int i = 2; i <= n; i++) {
        for (int j = 0; j < i; j++) {
            catalan[i] += catalan[j] * catalan[i - 1 - j];
        }
    }
    
    return catalan[n];
}

// Using combination formula: C_n = (2n)! / ((n+1)! * n!)
public long catalanUsingCombination(int n) {
    Combinatorics comb = new Combinatorics(2 * n);
    return comb.nCr(2 * n, n) * modInverse(n + 1, MOD) % MOD;
}
```

## 5. Advanced Number Theory Problems

### Euler's Totient Function:
```java
public int eulerTotient(int n) {
    int result = n;
    
    for (int i = 2; i * i <= n; i++) {
        if (n % i == 0) {
            // Remove all factors of i
            while (n % i == 0) {
                n /= i;
            }
            // Multiply by (1 - 1/i)
            result -= result / i;
        }
    }
    
    // If n is still > 1, it's a prime factor
    if (n > 1) {
        result -= result / n;
    }
    
    return result;
}

// Sieve-based approach for multiple values
public int[] eulerTotientSieve(int n) {
    int[] phi = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        phi[i] = i;
    }
    
    for (int i = 2; i <= n; i++) {
        if (phi[i] == i) { // i is prime
            for (int j = i; j <= n; j += i) {
                phi[j] -= phi[j] / i;
            }
        }
    }
    
    return phi;
}
```

### Perfect Numbers and Divisor Sums:
```java
public boolean isPerfectNumber(int n) {
    if (n <= 1) return false;
    
    int sum = 1; // 1 is always a divisor
    for (int i = 2; i * i <= n; i++) {
        if (n % i == 0) {
            sum += i;
            if (i != n / i) {
                sum += n / i;
            }
        }
    }
    
    return sum == n;
}

public int sumOfDivisors(int n) {
    int sum = 0;
    for (int i = 1; i * i <= n; i++) {
        if (n % i == 0) {
            sum += i;
            if (i != n / i) {
                sum += n / i;
            }
        }
    }
    return sum;
}
```

## 6. Practical Applications

### RSA Key Generation (Simplified):
```java
public class SimpleRSA {
    public static class KeyPair {
        long publicKey, privateKey, n;
        
        KeyPair(long pub, long priv, long modulus) {
            publicKey = pub;
            privateKey = priv;
            n = modulus;
        }
    }
    
    public KeyPair generateKeys(int p, int q) {
        long n = (long) p * q;
        long phi = (long) (p - 1) * (q - 1);
        
        // Choose e (commonly 65537)
        long e = 65537;
        
        // Calculate d = e^(-1) mod phi
        long d = modInverse(e, phi);
        
        return new KeyPair(e, d, n);
    }
    
    public long encrypt(long message, long e, long n) {
        return modPow(message, e, n);
    }
    
    public long decrypt(long ciphertext, long d, long n) {
        return modPow(ciphertext, d, n);
    }
}
```

## LeetCode Similar Problems:
- [204. Count Primes](https://leetcode.com/problems/count-primes/)
- [372. Super Pow](https://leetcode.com/problems/super-pow/)
- [509. Fibonacci Number](https://leetcode.com/problems/fibonacci-number/)
- [62. Unique Paths](https://leetcode.com/problems/unique-paths/) (Combinatorics)
- [1015. Smallest Integer Divisible by K](https://leetcode.com/problems/smallest-integer-divisible-by-k/)

## Interview Tips:
- **Master modular arithmetic** - it prevents overflow and is used everywhere
- **Know prime algorithms** - sieve for preprocessing, Miller-Rabin for large numbers
- **Understand GCD/LCM** - fundamental for many problems
- **Practice combinatorics** - comes up in probability and counting problems
- **These are becoming more common** in system design and advanced coding rounds 
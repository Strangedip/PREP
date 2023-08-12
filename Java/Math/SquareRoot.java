package Math;

public class SquareRoot {
    public static void main(String[] args) {
        System.out.println(sqrrt(38.44));
    }

    public static double sqrrt(double n)
    {
        double i = 2;
        for (; i*i <= n; i++) {
            if(i*i==n)
            {
                return i;
            }
        }
        return decimalSqrt(i-1,n);
    }
    public static double decimalSqrt(double i,double n){
        double inc=0.1;
        // here 5 is accuracy 
        for (int j = 0; j <= 4; j++) {
            // check if current i value square is equal ot the number
            while(i*i<=n){
                // if not increase value of i with 0.1 everytime
                if(i*i==n)
                {
                    return i;
                }
                i+=inc;
            }
            // if i*i<=0 is false means the loop broke at i+inc value soe subtract the increased value
            i=i-inc;

            // update the new increament value (divide by 10) => 0.01 => 0.001 till the value of i equal to n
            inc=inc/10;
        }
        return i;
    }
}

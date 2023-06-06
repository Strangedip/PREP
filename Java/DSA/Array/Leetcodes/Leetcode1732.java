// create new net array n Get highest net value from an array starting at 0 
// [-4,-3,-2,-1,4,3,2] 
// new array -> [0, -4, -7, -9, -10, -6, -3, -1]

public class Leetcode1732 {
    public static void main(String[] args) {
        int[] array={-4,-3,-2,-1,4,3,2};
        // int[] array={28,0,-8,-99,11,62,-35,68,2,12,-71,13,66,-28};
        System.out.println(largestAltitude(array));
    }
    
    public static int largestAltitude(int[] gain) {
        int[] array=new int[gain.length+1];
        int ans=0;
        int sum=0;
        array[0]=0;
        int i=1;
        while(i<array.length){
            sum=sum+gain[i-1];
            array[i]= sum;
            if (sum>ans){
                ans=sum;
            }
            i++;
        }
        return ans;
    }
    // public static int largest(int[] gain) {
    //     int highest = 0;
    //     for (int i=0;i<gain.length;i++){
    //         if(gain[i]>highest){
    //             highest=gain[i];
    //         }
    //     }
    //     return highest;
    // }
}

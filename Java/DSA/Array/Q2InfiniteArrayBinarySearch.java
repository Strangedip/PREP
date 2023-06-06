
public class Q2InfiniteArrayBinarySearch {
    public static void main(String[] args) {
        int[] array={1,2,3,4,5,7,8,9,12,13,14};
        int target=8;
        System.out.println(ans(array,target));
    }

    public static int ans(int[]array,int target)
    {
        //seraching in chunks starting from chunk size 2 i.e end=1
        int start=0;
        int end=1;

        // searching possible chunk break if target is less tha end
        while(array[end]<target){          
            int tempStart=end+1;

            //doubling chunk size by multiplying previous chunk size by 2 and appending in from start of previous end point
            end=end+(end-start+1)*2;
            start=tempStart;

            //edge case if elemne not found
            // if(end>array.length){
            //     return -1;
            // }
        }

        //normal binary search
        int middle;
        while(start<=end){
            if(end>=array.length){
                return -1;
            }
            middle=(start+end)/2;
            if(array[middle]==target){
                return middle;
            }
            else if(array[middle]<target){
                start=middle+1;
            }
            else{
                end=middle-1;
            }
        }
        // if not found in array 

        return -1;


    }
}
import java.util.*;

/**
 * Task Scheduler — minimum time to complete all tasks with cooldown n between same task.
 * LeetCode 621
 */
public class TaskScheduler {

    /** Greedy with max heap — O(n) time */
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char t : tasks) freq[t - 'A']++;

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        for (int f : freq) {
            if (f > 0) maxHeap.offer(f);
        }

        int time = 0;
        while (!maxHeap.isEmpty()) {
            List<Integer> temp = new ArrayList<>();
            for (int i = 0; i <= n; i++) {
                if (!maxHeap.isEmpty()) {
                    int count = maxHeap.poll() - 1;
                    if (count > 0) temp.add(count);
                }
                if (maxHeap.isEmpty() && temp.isEmpty()) break;
                time++;
            }
            for (int c : temp) maxHeap.offer(c);
        }
        return time;
    }

    /** Math formula approach */
    public int leastIntervalFormula(char[] tasks, int n) {
        int[] freq = new int[26];
        int maxFreq = 0, maxCount = 0;
        for (char t : tasks) {
            freq[t - 'A']++;
            if (freq[t - 'A'] > maxFreq) {
                maxFreq = freq[t - 'A'];
                maxCount = 1;
            } else if (freq[t - 'A'] == maxFreq) {
                maxCount++;
            }
        }
        int partCount = maxFreq - 1;
        int partLength = n + 1;
        int idleSlots = partCount * partLength + maxCount;
        return Math.max(tasks.length, idleSlots);
    }

    public static void main(String[] args) {
        TaskScheduler sol = new TaskScheduler();
        System.out.println(sol.leastInterval(new char[]{'A','A','A','B','B','B'}, 2)); // 8
        System.out.println(sol.leastInterval(new char[]{'A','A','A','B','B','B'}, 0)); // 6
    }
}

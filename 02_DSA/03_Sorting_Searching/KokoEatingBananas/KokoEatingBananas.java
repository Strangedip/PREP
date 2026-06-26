/**
 * Koko Eating Bananas — LeetCode 875
 * Binary search on answer (eating speed).
 */
public class KokoEatingBananas {

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1, right = 0;
        for (int pile : piles) right = Math.max(right, pile);

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canFinish(piles, mid, h)) right = mid;
            else left = mid + 1;
        }
        return left;
    }

    private boolean canFinish(int[] piles, int speed, int h) {
        long hours = 0;
        for (int pile : piles) {
            hours += (pile + speed - 1) / speed;
            if (hours > h) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        KokoEatingBananas sol = new KokoEatingBananas();
        System.out.println(sol.minEatingSpeed(new int[]{3, 6, 7, 11}, 8)); // 4
        System.out.println(sol.minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5)); // 30
    }
}

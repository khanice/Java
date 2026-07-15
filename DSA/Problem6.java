import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * PROBLEM 6: Prefix Sums -- two parts. Do PART A first, it feeds PART B.
 *
 * =========================================================================
 * PART A -- Range Sum Query (warm-up: just the formula)
 * =========================================================================
 *
 * Implement the RangeSum class below. Its constructor gets an array. Then
 * sumRange(i, j) must return the sum of nums[i..j] INCLUSIVE, in O(1) time.
 *
 *   nums = [3, 1, 4, 1, 5, 9]
 *   sumRange(1, 3) -> 1 + 4 + 1     = 6
 *   sumRange(0, 5) -> whole array   = 23
 *   sumRange(2, 2) -> just nums[2]  = 4
 *
 * The constructor may do O(n) work. Every sumRange call after that must be
 * O(1) -- NO loop inside sumRange. That is the entire point: pay once, then
 * answer any range instantly.
 *
 * BUILD the prefix array of size n+1, with prefix[0] = 0:
 *     prefix[i + 1] = prefix[i] + nums[i]
 * THEN:
 *     sum(i..j) = prefix[j + 1] - prefix[i]
 *
 * Keep the leading 0. It is what makes i == 0 work with no special case.
 *
 * =========================================================================
 * PART B -- Subarray Sum Equals K (the real one)
 * =========================================================================
 *
 * Given an array `nums` (NEGATIVES ARE ALLOWED) and an integer k, return the
 * TOTAL NUMBER of contiguous subarrays whose sum equals exactly k.
 *
 * Example 1:
 *   nums = [1, 1, 1], k = 2      -> 2
 *   (the [1,1] at indices 0-1, and the [1,1] at indices 1-2 -- they OVERLAP,
 *    and both count. You are counting subarrays, not partitioning the array.)
 *
 * Example 2:
 *   nums = [1, 2, 3], k = 3      -> 2      ([1,2] and [3])
 *
 * Example 3:
 *   nums = [1, -1, 0], k = 0     -> 3      ([1,-1], [0], and [1,-1,0])
 *
 * Constraints:
 *   1 <= nums.length <= 20000
 *   -1000 <= nums[i] <= 1000     <-- NEGATIVES. Sliding window is INVALID here.
 *   -10^7 <= k <= 10^7
 *
 * WHY NOT A SLIDING WINDOW: with negatives, growing the window can DECREASE
 * the sum. The monotonicity that licensed Problem 5 is gone, so shrinking no
 * longer means what you need it to mean. Reach for prefix sums instead.
 *
 * THE DERIVATION (do this on paper, do not memorise the code):
 *     you want    sum(i..j) == k
 *     which is    prefix[j+1] - prefix[i] == k
 *     rearrange   prefix[i] == prefix[j+1] - k
 *
 * So: standing at j, HOW MANY earlier prefix values equal (currentSum - k)?
 * Each one is a subarray ending at j that sums to k. "How many times have I
 * seen this value?" is a HashMap<Integer,Integer> question -- value -> count.
 *
 * You do NOT need to build a prefix ARRAY here. Keep a running `sum` and a map.
 *
 * TWO DETAILS THAT ARE BOTH BUGS IF YOU GET THEM WRONG:
 *   1. Seed the map with  map.put(0, 1)  before the loop. That is the empty
 *      prefix, and it is what lets a subarray STARTING AT INDEX 0 be counted.
 *      Without it, [1,2,3] with k=3 returns 1 instead of 2.
 *   2. LOOK UP (sum - k) BEFORE you insert the current sum -- same ordering
 *      discipline as your Two Sum in Problem 1.
 *
 * Useful: map.getOrDefault(key, 0)   and   map.merge(key, 1, Integer::sum)
 *         (merge inserts 1 if absent, else adds 1 to the existing count)
 *
 * COUNT, do not return early -- there can be many answers, and they overlap.
 *
 * TIME/SPACE: O(n) time, O(n) space.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem6.java
 */
public class Problem6 {

    // ---------------- PART A ----------------
    static class RangeSum {
        // TODO: declare your prefix array here
        private final int[] prefix;

        RangeSum(int[] nums) {
            // TODO: build the prefix array (O(n))
            this.prefix = new int[nums.length +1];

            for(int k=0;k<nums.length;k++){
                prefix[k+1]= prefix[k] + nums[k];
            }

        }

        int sumRange(int i, int j) {
            // TODO: O(1) -- no loops allowed
            return prefix[j+1] - prefix[i];
        }
    }

    // ---------------- PART B ----------------
    public static int subarraySum(int[] nums, int k) {
        // TODO: your code here
        return -1;
    }

    // ---------------------------------------------------------------
    // Test harness -- brute force cross-check plus random arrays.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("--- PART A: Range Sum Query ---");
        int[] a = { 3, 1, 4, 1, 5, 9 };
        RangeSum rs = new RangeSum(a);
        checkA(rs, a, 1, 3);
        checkA(rs, a, 0, 5);
        checkA(rs, a, 2, 2);
        checkA(rs, a, 0, 0);
        checkA(rs, a, 5, 5);

        System.out.println("\n--- PART B: Subarray Sum Equals K ---");
        checkB(new int[] { 1, 1, 1 }, 2);            // 2
        checkB(new int[] { 1, 2, 3 }, 3);            // 2
        checkB(new int[] { 1, -1, 0 }, 0);           // 3
        checkB(new int[] { 1 }, 0);                  // 0
        checkB(new int[] { -1, -1, 1 }, 0);          // 1
        checkB(new int[] { 3, 4, 7, 2, -3, 1, 4, 2 }, 7);  // 4
        checkB(new int[] { 0, 0, 0 }, 0);            // 6  -- zeros are brutal

        java.util.Random rnd = new java.util.Random(3);
        int failures = 0;
        for (int t = 0; t < 300; t++) {
            int[] arr = new int[1 + rnd.nextInt(10)];
            for (int i = 0; i < arr.length; i++) arr[i] = rnd.nextInt(9) - 4;  // -4..4
            int k = rnd.nextInt(9) - 4;
            if (subarraySum(arr.clone(), k) != bruteForceB(arr, k)) {
                if (failures++ == 0) {
                    System.out.printf("%nRANDOM FAIL on %s k=%d: got %d, expected %d%n",
                            Arrays.toString(arr), k,
                            subarraySum(arr.clone(), k), bruteForceB(arr, k));
                }
            }
        }
        System.out.printf("%nrandom tests: %d/300 passed%n", 300 - failures, 300);
    }

    private static int bruteForceB(int[] nums, int k) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum == k) count++;
            }
        }
        return count;
    }

    private static void checkA(RangeSum rs, int[] nums, int i, int j) {
        int expected = 0;
        for (int x = i; x <= j; x++) expected += nums[x];
        int got = rs.sumRange(i, j);
        System.out.printf("sumRange(%d, %d) -> %-4d (expected %-4d) %s%n",
                i, j, got, expected, got == expected ? "PASS" : "FAIL");
    }

    private static void checkB(int[] nums, int k) {
        int expected = bruteForceB(nums, k);
        int got = subarraySum(nums.clone(), k);
        System.out.printf("%-28s k=%-4d -> %-4d (expected %-4d) %s%n",
                Arrays.toString(nums), k, got, expected,
                got == expected ? "PASS" : "FAIL");
    }
}

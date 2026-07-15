import java.util.Arrays;

/*
 * PROBLEM 5: Minimum Size Subarray Sum
 *
 * Given an array of POSITIVE integers `nums` and a positive integer `target`,
 * return the MINIMAL LENGTH of a contiguous subarray whose sum is >= target.
 * If no such subarray exists, return 0.
 *
 * Example 1:
 *   nums = [2, 3, 1, 2, 4, 3], target = 7
 *   Output: 2        the subarray [4, 3] sums to 7, and no length-1 subarray
 *                    reaches 7, so 2 is minimal
 * Example 2:
 *   nums = [1, 4, 4], target = 4
 *   Output: 1        the single element [4]
 * Example 3:
 *   nums = [1, 1, 1, 1, 1, 1, 1, 1], target = 11
 *   Output: 0        the whole array only sums to 8 -- impossible
 *
 * Constraints:
 *   1 <= nums.length <= 100000
 *   1 <= nums[i] <= 10000        <-- ALL STRICTLY POSITIVE. This matters.
 *   1 <= target <= 10^9
 *
 * ---------------------------------------------------------------------------
 * NO SKELETON THIS TIME. Derive it.
 *
 * It is a sliding window, and you already know the machinery: grow on the
 * right, shrink on the left, left never moves backward, so it is O(n).
 *
 * But do NOT copy Problem 4's shape, because the LOGIC IS INVERTED and if you
 * paste the old structure you will get it wrong. Think it through:
 *
 *   In Problem 4 you MAXIMIZED, and the window was INVALID when it had a
 *   duplicate. So you shrank while the window was BAD, and measured when it
 *   was GOOD.
 *
 *   Here you MINIMIZE, and the window is a valid ANSWER once its sum >= target.
 *   Growing only ever makes the sum bigger -- so once you have a valid window,
 *   a LONGER one is no better. You want the SHORTEST one.
 *
 *   So ask yourself: while the window is GOOD (sum >= target), what should you
 *   do -- and when should you record the length?
 *
 * Questions to answer before you code:
 *   1. Do you shrink while the window is valid, or while it is invalid?
 *   2. Where does the "record a candidate answer" line go -- inside the shrink
 *      loop, or after it?
 *   3. You are taking a MINIMUM. What do you initialise your answer to, and how
 *      do you return 0 when nothing was ever found?
 *      (Hint: Integer.MAX_VALUE, then translate it at the end.)
 *   4. Track the window's sum in a running `int sum`. Do NOT re-add the window
 *      from scratch each step -- that would put you back at O(n^2). Add on the
 *      way in, subtract on the way out.
 *
 * WHY "ALL POSITIVE" MATTERS:
 *   Because every element is > 0, growing the window strictly INCREASES the sum
 *   and shrinking strictly DECREASES it. That monotonicity is the whole licence
 *   for the two pointers -- it is what lets you shrink and know the sum only
 *   goes down. If negatives were allowed, sliding window would be INVALID here
 *   and you would need a different technique (prefix sums + a deque). Know why
 *   your tool works, not just that it works.
 *
 * TIME/SPACE: O(n) time, O(1) space.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem5.java
 */
public class Problem5 {

    public static int minSubArrayLen(int target, int[] nums) {
        int left=0;
        int minWindow = Integer.MAX_VALUE;
        int sum=0;
        for(int right=0;right<nums.length;right++){
            sum += nums[right];
            while(sum>=target){
                minWindow = Math.min(right-left+1,minWindow);
                sum -= nums[left];
                left++;
            }
        }
        return minWindow == Integer.MAX_VALUE ? 0 : minWindow;


    }

    // ---------------------------------------------------------------
    // Test harness -- brute force cross-check plus random arrays.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check(7, new int[] { 2, 3, 1, 2, 4, 3 });        // 2
        check(4, new int[] { 1, 4, 4 });                 // 1
        check(11, new int[] { 1, 1, 1, 1, 1, 1, 1, 1 }); // 0  -- impossible
        check(11, new int[] { 1, 2, 3, 4, 5 });          // 3
        check(15, new int[] { 1, 2, 3, 4, 5 });          // 5  -- whole array
        check(6, new int[] { 10, 2, 3 });                // 1  -- answer at index 0
        check(213, new int[] { 12, 28, 83, 4, 25, 26, 25, 2, 25, 25, 25, 25 }); // 8

        java.util.Random rnd = new java.util.Random(11);
        int failures = 0;
        for (int t = 0; t < 300; t++) {
            int[] a = new int[1 + rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = 1 + rnd.nextInt(10);
            int tgt = 1 + rnd.nextInt(40);
            if (minSubArrayLen(tgt, a.clone()) != bruteForce(tgt, a)) {
                if (failures++ == 0) {
                    System.out.printf("%nRANDOM FAIL on %s target=%d: got %d, expected %d%n",
                            Arrays.toString(a), tgt,
                            minSubArrayLen(tgt, a.clone()), bruteForce(tgt, a));
                }
            }
        }
        System.out.printf("%nrandom tests: %d/300 passed%n", 300 - failures, 300);
    }

    private static int bruteForce(int target, int[] nums) {
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum >= target) {
                    best = Math.min(best, j - i + 1);
                    break;
                }
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    private static void check(int target, int[] nums) {
        int expected = bruteForce(target, nums);
        int got = minSubArrayLen(target, nums.clone());
        System.out.printf("target=%-5d %-40s -> %-4d (expected %-4d) %s%n",
                target, Arrays.toString(nums), got, expected,
                got == expected ? "PASS" : "FAIL");
    }
}

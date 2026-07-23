import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/*
 * PROBLEM 14: Sliding Window Maximum
 *
 * Given an array nums and a window size k, the window starts at the left edge
 * and slides one position at a time to the right. Return an array containing
 * the MAXIMUM of each window position.
 *
 *   nums = [1,3,-1,-3,5,3,6,7], k = 3   ->   [3,3,5,5,6,7]
 *
 *     [1  3  -1] -3  5  3  6  7    max 3
 *      1 [3  -1  -3] 5  3  6  7    max 3
 *      1  3 [-1  -3  5] 3  6  7    max 5
 *      1  3  -1 [-3  5  3] 6  7    max 5
 *      1  3  -1  -3 [5  3  6] 7    max 6
 *      1  3  -1  -3  5 [3  6  7]   max 7
 *
 *   nums = [1], k = 1        ->  [1]
 *   nums = [9,8,7,6], k = 2  ->  [9,8,7]
 *   nums = [1,2,3,4], k = 4  ->  [4]
 *
 * The output has exactly nums.length - k + 1 entries.
 *
 * Brute force re-scans each window: O(n*k). You must do O(n).
 *
 * =========================================================================
 * THE IDEA -- a MONOTONIC DEQUE
 * =========================================================================
 * This is the monotonic stack from Problems 10 and 11, upgraded: you now need
 * to remove from BOTH ends, so a stack is not enough and a deque is.
 *
 * Keep a deque of INDICES whose heights are in DECREASING order from front to
 * back. Then the FRONT always holds the index of the current window's maximum.
 * You read the answer off the front without searching.
 *
 * Two different removals, and keeping them straight is the whole problem:
 *
 *   FROM THE BACK -- "this element is useless now"
 *     While the back's value is <= nums[i], pop the back. A smaller element
 *     sitting to the LEFT of a bigger one can never be a maximum again: any
 *     future window containing it also contains nums[i], which is bigger and
 *     survives longer. It is permanently dominated, so discard it.
 *
 *   FROM THE FRONT -- "this element has slid out of the window"
 *     If the front index is <= i - k, it has fallen off the left edge.
 *     Remove it. This is a WINDOW BOUNDS check, not a value comparison --
 *     the value may well still be the largest in the array, but it is no
 *     longer inside the window, so it cannot be the answer.
 *
 * Per iteration i:
 *   1. evict from the FRONT any index that is out of the window
 *   2. pop from the BACK while nums[back] <= nums[i]
 *   3. add i at the BACK
 *   4. once i >= k - 1, the window is full -- record nums[front] as an answer
 *
 * Step 4's guard matters: for the first k-1 iterations the window has not
 * filled yet, so there is no maximum to report.
 *
 * =========================================================================
 * WHY IT IS O(n)
 * =========================================================================
 * Every index is added to the deque exactly once and removed at most once --
 * whether from the front (slid out) or the back (dominated). So there are at
 * most 2n deque operations regardless of k. Same amortised argument as the
 * monotonic stack; the only change is that removals happen at two ends.
 *
 * =========================================================================
 * THE DEQUE API YOU NEED
 * =========================================================================
 * A Deque works from both ends. Use the explicit First/Last method names here
 * rather than push/pop -- with two ends in play, push/pop is too easy to
 * misread:
 *
 *   Deque<Integer> dq = new ArrayDeque<>();
 *   dq.addLast(i);        // append at the back
 *   dq.pollLast();        // remove from the back
 *   dq.peekLast();        // look at the back
 *   dq.pollFirst();       // remove from the front
 *   dq.peekFirst();       // look at the front
 *   dq.isEmpty();
 *
 * =========================================================================
 * TRAPS
 * =========================================================================
 * 1. Storing VALUES instead of INDICES. Without indices you cannot tell when
 *    an element has slid out of the window. Store indices; read values with
 *    nums[index].
 *
 * 2. Using `<` instead of `<=` when popping the back. With duplicates like
 *    [2,2,2] and `<`, stale equal indices pile up. `<=` is correct: an equal
 *    element further left is also dominated, since the newer one lasts longer.
 *
 * 3. Off-by-one on the window bound. The window at output position i covers
 *    indices i-k+1 .. i, so an index is stale exactly when it is <= i-k.
 *
 * 4. Result array size. It is nums.length - k + 1, NOT nums.length.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem14.java
 */
public class Problem14 {

    public static int[] maxSlidingWindow(int[] nums, int k) {
        int left=0;
        int[] result = new int[nums.length - k +1];
        int right = k;
        int val = 0;
        for(int i=0;i<k;i++){
            val = Math.max(val, nums[i]);
        }
        result[0] = val;
        while(left < right && right<nums.length){
            if(nums[left]==val){
                val = Math.max(nums[left], nums[right]);
                
            }else{
                val = Math.max(val,nums[right]);
            }
            result[left] = val;
            left++;
            right--;
        }


        
        return result;
    }

    // ---------------------------------------------------------------
    // Test harness.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("--- worked examples ---");
        check(new int[] { 1, 3, -1, -3, 5, 3, 6, 7 }, 3);
        check(new int[] { 1 }, 1);
        check(new int[] { 9, 8, 7, 6 }, 2);
        check(new int[] { 1, 2, 3, 4 }, 4);

        System.out.println("\n--- edges ---");
        check(new int[] { 5, 5, 5, 5 }, 2);          // duplicates -> needs <=
        check(new int[] { 1, 2, 3, 4, 5 }, 1);       // k=1 -> output is the input
        check(new int[] { -7, -8, -9 }, 2);          // all negative
        check(new int[] { 4, 3, 2, 1, 5 }, 3);       // max arrives last
        check(new int[] { 5, 1, 2, 3, 4 }, 3);       // max slides out early

        System.out.println("\n--- random cross-check against brute force ---");
        java.util.Random rnd = new java.util.Random(14);
        int fails = 0;
        for (int t = 0; t < 500; t++) {
            int n = 1 + rnd.nextInt(15);
            int k = 1 + rnd.nextInt(n);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(21) - 10;
            if (!Arrays.equals(maxSlidingWindow(a.clone(), k), bruteMaxWindow(a, k))) fails++;
        }
        System.out.printf("random: %d/500%n", 500 - fails);

        System.out.println("\n--- O(n) check: 200k elements, k=1000 ---");
        int n = 200000;
        int[] big = new int[n];
        java.util.Random r2 = new java.util.Random(1);
        for (int i = 0; i < n; i++) big[i] = r2.nextInt(1000000);
        long t0 = System.nanoTime();
        int[] got = maxSlidingWindow(big, 1000);
        long ms = (System.nanoTime() - t0) / 1000000;
        // A correct O(n) solution finishes in a few milliseconds. Brute force
        // would be 200 million comparisons here.
        boolean sizeOk = got.length == n - 1000 + 1;
        System.out.printf("output length %d (expected %d) %s   [%d ms]%n",
                got.length, n - 1000 + 1, sizeOk ? "PASS" : "FAIL", ms);
    }

    private static void check(int[] nums, int k) {
        int[] expected = bruteMaxWindow(nums, k);
        int[] got = maxSlidingWindow(nums.clone(), k);
        boolean ok = Arrays.equals(got, expected);
        System.out.printf("%-28s k=%-3d -> %-24s (expected %-24s) %s%n",
                Arrays.toString(nums), k, Arrays.toString(got),
                Arrays.toString(expected), ok ? "PASS" : "FAIL");
    }

    private static int[] bruteMaxWindow(int[] nums, int k) {
        if (nums.length == 0 || k <= 0 || k > nums.length) return new int[0];
        int[] out = new int[nums.length - k + 1];
        for (int i = 0; i + k <= nums.length; i++) {
            int mx = nums[i];
            for (int j = i; j < i + k; j++) mx = Math.max(mx, nums[j]);
            out[i] = mx;
        }
        return out;
    }
}

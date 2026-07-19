import java.util.Arrays;
import java.util.Stack;

/*
 * PROBLEM 8: Next Greater Element II  (monotonic stack, round two)
 *
 * Given a CIRCULAR array `nums`, return an array `answer` where answer[i] is
 * the FIRST element GREATER than nums[i] that you meet walking forward from
 * i -- and because the array is circular, if you reach the end you wrap
 * around to the front and keep going. If no greater element exists anywhere,
 * answer[i] = -1.
 *
 * Example 1:
 *   nums   = [1, 2, 1]
 *   answer = [2, -1, 2]
 *
 *   Explanation:
 *     i=0 (1): next greater is 2 at index 1
 *     i=1 (2): nothing greater anywhere (2 is the max) -> -1
 *     i=2 (1): walk forward, hit the end, WRAP AROUND -> nums[1]... no wait,
 *              wrap gives nums[0]=1 (not greater), then nums[1]=2 -> 2
 *
 * Example 2:
 *   nums   = [1, 2, 3, 4, 3]
 *   answer = [2, 3, 4, -1, 4]
 *
 * Example 3:
 *   nums   = [5, 4, 3, 2, 1]
 *   answer = [-1, 5, 5, 5, 5]     <-- everything wraps around to find 5
 *
 * Constraints:
 *   1 <= nums.length <= 10000
 *   -10^9 <= nums[i] <= 10^9
 *
 * ---------------------------------------------------------------------------
 * TWO DIFFERENCES from Problem 7. Work out BOTH before you code:
 *
 * DIFFERENCE 1 -- what you record.
 *   Problem 7 wanted the DISTANCE (i - j). This one wants the VALUE of the
 *   greater element. Tiny change to the line inside your pop-loop. The stack
 *   should still hold INDICES though -- you need them to know WHERE to write.
 *
 * DIFFERENCE 2 -- the wrap-around. This is the real puzzle.
 *   In Problem 7, whatever was left on the stack at the end was simply done.
 *   Here it is NOT done: index 2 in example 1 still finds its answer by
 *   wrapping past the end.
 *
 *   THE TRICK: pretend you walk the array TWICE. Loop i from 0 to 2n-1 and
 *   use  i % n  everywhere you index into nums. The first lap builds the
 *   stack and resolves what it can; the second lap lets the leftovers see
 *   the elements "in front of them, wrapped around".
 *
 *   Then two questions to settle (think, don't guess):
 *     a) During the SECOND lap, should you still PUSH indices onto the
 *        stack, or only pop? What could go wrong if you push an index the
 *        stack has already seen -- could something get answered twice, and
 *        does it matter which answer wins? (Hint: the FIRST write is the
 *        correct one, so either only push on the first lap, or make sure a
 *        second write can never happen. Simplest: push only when i < n.)
 *     b) What do you initialise `answer` with? In Problem 7 the default 0
 *        was exactly right for "never found". Here "never found" must read
 *        -1, and Java initialises int[] to 0 -- so fill it yourself
 *        (Arrays.fill is your friend).
 *
 * SANITY CHECK for your comparison: nums = [100, 100]. Nothing is strictly
 * greater than anything, so the answer is [-1, -1]. Equal is NOT greater --
 * same strictness discipline as Problem 7.
 *
 * TIME/SPACE: O(n) time (the loop runs 2n times, each index pushed/popped
 * at most once), O(n) space for the stack.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem8.java
 */
public class Problem8 {

    public static int[] nextGreaterElements(int[] nums) {
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);
        Stack<Integer> s = new Stack<>();
        for(int i=0;i<2*nums.length;i++){
            while(!s.isEmpty() && nums[i%nums.length]>nums[s.peek()]){
                int j = s.pop();
                result[j] = nums[i%nums.length];
            }
            if(i<nums.length){
                s.push(i);
            }
            
        }
    
        return  result;
    }

    // ---------------------------------------------------------------
    // Test harness -- brute force cross-check plus random arrays.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check(new int[] { 1, 2, 1 });               // [2,-1,2]
        check(new int[] { 1, 2, 3, 4, 3 });         // [2,3,4,-1,4]
        check(new int[] { 5, 4, 3, 2, 1 });         // [-1,5,5,5,5]
        check(new int[] { 3, 3, 3 });               // [-1,-1,-1]  -- equal is NOT greater
        check(new int[] { 7 });                     // [-1]
        check(new int[] { 2, 5, 2, 4 });            // [5,-1,4,5]  -- wrap for the last one

        java.util.Random rnd = new java.util.Random(7);
        int failures = 0;
        for (int t = 0; t < 300; t++) {
            int[] a = new int[1 + rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(8) - 3; // -3..4, lots of ties
            int[] got = nextGreaterElements(a.clone());
            int[] expected = bruteForce(a);
            if (!Arrays.equals(got, expected)) {
                if (failures++ == 0) {
                    System.out.printf("%nRANDOM FAIL on %s%n  got:      %s%n  expected: %s%n",
                            Arrays.toString(a), Arrays.toString(got), Arrays.toString(expected));
                }
            }
        }
        System.out.printf("%nrandom tests: %d/300 passed%n", 300 - failures, 300);
    }

    private static int[] bruteForce(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        Arrays.fill(answer, -1);
        for (int i = 0; i < n; i++) {
            for (int step = 1; step < n; step++) {
                int j = (i + step) % n;
                if (nums[j] > nums[i]) {
                    answer[i] = nums[j];
                    break;
                }
            }
        }
        return answer;
    }

    private static void check(int[] nums) {
        int[] expected = bruteForce(nums);
        int[] got = nextGreaterElements(nums.clone());
        System.out.printf("%-22s -> %-22s (expected %-22s) %s%n",
                Arrays.toString(nums), Arrays.toString(got), Arrays.toString(expected),
                Arrays.equals(got, expected) ? "PASS" : "FAIL");
    }
}

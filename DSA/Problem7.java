import java.util.Arrays;
import java.util.Stack;

/*
 * PROBLEM 7: Daily Temperatures  (new technique: MONOTONIC STACK)
 *
 * Given an array `temperatures` where temperatures[i] is the temperature on
 * day i, return an array `answer` where answer[i] is the number of days you
 * have to wait after day i to get a WARMER temperature. If there is no
 * future day with a warmer temperature, answer[i] = 0.
 *
 * Example 1:
 *   temperatures = [73, 74, 75, 71, 69, 72, 76, 73]
 *   answer       = [ 1,  1,  4,  2,  1,  1,  0,  0]
 *
 *   Explanation for a couple of indices:
 *     i=0 (73): next warmer is 74 at i=1  -> wait 1 day
 *     i=2 (75): next warmer is 76 at i=6  -> wait 4 days
 *     i=6 (76): nothing warmer ever again -> 0
 *
 * Example 2:
 *   temperatures = [30, 40, 50, 60]
 *   answer       = [ 1,  1,  1,  0]
 *
 * Example 3:
 *   temperatures = [30, 60, 90]
 *   answer       = [ 1,  1,  0]
 *
 * Constraints:
 *   1 <= temperatures.length <= 100000
 *   30 <= temperatures[i] <= 100
 *
 * ---------------------------------------------------------------------------
 * WHY NOT BRUTE FORCE: for each day, scanning forward for the first warmer
 * day is O(n^2). With n up to 100000 that is 10 billion ops -- too slow.
 * You need each day to find its answer in roughly O(1) AMORTIZED time.
 *
 * THE IDEA -- MONOTONIC STACK:
 *   Walk the array left to right. Keep a stack of INDICES whose temperature
 *   you haven't found an answer for yet. The stack is kept DECREASING in
 *   temperature from bottom to top -- i.e. temperatures[stack.peek()] is
 *   always >= the temperature of whatever you'd push next... until it isn't.
 *
 *   At day i, while the stack is non-empty AND temperatures[i] is WARMER
 *   than temperatures[stack.peek()]:
 *     - pop that index j off the stack
 *     - you just found j's answer: i - j (that's how many days it waited)
 *   Once the stack stops being poppable (empty, or top is warmer-or-equal),
 *   push i onto the stack -- i is now "waiting" for its own warmer day.
 *
 *   Anything left on the stack at the end never found a warmer day, so its
 *   answer stays 0 (the array's default value already handles this).
 *
 * WHY THIS IS O(n) AND NOT O(n^2):
 *   It looks like a loop inside a loop (the while inside the for), but each
 *   index is pushed onto the stack EXACTLY ONCE and popped AT MOST ONCE
 *   across the whole run. Total push+pop operations <= 2n. That's what
 *   "amortized O(1) per element" means here.
 *
 * Questions to answer before you code:
 *   1. What do you store on the stack -- temperatures, or indices? (You need
 *      to compute i - j, so think about which one gives you that for free.)
 *   2. What Java structure gives you push/pop/peek? (java.util.Deque used as
 *      a stack via push/pop/peek is the idiomatic choice -- avoid the legacy
 *      java.util.Stack class.)
 *   3. Strict "warmer than", or "warmer than or equal to"? Check example 1
 *      at index 2 (75) vs index 6 (76) -- walk through what breaks if you
 *      get the comparison direction backwards.
 *
 * TIME/SPACE: O(n) time, O(n) space (worst case: strictly decreasing input,
 * e.g. [90, 80, 70] never pops anything until the stack itself is done).
 *
 * TO RUN:
 *   cd DSA
 *   java Problem7.java
 */
public class Problem7 {

    public static int[] dailyTemperatures(int[] temperatures) {
        int[] result = new int[temperatures.length];
        Stack<Integer> s = new Stack<>();
        for(int i=0;i<temperatures.length;i++){
            while(!s.isEmpty() && temperatures[i]>temperatures[s.peek()]){
                int j = s.pop();
                result[j] = i-j;
            }
            s.push(i);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Test harness -- brute force cross-check plus random arrays.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check(new int[] { 73, 74, 75, 71, 69, 72, 76, 73 }); // [1,1,4,2,1,1,0,0]
        check(new int[] { 30, 40, 50, 60 });                 // [1,1,1,0]
        check(new int[] { 30, 60, 90 });                     // [1,1,0]
        check(new int[] { 90, 80, 70 });                     // [0,0,0]
        check(new int[] { 55 });                             // [0]
        check(new int[] { 50, 50, 50 });                     // [0,0,0]  -- equal is NOT warmer

        java.util.Random rnd = new java.util.Random(42);
        int failures = 0;
        for (int t = 0; t < 300; t++) {
            int[] a = new int[1 + rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = 30 + rnd.nextInt(10); // narrow range -> lots of ties
            int[] got = dailyTemperatures(a.clone());
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

    private static int[] bruteForce(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (temperatures[j] > temperatures[i]) {
                    answer[i] = j - i;
                    break;
                }
            }
        }
        return answer;
    }

    private static void check(int[] temperatures) {
        int[] expected = bruteForce(temperatures);
        int[] got = dailyTemperatures(temperatures.clone());
        System.out.printf("%-30s -> %-30s (expected %-30s) %s%n",
                Arrays.toString(temperatures), Arrays.toString(got), Arrays.toString(expected),
                Arrays.equals(got, expected) ? "PASS" : "FAIL");
    }
}

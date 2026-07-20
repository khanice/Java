import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Stack;

/*
 * PROBLEM 10: Stacks -- three parts, escalating. Do them in order.
 *
 * A stack is LIFO: last in, first out. Three operations, all O(1):
 *     push(x)   put x on top
 *     pop()     remove and return the top       -- throws if empty
 *     peek()    look at the top without removing -- throws if empty
 *     isEmpty() ALWAYS check this before pop/peek
 *
 * In Java, use ArrayDeque, NOT the legacy java.util.Stack (which is synchronised
 * and slower, and iterates in the wrong order):
 *
 *     Deque<Character> stack = new ArrayDeque<>();
 *     stack.push(c);
 *     char top = stack.peek();
 *     stack.pop();
 *     stack.isEmpty();
 *
 * WHEN TO REACH FOR A STACK -- the mental model that matters:
 *   Use a stack when you meet something you CANNOT RESOLVE YET, and the thing
 *   that resolves it comes LATER, and the MOST RECENT unresolved item is always
 *   the first one to get resolved. That last clause is the giveaway. Nesting,
 *   matching, "undo", and "the next bigger thing" all have this shape.
 *
 * =========================================================================
 * PART A -- Valid Parentheses
 * =========================================================================
 * Given a string of just  ( ) [ ] { }  decide whether it is properly matched.
 * Every bracket must close with the SAME TYPE, and in the CORRECT ORDER.
 *
 *   "()"        -> true
 *   "()[]{}"    -> true
 *   "(]"        -> false     wrong type
 *   "([)]"      -> false     wrong ORDER -- this is the one a counter cannot catch
 *   "{[()]}"    -> true
 *   ""          -> true
 *   "("         -> false     never closed
 *   ")"         -> false     closes nothing
 *
 * WHY A COUNTER FAILS: if you just count openers and closers, "([)]" balances
 * to zero and you would wrongly say true. Order matters, so you must remember
 * WHICH bracket is still open, in what sequence. That is a stack.
 *
 * THE ALGORITHM: push openers. On a closer, check the top matches -- if the
 * stack is empty or the top is the wrong type, return false. At the very end,
 * the stack MUST be empty (otherwise something never closed).
 *
 * Both empty-stack checks are real test cases above. Do not skip them.
 *
 * =========================================================================
 * PART B -- Next Greater Element (the MONOTONIC STACK)
 * =========================================================================
 * For each element, find the first element to its RIGHT that is strictly
 * greater. If none exists, use -1.
 *
 *   [2, 1, 2, 4, 3]  ->  [4, 2, 4, -1, -1]
 *    2 -> 4 (skips the 1)
 *    1 -> 2
 *    2 -> 4
 *    4 -> nothing bigger to its right -> -1
 *    3 -> -1
 *
 *   [5, 4, 3, 2]     ->  [-1, -1, -1, -1]     (strictly decreasing)
 *   [1, 2, 3, 4]     ->  [2, 3, 4, -1]
 *
 * Brute force is O(n^2): for each i, scan right. You must do O(n).
 *
 * THE IDEA: sweep left to right keeping a stack of INDICES whose answer is
 * still unknown. When you meet nums[i], it is the "next greater" for every
 * pending index whose value is SMALLER than nums[i] -- so pop them all and
 * record i as their answer. Then push i, because its own answer is unknown.
 *
 *     for i in 0..n-1:
 *         while stack not empty AND nums[stack.peek()] < nums[i]:
 *             result[stack.pop()] = nums[i]
 *         stack.push(i)
 *     -- anything still on the stack at the end gets -1
 *
 * Initialise result to -1 everywhere and the leftovers handle themselves.
 *
 * WHY IT IS O(n), NOT O(n^2): there is a while inside a for, but every index is
 * pushed exactly ONCE and popped AT MOST ONCE, so the total pop work across the
 * whole run is at most n. This is the SAME amortised argument as your sliding
 * window in Problem 4 -- `left` never moved backward there, indices never get
 * re-pushed here.
 *
 * The stack stays in DECREASING order of value from bottom to top -- that
 * invariant is why this is called a "monotonic stack". Flip the comparison and
 * you get next-smaller instead.
 *
 * =========================================================================
 * PART C -- Daily Temperatures
 * =========================================================================
 * Given daily temperatures, return an array where answer[i] is the number of
 * days you must WAIT after day i for a warmer temperature. 0 if it never gets
 * warmer.
 *
 *   [73,74,75,71,69,72,76,73] -> [1,1,4,2,1,1,0,0]
 *   [30,40,50,60]             -> [1,1,1,0]
 *   [30,60,90]                -> [1,1,0]
 *
 * This is Part B with ONE line changed. Work out which. (Hint: you already
 * store indices on the stack. What is the DISTANCE between two indices?)
 *
 * Recognising that two differently-worded problems are the same algorithm is
 * most of what interview skill actually is.
 *
 * TIME/SPACE: all three are O(n) time, O(n) space.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem8.java
 */
public class Problem10 {

    // ---------------- PART A ----------------
    public static boolean isValid(String s) {
        Deque<Character> stack =  new ArrayDeque<>();
        if(s.length() <= 1)return true;
        for(int i=0;i<s.length();i++){
            while(!stack.isEmpty()){
                if(s.charAt(i)=='{' ){
                    stack.push('}');
                }else if (s.charAt(i) == '[' ) {
                    stack.push(']');
                }else if(s.charAt(i) == '(') {
                    stack.push(')');
                } else {
                    if (stack.isEmpty() || stack.pop() != s.charAt(i)) {
                        return false;
                    }
                }
            }
        }
        
        return stack.isEmpty();
    }

    // ---------------- PART B ----------------
    public static int[] nextGreater(int[] nums) {
        int[] result = new int[nums.length];
        Arrays.fill(result,-1);
        Stack<Integer> stack = new Stack<>();
        for(int i=0;i<nums.length;i++){
            while (!stack.isEmpty() && nums[i]>nums[stack.peek()]) {
                int j= stack.pop();
                result[j] = nums[i];
            }
            stack.push(i);
        }
        return result;
    }

    // ---------------- PART C ----------------
    public static int[] dailyTemperatures(int[] temps) {int[] result = new int[temps.length];
        
        Stack<Integer> stack = new Stack<>();
        for(int i=0;i<temps.length;i++){
            while (!stack.isEmpty() && temps[i]>temps[stack.peek()]) {
                int j= stack.pop();
                result[j] = i-j;
            }
            stack.push(i);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Test harness.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("--- PART A: valid parentheses ---");
        checkA("()", true);
        checkA("()[]{}", true);
        checkA("(]", false);
        checkA("([)]", false);      // the order case
        checkA("{[()]}", true);
        checkA("", true);
        checkA("(", false);         // never closed
        checkA(")", false);         // closes nothing
        checkA("]", false);
        checkA("((((", false);

        System.out.println("\n--- PART B: next greater element ---");
        checkB(new int[] { 2, 1, 2, 4, 3 });
        checkB(new int[] { 5, 4, 3, 2 });
        checkB(new int[] { 1, 2, 3, 4 });
        checkB(new int[] { 1 });
        checkB(new int[] { });
        checkB(new int[] { 2, 2, 2 });      // equal values -- strictly greater!

        System.out.println("\n--- PART C: daily temperatures ---");
        checkC(new int[] { 73, 74, 75, 71, 69, 72, 76, 73 });
        checkC(new int[] { 30, 40, 50, 60 });
        checkC(new int[] { 30, 60, 90 });
        checkC(new int[] { 50 });
        checkC(new int[] { 90, 80, 70 });

        // O(n) check: 200k strictly decreasing, then one big value at the end.
        // This is the worst case -- every index sits on the stack until the last
        // element pops them all. An O(n^2) solution will crawl here.
        int n = 200000;
        int[] big = new int[n];
        for (int i = 0; i < n - 1; i++) big[i] = n - i;
        big[n - 1] = Integer.MAX_VALUE;
        long t0 = System.nanoTime();
        int[] r = dailyTemperatures(big);
        long ms = (System.nanoTime() - t0) / 1000000;
        boolean ok = r != null && r.length == n && r[0] == n - 1 && r[n - 1] == 0;
        System.out.printf("%n200k worst case -> %s   [%d ms]%n", ok ? "PASS" : "FAIL", ms);

        // Randomised cross-check.
        java.util.Random rnd = new java.util.Random(9);
        int fb = 0, fc = 0;
        for (int t = 0; t < 400; t++) {
            int[] a = new int[rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
            if (!Arrays.equals(nextGreater(a.clone()), bruteNextGreater(a))) fb++;
            if (!Arrays.equals(dailyTemperatures(a.clone()), bruteDaily(a))) fc++;
        }
        System.out.printf("random: B %d/400, C %d/400%n", 400 - fb, 400 - fc);
    }

    private static int[] bruteNextGreater(int[] a) {
        int[] r = new int[a.length];
        Arrays.fill(r, -1);
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[j] > a[i]) { r[i] = a[j]; break; }
        return r;
    }

    private static int[] bruteDaily(int[] a) {
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[j] > a[i]) { r[i] = j - i; break; }
        return r;
    }

    private static void checkA(String s, boolean expected) {
        boolean got = isValid(s);
        System.out.printf("%-12s -> %-6s (expected %-6s) %s%n",
                "\"" + s + "\"", got, expected, got == expected ? "PASS" : "FAIL");
    }

    private static void checkB(int[] a) {
        int[] expected = bruteNextGreater(a);
        int[] got = nextGreater(a.clone());
        System.out.printf("%-18s -> %-22s (expected %-22s) %s%n",
                Arrays.toString(a), Arrays.toString(got), Arrays.toString(expected),
                Arrays.equals(got, expected) ? "PASS" : "FAIL");
    }

    private static void checkC(int[] a) {
        int[] expected = bruteDaily(a);
        int[] got = dailyTemperatures(a.clone());
        System.out.printf("%-30s -> %-24s (expected %-24s) %s%n",
                Arrays.toString(a), Arrays.toString(got), Arrays.toString(expected),
                Arrays.equals(got, expected) ? "PASS" : "FAIL");
    }
}

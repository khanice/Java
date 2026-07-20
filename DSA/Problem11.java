import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
 * PROBLEM 11: Stacks II -- three parts, escalating. Do them in order.
 *
 * Problem 10 covered the core monotonic-stack shape (next greater, daily
 * temperatures). This one pushes further: augmenting a stack with extra
 * state (Part A), using a stack to EVALUATE something instead of just
 * matching (Part B), and the hardest classic monotonic-stack problem,
 * which is the same "pop while smaller" idea from Problem 10 but the thing
 * you compute on each pop is an AREA, not an index distance (Part C).
 *
 * =========================================================================
 * PART A -- Min Stack
 * =========================================================================
 * Design a stack that supports push, pop, top, AND getMin -- retrieving the
 * minimum element currently in the stack -- ALL in O(1).
 *
 *   push(-2); push(0); push(-3);
 *   getMin() -> -3
 *   pop();          // removes -3
 *   top()    -> 0
 *   getMin() -> -2
 *
 * WHY THE OBVIOUS APPROACH FAILS: if getMin() scans the whole stack, that's
 * O(n), not O(1). You need the answer ready WITHOUT looking.
 *
 * THE TRICK: keep a SECOND stack alongside the main one -- the "min stack" --
 * where minStack.peek() is always the minimum of everything currently
 * pushed. On every push(val), also push Math.min(val, current min) onto the
 * min stack (or just val, if the min stack is empty). On every pop(), pop
 * BOTH stacks together, in lockstep. Now minStack.peek() is always the
 * answer, no scanning required.
 *
 * Two stacks, moving together, same size at all times -- that's the whole
 * idea.
 *
 * =========================================================================
 * PART B -- Evaluate Reverse Polish Notation (postfix)
 * =========================================================================
 * tokens is an expression in POSTFIX form: operators come AFTER their
 * operands. Evaluate it and return the result.
 *
 *   ["2","1","+","3","*"]                                  -> 9   ((2+1)*3)
 *   ["4","13","5","/","+"]                                 -> 6   (4 + 13/5)
 *   ["10","6","9","3","+","-11","*","/","*","17","+","5","+"] -> 22
 *
 * THE ALGORITHM: scan tokens left to right.
 *   - if the token is a NUMBER, push it.
 *   - if the token is an OPERATOR, pop TWICE. The value popped SECOND is the
 *     left operand, the value popped FIRST is the right operand (it was
 *     pushed later, so it comes off first). Apply the operator, push the
 *     result back.
 * At the end exactly one value remains -- that is the answer.
 *
 * WATCH OUT: order matters for "-" and "/". If you pop `b` then `a`, compute
 * a - b (or a / b), NOT b - a. Getting this backwards is the single most
 * common bug here.
 *
 * WATCH OUT #2: a token like "-11" is a NEGATIVE NUMBER, not the subtraction
 * operator. Don't decide "is this an operator?" by checking whether the
 * string CONTAINS "-" -- check whether it EQUALS "+", "-", "*", or "/"
 * exactly (and has length 1).
 *
 * Integer division in Java already truncates toward zero, which matches
 * what this problem expects -- no special handling needed there.
 *
 * =========================================================================
 * PART C -- Largest Rectangle in Histogram
 * =========================================================================
 * heights[i] is the height of bar i in a histogram, each bar width 1. Find
 * the area of the LARGEST rectangle that fits entirely inside the skyline.
 *
 *   [2,1,5,6,2,3]  -> 10     (the bars of height 5 and 6, width 2 -> 5*2=10)
 *   [2,4]          -> 4
 *   [1,1,1,1]      -> 4      (whole thing, height 1, width 4)
 *   []             -> 0
 *
 * Brute force is O(n^2): for every pair (i, j), the rectangle spanning bars
 * i..j has height = min(heights[i..j]). You must do O(n).
 *
 * THE IDEA -- same pop-while-smaller shape as Problem 10, different payload:
 * keep a stack of INDICES with heights in INCREASING order from bottom to
 * top. When heights[i] is smaller than the bar at the top of the stack, that
 * top bar can never extend any further right than i-1 -- its rectangle is
 * now fully determined, so pop it and compute its area:
 *
 *     height = heights[popped]
 *     width  = i - stack.peek() - 1      (the new top is the nearest
 *                                          SMALLER bar to the left; if the
 *                                          stack is empty, width = i)
 *     area   = height * width
 *
 * Push i. Track the best area seen.
 *
 * THE FLUSH TRICK: bars still on the stack at the end never met a smaller
 * bar to their right. Easiest fix: pretend there's one more bar of height 0
 * after the array (loop i from 0 to n inclusive, treating heights[n] as 0).
 * That forces every remaining bar to pop and get priced before you finish.
 *
 * TIME/SPACE: O(n) time, O(n) space -- same amortised argument as always:
 * each index is pushed once, popped at most once.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem11.java
 */
public class Problem11 {

    // ---------------- PART A ----------------
    static class MinStack {
        public MinStack() {
            // TODO
        }

        public void push(int val) {
            // TODO
        }

        public void pop() {
            // TODO
        }

        public int top() {
            // TODO
            return -1;
        }

        public int getMin() {
            // TODO
            return -1;
        }
    }

    // ---------------- PART B ----------------
    public static int evalRPN(String[] tokens) {
        // TODO
        return 0;
    }

    // ---------------- PART C ----------------
    public static int largestRectangleArea(int[] heights) {
        // TODO
        return 0;
    }

    // ---------------------------------------------------------------
    // Test harness.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("--- PART A: min stack ---");
        checkAScripted();
        fuzzMinStack();

        System.out.println("\n--- PART B: evaluate RPN ---");
        checkB(new String[] { "2", "1", "+", "3", "*" }, 9);
        checkB(new String[] { "4", "13", "5", "/", "+" }, 6);
        checkB(new String[] { "10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+" }, 22);
        checkB(new String[] { "18" }, 18);
        checkB(new String[] { "4", "3", "-" }, 1);
        checkB(new String[] { "15", "7", "1", "1", "+", "-", "/", "3", "*" }, 9);

        System.out.println("\n--- PART C: largest rectangle in histogram ---");
        checkC(new int[] { 2, 1, 5, 6, 2, 3 });
        checkC(new int[] { 2, 4 });
        checkC(new int[] { 1, 1, 1, 1 });
        checkC(new int[] { });
        checkC(new int[] { 0 });
        checkC(new int[] { 5 });
        checkC(new int[] { 6, 5, 4, 3, 2, 1 });
        checkC(new int[] { 1, 2, 3, 4, 5, 6 });

        // O(n) check: 100k equal-height bars -- worst case, nothing pops
        // until the final flush.
        int n = 100000;
        int[] big = new int[n];
        Arrays.fill(big, 5);
        long t0 = System.nanoTime();
        int area = largestRectangleArea(big);
        long ms = (System.nanoTime() - t0) / 1000000;
        boolean ok = area == 5 * n;
        System.out.printf("%n100k flat bars -> %d (expected %d)  %s   [%d ms]%n",
                area, 5 * n, ok ? "PASS" : "FAIL", ms);

        // Randomised cross-check against brute force.
        java.util.Random rnd = new java.util.Random(11);
        int fails = 0;
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(11);
            if (largestRectangleArea(a.clone()) != bruteLargestRectangle(a)) fails++;
        }
        System.out.printf("random: C %d/300%n", 300 - fails);
    }

    private static void checkAScripted() {
        MinStack s = new MinStack();
        s.push(-2);
        s.push(0);
        s.push(-3);
        boolean p1 = s.getMin() == -3;
        s.pop();
        boolean p2 = s.top() == 0;
        boolean p3 = s.getMin() == -2;
        System.out.printf("push -2,0,-3 -> min=-3; pop; top=0,min=-2 : %s%n",
                (p1 && p2 && p3) ? "PASS" : "FAIL");
    }

    private static void fuzzMinStack() {
        java.util.Random rnd = new java.util.Random(7);
        int fails = 0;
        for (int t = 0; t < 200; t++) {
            MinStack s = new MinStack();
            ArrayList<Integer> ref = new ArrayList<>();
            int ops = 1 + rnd.nextInt(30);
            boolean ok = true;
            for (int i = 0; i < ops; i++) {
                int choice = ref.isEmpty() ? 0 : rnd.nextInt(4);
                if (choice == 0) {
                    int v = rnd.nextInt(200) - 100;
                    s.push(v);
                    ref.add(v);
                } else if (choice == 1) {
                    s.pop();
                    ref.remove(ref.size() - 1);
                } else if (choice == 2) {
                    if (s.top() != ref.get(ref.size() - 1)) ok = false;
                } else {
                    if (s.getMin() != Collections.min(ref)) ok = false;
                }
            }
            if (!ok) fails++;
        }
        System.out.printf("MinStack random sequences: %d/200 correct%n", 200 - fails);
    }

    private static int bruteLargestRectangle(int[] h) {
        int best = 0;
        for (int i = 0; i < h.length; i++) {
            int min = h[i];
            for (int j = i; j < h.length; j++) {
                min = Math.min(min, h[j]);
                best = Math.max(best, min * (j - i + 1));
            }
        }
        return best;
    }

    private static void checkB(String[] tokens, int expected) {
        int got = evalRPN(tokens);
        System.out.printf("%-55s -> %-5d (expected %-5d) %s%n",
                Arrays.toString(tokens), got, expected, got == expected ? "PASS" : "FAIL");
    }

    private static void checkC(int[] heights) {
        int expected = bruteLargestRectangle(heights);
        int got = largestRectangleArea(heights.clone());
        System.out.printf("%-24s -> %-6d (expected %-6d) %s%n",
                Arrays.toString(heights), got, expected, got == expected ? "PASS" : "FAIL");
    }
}

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * PROBLEM 12: Implement Queue using Stacks
 *
 * You just spent two problems on stacks. Now use them to build the OPPOSITE
 * data structure. A stack is LIFO (last in, first out). A queue is FIFO
 * (first in, first out). Build a working queue whose only storage is two
 * stacks.
 *
 * Support four operations:
 *   push(x)  -- add x to the BACK of the queue
 *   pop()    -- remove and return the element at the FRONT
 *   peek()   -- return the element at the FRONT without removing it
 *   empty()  -- true if the queue has no elements
 *
 *   push(1); push(2);
 *   peek()  -> 1        (1 arrived first, so it leaves first)
 *   pop()   -> 1
 *   empty() -> false
 *
 * =========================================================================
 * WHY TWO STACKS
 * =========================================================================
 * One stack alone gives you the wrong end. If you push 1 then 2, the stack
 * hands you 2 first -- but a queue must hand you 1. You need to REVERSE the
 * order, and pouring one stack into another does exactly that:
 *
 *   in:  [1, 2, 3]   (3 on top)
 *   pour every element from `in` into `out`, one pop/push at a time:
 *   out: [3, 2, 1]   (1 on top)  <-- now the top IS the front of the queue
 *
 * So keep TWO stacks:
 *   `in`  -- everything newly pushed goes here
 *   `out` -- elements ready to leave, already in reversed (queue) order
 *
 * push(x):  just push onto `in`. Nothing else.
 * pop()/peek():  if `out` is EMPTY, pour ALL of `in` into `out` first.
 *                Then pop/peek `out`.
 * empty():  true only when BOTH stacks are empty.
 *
 * =========================================================================
 * THE CRITICAL RULE
 * =========================================================================
 * Only pour when `out` is EMPTY. Never pour into a non-empty `out`.
 *
 * Why: `out` already holds older elements in the correct order. Dumping newer
 * elements on top of them would put the NEW arrivals in front of the OLD
 * ones -- exactly backwards. The old ones must all leave first.
 *
 * Get this wrong and simple cases still pass; it only breaks when you
 * interleave pushes and pops (push, push, pop, push, pop). The fuzz test
 * below is built to catch precisely that, so don't trust the scripted test
 * alone.
 *
 * =========================================================================
 * WHY THIS IS O(1) AMORTISED
 * =========================================================================
 * A single pop() can cost O(n) when it triggers a pour. But each element is
 * poured from `in` to `out` AT MOST ONCE in its whole lifetime -- once it is
 * in `out` it never goes back. So n operations do at most n pours total,
 * which averages O(1) per operation. This is the same amortised argument as
 * the monotonic stack in Problems 10 and 11: "each element moves at most
 * once."
 *
 * TO RUN:
 *   cd DSA
 *   java Problem12.java
 */
public class Problem12 {

    static class MyQueue {
        private Deque<Integer> in = new ArrayDeque<>();
        private Deque<Integer> out = new ArrayDeque<>();

        public MyQueue() {
        }

        public void push(int x) {
            in.push(x);
        }

        public int pop() {
            pour();
            return out.pop();
        }

        public int peek() {
            pour();
            return out.peek();
        }

        public boolean empty() {
            return in.isEmpty() && out.isEmpty();
        }

        private void pour() {
            if (out.isEmpty()) {
                while (!in.isEmpty())
                    out.push(in.pop());
            }
        }
    }

    // ---------------------------------------------------------------
    // Test harness.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("--- scripted ---");
        checkScripted();

        System.out.println("\n--- interleaved (the case that catches early pouring) ---");
        checkInterleaved();

        System.out.println("\n--- random ---");
        fuzz();

        System.out.println("\n--- amortised O(1) check: 200k operations ---");
        perf();
    }

    private static void checkScripted() {
        MyQueue q = new MyQueue();
        q.push(1);
        q.push(2);
        boolean p1 = q.peek() == 1;
        boolean p2 = q.pop() == 1;
        boolean p3 = !q.empty();
        boolean p4 = q.pop() == 2;
        boolean p5 = q.empty();
        System.out.printf("push 1,2 -> peek=1, pop=1, notEmpty, pop=2, empty : %s%n",
                (p1 && p2 && p3 && p4 && p5) ? "PASS" : "FAIL");
    }

    private static void checkInterleaved() {
        // push 1, push 2, pop (->1), push 3, pop (->2), pop (->3)
        MyQueue q = new MyQueue();
        q.push(1);
        q.push(2);
        int a = q.pop();
        q.push(3);
        int b = q.pop();
        int c = q.pop();
        boolean ok = a == 1 && b == 2 && c == 3;
        System.out.printf("push1,push2,pop,push3,pop,pop -> %d,%d,%d (expected 1,2,3) %s%n",
                a, b, c, ok ? "PASS" : "FAIL");
    }

    private static void fuzz() {
        java.util.Random rnd = new java.util.Random(12);
        int fails = 0;
        for (int t = 0; t < 500; t++) {
            MyQueue q = new MyQueue();
            ArrayDeque<Integer> ref = new ArrayDeque<>();
            int ops = 1 + rnd.nextInt(40);
            boolean ok = true;
            for (int i = 0; i < ops; i++) {
                int choice = ref.isEmpty() ? 0 : rnd.nextInt(4);
                if (choice == 0) {
                    int v = rnd.nextInt(200) - 100;
                    q.push(v);
                    ref.addLast(v);
                } else if (choice == 1) {
                    if (q.pop() != ref.pollFirst()) ok = false;
                } else if (choice == 2) {
                    if (q.peek() != ref.peekFirst()) ok = false;
                } else {
                    if (q.empty() != ref.isEmpty()) ok = false;
                }
            }
            if (ok && q.empty() != ref.isEmpty()) ok = false;
            if (!ok) fails++;
        }
        System.out.printf("random sequences: %d/500 correct%n", 500 - fails);
    }

    private static void perf() {
        int n = 200000;
        MyQueue q = new MyQueue();
        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) q.push(i);
        boolean ok = true;
        for (int i = 0; i < n; i++) {
            if (q.pop() != i) { ok = false; break; }
        }
        long ms = (System.nanoTime() - t0) / 1000000;
        System.out.printf("200k push then 200k pop in order -> %s   [%d ms]%n",
                ok ? "PASS" : "FAIL", ms);
    }
}

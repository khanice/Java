/*
 * PROBLEM 13: Design Circular Queue (ring buffer)
 *
 * Build a queue with a FIXED capacity k, backed by a plain int array, where
 * space freed by dequeuing is REUSED. No ArrayList, no Deque, no shifting
 * elements -- one array and some index arithmetic.
 *
 * Support:
 *   MyCircularQueue(k) -- create a queue that holds at most k elements
 *   enQueue(v)         -- insert v at the back. Return true on success,
 *                         false if the queue is FULL.
 *   deQueue()          -- delete the element at the front. Return true on
 *                         success, false if the queue is EMPTY.
 *   Front()            -- front element, or -1 if empty
 *   Rear()             -- last element, or -1 if empty
 *   isEmpty()
 *   isFull()
 *
 *   MyCircularQueue q = new MyCircularQueue(3);
 *   q.enQueue(1)  -> true
 *   q.enQueue(2)  -> true
 *   q.enQueue(3)  -> true
 *   q.enQueue(4)  -> false      (full -- capacity is 3)
 *   q.Rear()      -> 3
 *   q.isFull()    -> true
 *   q.deQueue()   -> true
 *   q.enQueue(4)  -> true       (a slot freed up, and it gets REUSED)
 *   q.Rear()      -> 4
 *
 * =========================================================================
 * WHY "CIRCULAR"
 * =========================================================================
 * The naive array queue keeps a `head` and `tail` index and only moves them
 * forward. After a few enqueue/dequeue rounds, `tail` runs off the end of the
 * array even though the front of the array is now empty and wasted.
 *
 * The fix: when an index reaches the end, WRAP it back to 0. The array
 * behaves like a ring. The one operation that does all the work:
 *
 *     index = (index + 1) % capacity
 *
 * That is the whole trick. `% capacity` is what turns a line into a circle.
 *
 * =========================================================================
 * THE STATE YOU NEED
 * =========================================================================
 * Keep:
 *   int[] data     -- the backing array, length k
 *   int head       -- index of the FRONT element
 *   int size       -- how many elements are currently stored
 *
 * Deriving the rear index from head and size avoids a classic trap (see
 * below):
 *
 *     rearIndex = (head + size - 1) % capacity
 *
 * enQueue(v):  if isFull() return false.
 *              write v at (head + size) % capacity, then size++.
 * deQueue():   if isEmpty() return false.
 *              head = (head + 1) % capacity, then size--.
 * isEmpty():   size == 0
 * isFull():    size == capacity
 *
 * =========================================================================
 * THE CLASSIC TRAP -- head == tail is AMBIGUOUS
 * =========================================================================
 * If you store `head` and `tail` instead of `head` and `size`, then
 * head == tail means EITHER completely empty OR completely full, and you
 * cannot tell which. People work around it by wasting one slot or adding a
 * boolean flag. Tracking `size` sidesteps the whole problem -- empty and full
 * become two different numbers. Use `size`.
 *
 * SECOND TRAP: Front() and Rear() must return -1 when empty, not crash and
 * not return leftover garbage from the array. A dequeued slot still holds its
 * old value -- your emptiness check must come from `size`, never from what
 * happens to be sitting in `data`.
 *
 * THIRD TRAP: k can be 1. Then every wrap is immediate. The tests cover it.
 *
 * TIME/SPACE: every operation O(1). O(k) space, allocated once up front.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem13.java
 */
public class Problem13 {

    static class MyCircularQueue {
        private int[] data;
        private int head;
        private int size;

        public MyCircularQueue(int k) {
            // TODO
        }

        public boolean enQueue(int value) {
            // TODO
            return false;
        }

        public boolean deQueue() {
            // TODO
            return false;
        }

        public int Front() {
            // TODO
            return -1;
        }

        public int Rear() {
            // TODO
            return -1;
        }

        public boolean isEmpty() {
            // TODO
            return true;
        }

        public boolean isFull() {
            // TODO
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Test harness.
    // ---------------------------------------------------------------
    
    public static void main(String[] args) {
        System.out.println("--- scripted ---");
        checkScripted();

        System.out.println("\n--- wrap-around reuse ---");
        checkWrap();

        System.out.println("\n--- capacity 1 (every wrap is immediate) ---");
        checkCapacityOne();

        System.out.println("\n--- empty-queue behaviour ---");
        checkEmpty();

        System.out.println("\n--- random ---");
        fuzz();
    }

    private static void checkScripted() {
        MyCircularQueue q = new MyCircularQueue(3);
        boolean ok = true;
        ok &= q.enQueue(1);
        ok &= q.enQueue(2);
        ok &= q.enQueue(3);
        ok &= !q.enQueue(4);        // full
        ok &= q.Rear() == 3;
        ok &= q.isFull();
        ok &= q.deQueue();
        ok &= q.enQueue(4);         // slot reused
        ok &= q.Rear() == 4;
        ok &= q.Front() == 2;
        System.out.printf("k=3: fill, reject, dequeue, reuse : %s%n", ok ? "PASS" : "FAIL");
    }

    private static void checkWrap() {
        // Go around the ring several times; the array must be reused, not
        // exhausted.
        MyCircularQueue q = new MyCircularQueue(3);
        boolean ok = true;
        for (int round = 0; round < 10; round++) {
            ok &= q.enQueue(round * 3);
            ok &= q.enQueue(round * 3 + 1);
            ok &= q.Front() == round * 3;
            ok &= q.Rear() == round * 3 + 1;
            ok &= q.deQueue();
            ok &= q.deQueue();
            ok &= q.isEmpty();
        }
        System.out.printf("10 laps around a k=3 ring : %s%n", ok ? "PASS" : "FAIL");
    }

    private static void checkCapacityOne() {
        MyCircularQueue q = new MyCircularQueue(1);
        boolean ok = true;
        ok &= q.isEmpty();
        ok &= q.enQueue(7);
        ok &= q.isFull();
        ok &= !q.enQueue(8);
        ok &= q.Front() == 7 && q.Rear() == 7;
        ok &= q.deQueue();
        ok &= q.isEmpty() && !q.isFull();
        ok &= q.enQueue(9);
        ok &= q.Front() == 9 && q.Rear() == 9;
        System.out.printf("k=1 : %s%n", ok ? "PASS" : "FAIL");
    }

    private static void checkEmpty() {
        MyCircularQueue q = new MyCircularQueue(2);
        boolean ok = true;
        ok &= q.isEmpty();
        ok &= q.Front() == -1;       // must be -1, not garbage
        ok &= q.Rear() == -1;
        ok &= !q.deQueue();          // nothing to remove
        // Drain a used queue and confirm it reports -1 again, rather than
        // leftover array contents.
        q.enQueue(5);
        q.deQueue();
        ok &= q.isEmpty();
        ok &= q.Front() == -1;
        ok &= q.Rear() == -1;
        System.out.printf("empty and drained -> -1, deQueue false : %s%n", ok ? "PASS" : "FAIL");
    }

    private static void fuzz() {
        java.util.Random rnd = new java.util.Random(13);
        int fails = 0;
        for (int t = 0; t < 500; t++) {
            int k = 1 + rnd.nextInt(5);
            MyCircularQueue q = new MyCircularQueue(k);
            java.util.ArrayDeque<Integer> ref = new java.util.ArrayDeque<>();
            int ops = 1 + rnd.nextInt(50);
            boolean ok = true;
            for (int i = 0; i < ops; i++) {
                switch (rnd.nextInt(6)) {
                    case 0: {
                        int v = rnd.nextInt(100);
                        boolean got = q.enQueue(v);
                        boolean want = ref.size() < k;
                        if (want) ref.addLast(v);
                        if (got != want) ok = false;
                        break;
                    }
                    case 1: {
                        boolean got = q.deQueue();
                        boolean want = !ref.isEmpty();
                        if (want) ref.pollFirst();
                        if (got != want) ok = false;
                        break;
                    }
                    case 2:
                        if (q.Front() != (ref.isEmpty() ? -1 : ref.peekFirst())) ok = false;
                        break;
                    case 3:
                        if (q.Rear() != (ref.isEmpty() ? -1 : ref.peekLast())) ok = false;
                        break;
                    case 4:
                        if (q.isEmpty() != ref.isEmpty()) ok = false;
                        break;
                    default:
                        if (q.isFull() != (ref.size() == k)) ok = false;
                        break;
                }
            }
            if (!ok) fails++;
        }
        System.out.printf("random sequences: %d/500 correct%n", 500 - fails);
    }
}

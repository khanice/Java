import java.util.Arrays;

/*
 * PROBLEM 9: Binary Search -- three parts, escalating. Do them in order.
 *
 * The idea: on SORTED data, compare with the middle element and you can throw
 * away HALF the remaining range every step. n -> n/2 -> n/4 -> ... -> 1 takes
 * about log2(n) steps. For n = 1,000,000 that is 20 comparisons instead of a
 * million. This is the single biggest speedup in basic algorithms.
 *
 * THE TEMPLATE (learn this exact shape -- Part A):
 *
 *     int left = 0, right = n - 1;          // INCLUSIVE range [left, right]
 *     while (left <= right) {               // <=  because [3,3] is non-empty
 *         int mid = left + (right - left) / 2;
 *         if      (a[mid] == target) return mid;
 *         else if (a[mid] <  target) left  = mid + 1;   // answer is to the RIGHT
 *         else                       right = mid - 1;   // answer is to the LEFT
 *     }
 *     return -1;
 *
 * THREE THINGS PEOPLE GET WRONG, every time:
 *
 *   1. mid = (left + right) / 2  OVERFLOWS when left+right exceeds 2^31-1.
 *      This is a REAL bug that sat in the JDK's own binary search for nine
 *      years. Always write:  left + (right - left) / 2
 *
 *   2. `while (left <= right)` with `right = n - 1`. If you use `<` with an
 *      inclusive right, you skip the last element. Pick a convention --
 *      inclusive [left, right] with <= is the one above -- and never mix them.
 *
 *   3. `mid + 1` and `mid - 1`, not `mid`. You already CHECKED mid, so exclude
 *      it. Writing `left = mid` when mid == left gives an INFINITE LOOP.
 *
 * =========================================================================
 * PART A -- classic binary search
 * =========================================================================
 * Return the index of `target` in the sorted array `nums`, or -1 if absent.
 * (Values are distinct in Part A.)
 *
 * =========================================================================
 * PART B -- search insert position ("lower bound")
 * =========================================================================
 * Return the index where `target` IS, or -- if it is absent -- the index where
 * it WOULD BE inserted to keep the array sorted.
 *
 *   [1, 3, 5, 6], target = 5  ->  2     (it is there)
 *   [1, 3, 5, 6], target = 2  ->  1     (goes between 1 and 3)
 *   [1, 3, 5, 6], target = 7  ->  4     (goes at the very end -- past the array!)
 *   [1, 3, 5, 6], target = 0  ->  0     (goes at the front)
 *
 * Same loop as Part A. The trick is what you return when the loop ENDS without
 * finding it. Run the template on [1,3,5,6] with target=2 by hand, on paper,
 * and watch where `left` ends up. That is your answer -- and it is not a
 * coincidence. This is called "lower bound" and it is the most useful binary
 * search variant in practice.
 *
 * =========================================================================
 * PART C -- first and last position of a target (duplicates!)
 * =========================================================================
 * The array is sorted but may contain DUPLICATES. Return int[]{first, last} --
 * the first and last indices where `target` appears -- or {-1, -1} if absent.
 *
 *   [5,7,7,8,8,10], target = 8  ->  [3, 4]
 *   [5,7,7,8,8,10], target = 6  ->  [-1, -1]
 *   [1,1,1,1],      target = 1  ->  [0, 3]
 *
 * MUST be O(log n). Finding one 8 and then walking left/right to the edges is
 * O(n) on an array like [1,1,1,...,1] -- that is the trap, and the harness has
 * a 100000-element all-ones array to catch exactly that.
 *
 * THE KEY INSIGHT: run binary search TWICE, once biased left and once biased
 * right. When you find the target, do NOT return immediately -- RECORD it as
 * your best answer so far, then KEEP SEARCHING in the direction of the edge you
 * want. (For `first`: after recording, keep searching LEFT with right = mid-1.)
 *
 * TIME: A and B are O(log n). C is O(log n) -- two passes is still O(log n).
 *
 * TO RUN:
 *   cd DSA
 *   java Problem7.java
 */
public class Problem9 {

    // ---------------- PART A ----------------
    public static int search(int[] nums, int target) {
        int left=0,right=nums.length-1;
        while(left<=right){
            int mid = left +( right-left)/2;
            if(nums[mid]==target){
                return mid;
            }
            if(nums[mid]>target){
                right = mid-1;
            }else{
                left = mid+1;
            }
        }
        return -1;
    }

    // ---------------- PART B ----------------
    public static int searchInsert(int[] nums, int target) {
        int left=0,right=nums.length-1;
        while (left <= right) {
        int mid = left + (right - left) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] > target) right = mid - 1;
        else left = mid+1;
        }
        return left;
    }

    // ---------------- PART C ----------------
    public static int[] searchRange(int[] nums, int target) {
        int isFirst = findBound(nums, target, true);
        if(isFirst == -1)return new int[]{-1,-1};
        return new int[] {isFirst, findBound(nums, target, false) };
    }
    
    public static int findBound(int[] nums, int target, boolean isFirst) {
        int left = 0, right = nums.length - 1;
        int boundIndex = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                boundIndex = mid;
                if (isFirst) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return boundIndex;
    }

    // ---------------------------------------------------------------
    // Test harness.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("--- PART A: binary search ---");
        checkA(new int[] { -1, 0, 3, 5, 9, 12 }, 9);    // 4
        checkA(new int[] { -1, 0, 3, 5, 9, 12 }, 2);    // -1
        checkA(new int[] { 5 }, 5);                     // 0
        checkA(new int[] { 5 }, -5);                    // -1
        checkA(new int[] { 1, 2 }, 2);                  // 1  -- last element
        checkA(new int[] { }, 1);                       // -1 -- empty array

        System.out.println("\n--- PART B: search insert position ---");
        checkB(new int[] { 1, 3, 5, 6 }, 5);    // 2
        checkB(new int[] { 1, 3, 5, 6 }, 2);    // 1
        checkB(new int[] { 1, 3, 5, 6 }, 7);    // 4  -- past the end
        checkB(new int[] { 1, 3, 5, 6 }, 0);    // 0
        checkB(new int[] { }, 4);               // 0

        System.out.println("\n--- PART C: first and last position ---");
        checkC(new int[] { 5, 7, 7, 8, 8, 10 }, 8);   // [3,4]
        checkC(new int[] { 5, 7, 7, 8, 8, 10 }, 6);   // [-1,-1]
        checkC(new int[] { 1, 1, 1, 1 }, 1);          // [0,3]
        checkC(new int[] { 2, 2 }, 2);                // [0,1]
        checkC(new int[] { }, 0);                     // [-1,-1]
        checkC(new int[] { 1 }, 1);                   // [0,0]

        // O(log n) check: 100k identical values. An O(n) edge-walk still gets
        // the right ANSWER, so only the timing exposes it.
        int[] big = new int[100000];
        Arrays.fill(big, 1);
        long t0 = System.nanoTime();
        int[] r = searchRange(big, 1);
        long us = (System.nanoTime() - t0) / 1000;
        System.out.printf("%n100k all-ones -> %s  (expected [0, 99999])  %s   [%d us]%n",
                Arrays.toString(r),
                (r != null && r.length == 2 && r[0] == 0 && r[1] == 99999) ? "PASS" : "FAIL",
                us);

        // Randomised cross-check against Arrays.binarySearch / linear scan.
        java.util.Random rnd = new java.util.Random(5);
        int fa = 0, fb = 0, fc = 0;
        for (int t = 0; t < 400; t++) {
            int[] a = new int[rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(12);
            Arrays.sort(a);
            int tgt = rnd.nextInt(12);

            int[] distinct = Arrays.stream(a).distinct().toArray();
            int expA = Arrays.binarySearch(distinct, tgt);
            if (search(distinct.clone(), tgt) != (expA < 0 ? -1 : expA)) fa++;

            if (searchInsert(distinct.clone(), tgt) != lowerBound(distinct, tgt)) fb++;

            int[] expC = bruteRange(a, tgt);
            int[] gotC = searchRange(a.clone(), tgt);
            if (gotC == null || gotC.length != 2
                    || gotC[0] != expC[0] || gotC[1] != expC[1]) {
                if (fc++ == 0) {
                    System.out.printf("RANGE FAIL on %s target=%d: got %s, expected %s%n",
                            Arrays.toString(a), tgt,
                            Arrays.toString(gotC), Arrays.toString(expC));
                }
            }
        }
        System.out.printf("%nrandom: A %d/400, B %d/400, C %d/400%n",
                400 - fa, 400 - fb, 400 - fc);
    }

    private static int lowerBound(int[] a, int t) {
        int i = 0;
        while (i < a.length && a[i] < t) i++;
        return i;
    }

    private static int[] bruteRange(int[] a, int t) {
        int first = -1, last = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == t) { if (first == -1) first = i; last = i; }
        }
        return new int[] { first, last };
    }

    private static void checkA(int[] nums, int target) {
        int e = Arrays.binarySearch(nums, target);
        int expected = e < 0 ? -1 : e;
        int got = search(nums.clone(), target);
        System.out.printf("%-24s t=%-4d -> %-4d (expected %-4d) %s%n",
                Arrays.toString(nums), target, got, expected,
                got == expected ? "PASS" : "FAIL");
    }

    private static void checkB(int[] nums, int target) {
        int expected = lowerBound(nums, target);
        int got = searchInsert(nums.clone(), target);
        System.out.printf("%-24s t=%-4d -> %-4d (expected %-4d) %s%n",
                Arrays.toString(nums), target, got, expected,
                got == expected ? "PASS" : "FAIL");
    }

    private static void checkC(int[] nums, int target) {
        int[] expected = bruteRange(nums, target);
        int[] got = searchRange(nums.clone(), target);
        boolean ok = got != null && got.length == 2
                && got[0] == expected[0] && got[1] == expected[1];
        System.out.printf("%-24s t=%-4d -> %-10s (expected %-10s) %s%n",
                Arrays.toString(nums), target,
                Arrays.toString(got), Arrays.toString(expected),
                ok ? "PASS" : "FAIL");
    }
}

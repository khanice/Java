import java.util.Arrays;

/*
 * PROBLEM 3: Container With Most Water
 *
 * You are given an array `height` where height[i] is the height of a vertical
 * line drawn at x-position i. Pick TWO lines which, together with the x-axis,
 * form a container. Return the maximum amount of water it can store.
 *
 * The water is limited by the SHORTER of the two lines (it would spill over
 * the top otherwise). So for lines at indices i and j (i < j):
 *
 *     width  = j - i
 *     depth  = min(height[i], height[j])      <-- the SHORTER line
 *     water  = width * depth
 *
 * Return the largest `water` over all pairs.
 *
 * Example 1:
 *   height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
 *   Output: 49
 *   (lines at index 1 and index 8: width = 8-1 = 7, depth = min(8,7) = 7,
 *    so water = 7 * 7 = 49)
 *
 *          8|  #                   #
 *          7|  #                   #     #
 *          6|  #  #                #     #
 *          5|  #  #     #          #     #
 *          4|  #  #     #  #       #     #
 *          3|  #  #     #  #       #  #  #
 *          2|  #  #  #  #  #       #  #  #
 *          1|# #  #  #  #  #       #  #  #
 *            0 1  2  3  4  5   6   7  8      <- the 1 and 8 hold 49
 *
 * Example 2:
 *   height = [1, 1]
 *   Output: 1        (width = 1, depth = 1)
 *
 * Constraints:
 *   2 <= height.length <= 100000
 *   0 <= height[i] <= 10000
 *
 * NOTE: This asks for the AREA, not the indices. Nothing to keep track of but
 * a running maximum.
 *
 * BRUTE FORCE is O(n^2): try every pair. Correct, but too slow at n = 100000.
 * Your job is O(n) with O(1) space -- two pointers, left at 0, right at the end.
 *
 * THE ONE DECISION:
 *   At each step you compute the area, update your max, and then must move ONE
 *   pointer inward. Which one -- and why?
 *
 *   Use the exact argument from Problem 2. Moving a pointer inward discards
 *   every remaining pair involving it, so you may only move a pointer once you
 *   can prove none of those pairs could beat your current best. Ask yourself:
 *   the SHORTER of the two lines -- could it ever do better than it just did?
 *   Its width will only shrink from here (the pointers close in), and its depth
 *   is capped by its own height no matter how tall a partner it finds. What does
 *   that tell you?
 *
 *   Careful: the greedy instinct "move whichever pointer makes the next area
 *   bigger" is WRONG. Move the one you can PROVE is finished.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem3.java
 */
public class Problem3 {

    public static int maxArea(int[] height) {
        int left=0;
        int right = height.length-1;
        int maxArea = 0;

        while(left<right){
            maxArea = Math.max((right-left)*Math.min(height[right],height[left]), maxArea);
            if(height[left]<height[right]){
                left++;
            }else{
                right--;
            }


        }

        return maxArea;
    }

    // ---------------------------------------------------------------
    // Test harness -- you don't need to edit anything below this line.
    // It checks your answer against a slow-but-obviously-correct brute
    // force, including on random arrays.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check(new int[] { 1, 8, 6, 2, 5, 4, 8, 3, 7 });   // expect 49
        check(new int[] { 1, 1 });                        // expect 1
        check(new int[] { 4, 3, 2, 1, 4 });               // expect 16
        check(new int[] { 1, 2, 1 });                     // expect 2
        check(new int[] { 2, 3, 4, 5, 18, 17, 6 });       // expect 17
        check(new int[] { 0, 0, 5, 5 });                  // zeros present

        // 200 random arrays -- catches greedy mistakes the fixed cases miss.
        java.util.Random rnd = new java.util.Random(42);
        int failures = 0;
        for (int t = 0; t < 200; t++) {
            int[] a = new int[2 + rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(20);
            if (maxArea(a.clone()) != bruteForce(a)) {
                if (failures++ == 0) {
                    System.out.printf("%nRANDOM FAIL on %s: got %d, expected %d%n",
                            Arrays.toString(a), maxArea(a.clone()), bruteForce(a));
                }
            }
        }
        System.out.printf("%nrandom tests: %d/200 passed%n", 200 - failures, 200);
    }

    private static int bruteForce(int[] h) {
        int best = 0;
        for (int i = 0; i < h.length; i++)
            for (int j = i + 1; j < h.length; j++)
                best = Math.max(best, (j - i) * Math.min(h[i], h[j]));
        return best;
    }

    private static void check(int[] height) {
        int expected = bruteForce(height);
        int got = maxArea(height.clone());
        System.out.printf("%-28s -> %-6d (expected %-6d) %s%n",
                Arrays.toString(height), got, expected,
                got == expected ? "PASS" : "FAIL");
    }
}

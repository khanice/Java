import java.util.Arrays;

/*
 * PROBLEM 2: Two Sum II -- Input Array Is Sorted
 *
 * Given an array `numbers` that is ALREADY SORTED in non-decreasing order,
 * find two numbers that add up to `target` and return their indices.
 *
 * - Exactly one valid answer exists.
 * - You may not use the same element twice.
 * - Return the indices in increasing order: [smaller, larger].
 *
 * THE CATCH: You must solve this using O(1) extra space.
 *   That means NO HashMap. Your Problem 1 solution is banned here.
 *
 * Example 1:
 *   numbers = [2, 7, 11, 15], target = 9
 *   Output: [0, 1]           because 2 + 7 == 9
 *
 * Example 2:
 *   numbers = [2, 3, 4], target = 6
 *   Output: [0, 2]           because 2 + 4 == 6
 *
 * Example 3:
 *   numbers = [-5, -3, 0, 1, 8], target = -3
 *   Output: [0, 3]           because -5 + 1 == -3
 *
 * Constraints:
 *   2 <= numbers.length <= 10000
 *   numbers is sorted in non-decreasing order (duplicates allowed)
 *   -10^9 <= numbers[i] <= 10^9
 *
 * THINK ABOUT:
 *   You already wrote this algorithm once, in Problem 1 -- and it was correct
 *   there too. The only thing that broke it was that YOU did the sorting, which
 *   scrambled the indices. Here the array arrives sorted, so the indices are
 *   the real ones. Nothing to scramble.
 *
 *   Before you code: why is it safe to move a pointer inward? When the sum is
 *   too big and you do right--, you just threw away every pair involving that
 *   old `right` value. Convince yourself none of them could have been the
 *   answer. That argument is the whole proof, and it is what an interviewer
 *   will ask you for.
 *
 *   Then: what is the time complexity, and what is the space complexity?
 *
 * TO RUN:
 *   cd DSA
 *   java Problem2.java
 */
public class Problem2 {

    public static int[] twoSumSorted(int[] numbers, int target) {
        int left=0;
        int right = numbers.length-1;
        while(left<right){
            if(numbers[left] + numbers[right] == target){
                return new int[]{left,right};
            }
            if(numbers[left]+numbers[right]<target){
                left++;
            }else{
                right--;
            }
        }
        return new int[] { -1, -1 };
    }

    // ---------------------------------------------------------------
    // Test harness -- you don't need to edit anything below this line.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check(new int[] { 2, 7, 11, 15 }, 9);
        check(new int[] { 2, 3, 4 }, 6);
        check(new int[] { -5, -3, 0, 1, 8 }, -3);   // negatives
        check(new int[] { 1, 1 }, 2);               // duplicates, tiny array
        check(new int[] { 0, 0, 3, 4 }, 0);         // two zeros
        check(new int[] { 1, 2, 3, 4, 9 }, 13);     // answer at the far ends
    }

    private static void check(int[] numbers, int target) {
        int[] got = twoSumSorted(numbers.clone(), target);
        boolean ok = got != null
                && got.length == 2
                && got[0] >= 0 && got[0] < numbers.length
                && got[1] >= 0 && got[1] < numbers.length
                && got[0] < got[1]
                && numbers[got[0]] + numbers[got[1]] == target;

        System.out.printf("%-22s target=%-4d -> %-10s %s%n",
                Arrays.toString(numbers),
                target,
                Arrays.toString(got),
                ok ? "PASS" : "FAIL");
    }
}

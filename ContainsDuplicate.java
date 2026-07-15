import java.util.*;

/**
 * 217. Contains Duplicate
 *
 * Given an integer array nums, return true if any value appears
 * at least twice in the array, and return false if every element is distinct.
 *
 * Example 1:
 *   Input:  nums = [1, 2, 3, 1]
 *   Output: true
 *
 * Example 2:
 *   Input:  nums = [1, 2, 3, 4]
 *   Output: false
 *
 * Example 3:
 *   Input:  nums = [1, 1, 1, 3, 3, 4, 3, 2, 4, 2]
 *   Output: true
 *
 * Constraints:
 *   - 1 <= nums.length <= 10^5
 *   - -10^9 <= nums[i] <= 10^9
 */

public class ContainsDuplicate {

    // =====================
    // Write your solution
    // =====================

    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (seen.contains(num)) {
                return true;
            }
            seen.add(num);
        }
        return false;
    }

    // =====================
    // Test cases
    // =====================

    public static void main(String[] args) {
        ContainsDuplicate sol = new ContainsDuplicate();

        int[][] inputs = {
            {1, 2, 3, 1},
            {1, 2, 3, 4},
            {1, 1, 1, 3, 3, 4, 3, 2, 4, 2},
            {1},
            {-1, -1, 0},
            {0, 0}
        };

        boolean[] expected = { true, false, true, false, true, true };

        for (int i = 0; i < inputs.length; i++) {
            boolean result = sol.containsDuplicate(inputs[i]);
            boolean pass = result == expected[i];
            System.out.println("Test " + (i + 1) + ": " + (pass ? "PASS" : "FAIL")
                + " | Input: " + Arrays.toString(inputs[i])
                + " | Expected: " + expected[i]
                + " | Got: " + result);
        }
    }
}
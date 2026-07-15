import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * PROBLEM 1: Two Sum
 *
 * Given an array of integers `nums` and an integer `target`, return the
 * indices of the two numbers that add up to `target`.
 *
 * - Exactly one valid answer exists.
 * - You may not use the same element twice.
 * - The order of the two indices in your answer does not matter.
 *
 * Example 1:
 *   nums = [2, 7, 11, 15], target = 9
 *   Output: [0, 1]          because nums[0] + nums[1] == 2 + 7 == 9
 *
 * Example 2:
 *   nums = [3, 2, 4], target = 6
 *   Output: [1, 2]          because 2 + 4 == 6  (not [0, 0]!)
 *
 * Example 3:
 *   nums = [3, 3], target = 6
 *   Output: [0, 1]
 *
 * Constraints:
 *   2 <= nums.length <= 10000
 *   -10^9 <= nums[i] <= 10^9
 *   Values may be negative, and may repeat.
 *
 * YOUR TASK:
 *   1. Fill in twoSum() below. A straightforward "check every pair" solution
 *      is a perfectly good first answer -- get it correct first.
 *   2. Then tell me its time complexity, and we'll talk about making it faster.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem-1.java
 */
public class Problem1 {

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> map = new HashMap<>();
        for(int i=0;i<nums.length;i++){
            int curr = target - nums[i];
            if(map.containsKey(curr)){
                return new int[]{map.get(curr),i};
            }
            map.put(nums[i],i);
        }
        
        return new int[] { -1, -1 };
    }

    // ---------------------------------------------------------------
    // Test harness -- you don't need to edit anything below this line.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check(new int[] { 2, 7, 11, 15 }, 9);
        check(new int[] { 3, 2, 4 }, 6);
        check(new int[] { 3, 3 }, 6);
        check(new int[] { -1, -2, -3, -4 }, -7);   // negatives
        check(new int[] { 0, 4, 3, 0 }, 0);        // zeros
    }

    private static void check(int[] nums, int target) {
        int[] got = twoSum(nums.clone(), target);
        boolean ok = got != null
                && got.length == 2
                && got[0] >= 0 && got[0] < nums.length
                && got[1] >= 0 && got[1] < nums.length
                && got[0] != got[1]
                && nums[got[0]] + nums[got[1]] == target;

        System.out.printf("%-22s target=%-4d -> %-10s %s%n",
                Arrays.toString(nums),
                target,
                Arrays.toString(got),
                ok ? "PASS" : "FAIL");
    }
}

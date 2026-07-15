import java.util.*;

/**
 * 49. Group Anagrams
 *
 * Given an array of strings strs, group the anagrams together.
 * You can return the answer in any order.
 *
 * Example 1:
 *   Input:  strs = ["eat","tea","tan","ate","nat","bat"]
 *   Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
 *
 * Example 2:
 *   Input:  strs = [""]
 *   Output: [[""]]
 *
 * Example 3:
 *   Input:  strs = ["a"]
 *   Output: [["a"]]
 *
 * Constraints:
 *   - 1 <= strs.length <= 10^4
 *   - 0 <= strs[i].length <= 100
 *   - strs[i] consists of lowercase English letters
 */

public class GroupAnagram {

    // =====================
    // Write your solution
    // =====================

    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> anagramGroups = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            anagramGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(anagramGroups.values());
    }

    // =====================
    // Test cases
    // =====================

    public static void main(String[] args) {
        GroupAnagram sol = new GroupAnagram();

        String[][] inputs = {
            {"eat", "tea", "tan", "ate", "nat", "bat"},
            {""},
            {"a"},
            {"abc", "bca", "cab", "xyz", "zyx"},
            {"hman", "listen", "silent", "enlist", "google", "goelgo"}
        };

        for (int i = 0; i < inputs.length; i++) {
            List<List<String>> result = sol.groupAnagrams(inputs[i]);

            // Sort inner lists and outer list for consistent comparison
            for (List<String> group : result) Collections.sort(group);
            result.sort(Comparator.comparing(g -> g.get(0)));

            System.out.println("Test " + (i + 1) + ": " + result);
        
            
        }
    }
}
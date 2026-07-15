import java.util.HashSet;
import java.util.Set;

/*
 * PROBLEM 4: Longest Substring Without Repeating Characters
 *
 * Given a string s, return the LENGTH of the longest substring that contains
 * no repeated characters.
 *
 * A "substring" is CONTIGUOUS -- you cannot skip characters.
 * (That makes "abc" a substring of "abcd", but "acd" is NOT.)
 *
 * Example 1:
 *   s = "abcabcbb"   ->  3      the substring "abc"
 * Example 2:
 *   s = "bbbbb"      ->  1      the substring "b"
 * Example 3:
 *   s = "pwwkew"     ->  3      the substring "wke"
 *                               NOT "pwke" -- that skips a 'w', so it is a
 *                               subsequence, not a substring.
 * Example 4:
 *   s = ""           ->  0
 *
 * Constraints:
 *   0 <= s.length() <= 50000
 *   s may contain letters, digits, symbols, spaces.
 *
 * ---------------------------------------------------------------------------
 * THE PATTERN: SLIDING WINDOW
 *
 * Two pointers again -- but this time BOTH walk forward, and the span between
 * them is a "window" over the string:
 *
 *      a  b  c  a  b  c  b  b
 *         [-----------]
 *       left        right
 *
 * The window here is s[left..right], and you maintain an INVARIANT -- a
 * property you promise is always true of the window. Here the invariant is:
 *
 *      "the window contains no duplicate characters"
 *
 * The loop shape is always the same:
 *
 *   for (right = 0 .. n-1):
 *       add s[right] to the window            <- GROW
 *       while (the invariant is broken):      <- SHRINK until it holds again
 *           remove s[left] from the window
 *           left++
 *       record the window size as a candidate answer
 *
 * Grow on the right, shrink on the left, and only ever measure the window when
 * it is valid. Note `left` NEVER moves backward -- which is exactly why this is
 * O(n) and not O(n^2), even with a loop nested inside a loop. Each character is
 * added once and removed at most once, so the total work is 2n. Think about
 * that until it feels obvious; it is the thing people get wrong about this
 * pattern.
 *
 * WHAT YOU NEED:
 *   A way to ask "is character c currently in my window?" in O(1).
 *   That is a HashSet<Character> -- the same O(1)-lookup idea as your HashMap
 *   in Problem 1, minus the values. add(c), remove(c), contains(c).
 *
 * WATCH OUT:
 *   - After the shrink loop, the window is s[left..right] INCLUSIVE.
 *     Its length is right - left + 1. The +1 is the classic off-by-one here.
 *   - The empty string must return 0, not crash.
 *
 * Get s.charAt(i) for the character at index i, and s.length() for the length.
 *
 * TIME/SPACE: aim for O(n) time, O(k) space where k is the alphabet size.
 *
 * TO RUN:
 *   cd DSA
 *   java Problem4.java
 */
public class Problem4 {

    public static int lengthOfLongestSubstring(String s) {
        HashSet<Character> set = new HashSet<>();
        int maxLen = 0;
        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            while (set.contains(c)) {
                set.remove(s.charAt(left));
                left++;

            }
            set.add(c);

            maxLen = Math.max(maxLen, right - left + 1);

        }
        return maxLen;
    }

    // ---------------------------------------------------------------
    // Test harness -- checks you against a brute force, plus random strings.
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        check("abcabcbb"); // 3
        check("bbbbb"); // 1
        check("pwwkew"); // 3
        check(""); // 0
        check("a"); // 1
        check("au"); // 2
        check("dvdf"); // 3 <- the one that catches a common bug
        check("tmmzuxt"); // 5
        check("abba"); // 2 <- and so does this one

        java.util.Random rnd = new java.util.Random(7);
        int failures = 0;
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(12);
            for (int i = 0; i < len; i++)
                sb.append((char) ('a' + rnd.nextInt(4)));
            String r = sb.toString();
            if (lengthOfLongestSubstring(r) != bruteForce(r)) {
                if (failures++ == 0) {
                    System.out.printf("%nRANDOM FAIL on \"%s\": got %d, expected %d%n",
                            r, lengthOfLongestSubstring(r), bruteForce(r));
                }
            }
        }
        System.out.printf("%nrandom tests: %d/300 passed%n", 300 - failures, 300);
    }

    private static int bruteForce(String s) {
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = i; j < s.length(); j++) {
                if (!seen.add(s.charAt(j)))
                    break;
                best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    private static void check(String s) {
        int expected = bruteForce(s);
        int got = lengthOfLongestSubstring(s);
        System.out.printf("%-12s -> %-4d (expected %-4d) %s%n",
                "\"" + s + "\"", got, expected,
                got == expected ? "PASS" : "FAIL");
    }
}

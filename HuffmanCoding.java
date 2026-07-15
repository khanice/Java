import java.util.*;

/**
 * Huffman coding: builds an optimal prefix-free binary code for a piece of
 * text based on character frequency, then encodes and decodes with it.
 */
public class HuffmanCoding {

    // A node in the Huffman tree. Leaves hold a real character; internal
    // nodes hold only a combined frequency.
    static class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        Node(int freq, Node left, Node right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.freq, other.freq);
        }
    }

    // Repeatedly merge the two lowest-frequency nodes until one tree remains.
    public static Node buildTree(Map<Character, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Edge case: text has only one distinct character.
        if (pq.size() == 1) {
            Node only = pq.poll();
            return new Node(only.freq, only, null);
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.add(new Node(left.freq + right.freq, left, right));
        }

        return pq.poll();
    }

    // Walk the tree; left edges append '0', right edges append '1'.
    public static void buildCodes(Node node, String path, Map<Character, String> codes) {
        if (node == null) return;
        if (node.isLeaf()) {
            codes.put(node.ch, path.isEmpty() ? "0" : path); // single-symbol edge case
            return;
        }
        buildCodes(node.left, path + "0", codes);
        buildCodes(node.right, path + "1", codes);
    }

    public static String encode(String text, Map<Character, String> codes) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(codes.get(c));
        }
        return sb.toString();
    }

    public static String decode(String encoded, Node root) {
        StringBuilder sb = new StringBuilder();
        Node current = root;
        for (char bit : encoded.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.isLeaf()) {
                sb.append(current.ch);
                current = root;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String text = "abracadabra";

        // 1. Count frequencies
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.merge(c, 1, Integer::sum);
        }

        // 2. Build the tree, then derive codes from it
        Node root = buildTree(freqMap);
        Map<Character, String> codes = new TreeMap<>();
        buildCodes(root, "", codes);

        System.out.println("Frequencies: " + freqMap);
        System.out.println("Codes:");
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }

        // 3. Encode
        String encoded = encode(text, codes);
        System.out.println();
        System.out.println("Original:  " + text + " (" + (text.length() * 8) + " bits as 8-bit ASCII)");
        System.out.println("Encoded:   " + encoded + " (" + encoded.length() + " bits)");

        // 4. Decode to verify the round trip
        String decoded = decode(encoded, root);
        System.out.println("Decoded:   " + decoded);
        System.out.println("Round-trip successful: " + decoded.equals(text));
    }
}
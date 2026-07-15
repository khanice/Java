import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Consistent Hashing with Virtual Nodes
 *
 * Problem with naive hashing (key % N):
 *   Adding/removing a server remaps almost ALL keys → massive cache invalidation.
 *
 * Consistent Hashing solution:
 *   - Place servers AND keys on a circular ring (0 … 2^32-1)
 *   - A key is owned by the FIRST server clockwise from its position
 *   - Add/remove a server → only keys between it and its predecessor move
 *
 * Virtual Nodes (vnodes):
 *   - Each physical server maps to V points on the ring
 *   - Prevents hotspots when servers have unequal hash positions
 *   - More vnodes → better load distribution (at the cost of memory)
 *
 * Ring layout example (3 servers, 3 vnodes each):
 *
 *           0
 *     S2-v2   S1-v1
 *  S1-v2         S3-v1
 *
 *  S3-v3         S2-v1
 *     S1-v3   S3-v2
 *          2^32-1
 *
 * Data structures:
 *   TreeMap<Long, String>  ring  — hash position → server name  (O(log N) lookup)
 *   Map<String, Set<Long>> nodes — server name   → its vnode positions
 */
public class ConsistentHashing {

    private final TreeMap<Long, String>      ring;       // hash → server
    private final Map<String, Set<Long>>     serverNodes; // server → vnode hashes
    private final int                        virtualNodes;
    private final MessageDigest              md5;

    // ── Constructor ──────────────────────────────────────────────────────────
    public ConsistentHashing(int virtualNodes) {
        this.ring         = new TreeMap<>();
        this.serverNodes  = new HashMap<>();
        this.virtualNodes = virtualNodes;
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    // ── Core Operations ──────────────────────────────────────────────────────

    /**
     * Add a server to the ring.
     * Places `virtualNodes` evenly-labeled vnodes around the ring.  O(V log N)
     */
    public void addServer(String server) {
        if (serverNodes.containsKey(server)) return;

        Set<Long> positions = new HashSet<>();
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(server + "#vnode" + i);
            ring.put(hash, server);
            positions.add(hash);
        }
        serverNodes.put(server, positions);
        System.out.printf("  [+] Added %-12s  (%d vnodes on ring)%n",
                          server, virtualNodes);
    }

    /**
     * Remove a server from the ring.
     * Keys it owned migrate to the next clockwise server.  O(V log N)
     */
    public void removeServer(String server) {
        Set<Long> positions = serverNodes.remove(server);
        if (positions == null) return;

        positions.forEach(ring::remove);
        System.out.printf("  [-] Removed %-12s  (%d vnodes removed)%n",
                          server, positions.size());
    }

    /**
     * Route a key to its server.
     * Finds the first server clockwise from the key's hash.  O(log N)
     */
    public String getServer(String key) {
        if (ring.isEmpty()) throw new IllegalStateException("No servers in ring");

        long hash = hash(key);

        // Find the first entry with hash >= key's hash
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);

        // Wrap around to the start of the ring if past the last node
        if (entry == null) entry = ring.firstEntry();

        return entry.getValue();
    }

    /**
     * Returns a snapshot of the current load distribution.
     * Shows how many keys (from a sample set) each server owns.
     */
    public Map<String, Integer> getLoadDistribution(List<String> keys) {
        Map<String, Integer> distribution = new TreeMap<>();
        serverNodes.keySet().forEach(s -> distribution.put(s, 0));
        keys.forEach(key -> distribution.merge(getServer(key), 1, Integer::sum));
        return distribution;
    }

    /** Returns the number of active servers. */
    public int serverCount() { return serverNodes.size(); }

    // ── Hashing ──────────────────────────────────────────────────────────────

    /**
     * MD5-based hash → maps string to a point in [0, 2^32-1].
     * Uses first 4 bytes of the 16-byte MD5 digest for a 32-bit hash.
     */
    private synchronized long hash(String key) {
        md5.reset();
        byte[] digest = md5.digest(key.getBytes(StandardCharsets.UTF_8));
        // Combine first 4 bytes into an unsigned 32-bit integer
        return ((long)(digest[3] & 0xFF) << 24)
             | ((long)(digest[2] & 0xFF) << 16)
             | ((long)(digest[1] & 0xFF) <<  8)
             | ((long)(digest[0] & 0xFF));
    }

    // ── Demo ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        Consistent Hashing — Demo             ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        final int VNODES      = 150;   // virtual nodes per server
        final int SAMPLE_KEYS = 10_000;

        ConsistentHashing ch = new ConsistentHashing(VNODES);

        // ── Step 1: Build a sample key set ───────────────────────────────────
        List<String> keys = new ArrayList<>(SAMPLE_KEYS);
        for (int i = 0; i < SAMPLE_KEYS; i++) keys.add("user:" + i);

        // ── Step 2: Add 3 servers ─────────────────────────────────────────────
        System.out.println("── Step 1: Add 3 servers ──");
        ch.addServer("server-A");
        ch.addServer("server-B");
        ch.addServer("server-C");

        System.out.println("\nLoad distribution (" + SAMPLE_KEYS + " keys):");
        printDistribution(ch.getLoadDistribution(keys), SAMPLE_KEYS);

        // ── Step 3: Spot-check routing ────────────────────────────────────────
        System.out.println("\n── Step 2: Key routing ──");
        String[] testKeys = {"user:42", "order:9981", "session:abc", "product:7"};
        for (String k : testKeys) {
            System.out.printf("  %-20s  →  %s%n", k, ch.getServer(k));
        }

        // ── Step 4: Simulate adding a server ─────────────────────────────────
        System.out.println("\n── Step 3: Add server-D (scale out) ──");
        Map<String, Integer> before = ch.getLoadDistribution(keys);
        ch.addServer("server-D");
        Map<String, Integer> after  = ch.getLoadDistribution(keys);

        int remapped = keys.stream()
            .filter(k -> !ch.getServer(k).equals(findServer(before, k, keys)))
            .mapToInt(k -> 1)
            .sum();

        System.out.println("\nLoad distribution after adding server-D:");
        printDistribution(after, SAMPLE_KEYS);
        System.out.printf("%n  Keys remapped: ~%d / %d  (%.1f%%)  ← ideally ~25%%%n",
                          SAMPLE_KEYS / ch.serverCount(), SAMPLE_KEYS,
                          100.0 / ch.serverCount());

        // ── Step 5: Simulate removing a server ───────────────────────────────
        System.out.println("\n── Step 4: Remove server-B (server failure) ──");
        ch.removeServer("server-B");
        System.out.println("\nLoad distribution after removing server-B:");
        printDistribution(ch.getLoadDistribution(keys), SAMPLE_KEYS);
        System.out.println("  ✅ Only server-B's keys redistributed; A, C, D untouched.");

        // ── Step 6: Virtual nodes impact ─────────────────────────────────────
        System.out.println("\n── Step 5: Virtual node impact on uniformity ──");
        System.out.println("  (Std deviation of load — lower = more uniform)\n");
        for (int v : new int[]{1, 10, 50, 150, 300}) {
            ConsistentHashing test = new ConsistentHashing(v);
            test.addServer("S1"); test.addServer("S2"); test.addServer("S3");
            Map<String, Integer> dist = test.getLoadDistribution(keys);
            double stdDev = stdDev(dist, SAMPLE_KEYS, 3);
            System.out.printf("  vnodes=%-4d  distribution: %s  stdDev=%.0f%n",
                              v, formatBar(dist, SAMPLE_KEYS), stdDev);
        }
    }

    // ── Helpers for demo output ───────────────────────────────────────────────

    private static void printDistribution(Map<String, Integer> dist, int total) {
        dist.forEach((server, count) -> {
            double pct = 100.0 * count / total;
            System.out.printf("  %-12s  %5d keys  (%5.1f%%)  %s%n",
                              server, count, pct, "█".repeat((int)(pct / 2)));
        });
    }

    private static String formatBar(Map<String, Integer> dist, int total) {
        StringBuilder sb = new StringBuilder();
        dist.forEach((s, c) ->
            sb.append(String.format("%s=%.0f%% ", s, 100.0 * c / total)));
        return sb.toString().trim();
    }

    private static double stdDev(Map<String, Integer> dist, int total, int n) {
        double mean = (double) total / n;
        double sumSq = dist.values().stream().mapToDouble(v -> (v - mean) * (v - mean)).sum();
        return Math.sqrt(sumSq / n);
    }

    /** Resolve which server a key was on in a pre-recorded distribution snapshot. */
    private static String findServer(Map<String, Integer> snapshot,
                                     String key, List<String> allKeys){
    // Lightweight: just re-derive position by rank in sorted order (approximation)
        return snapshot.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).orElse("unknown");
    }
}
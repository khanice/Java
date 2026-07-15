import java.util.HashMap;

public class LRUCache {
    private static class Node{
        int key;
        int value;
        Node prev;
        Node next;
 
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private HashMap<Integer, Node> map;
    private Node head;
    private Node tail;

    public LRUCache(int capacity){
        if(capacity <= 0)throw new IllegalArgumentException("Capacity must be Positive");
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
        head = new Node(0,0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key){
        Node node = map.get(key);
        if(node != null){
            moveToFront(node);
            return node.value;
        }
        return -1;
    }
    public void put(int key , int value){
        Node node  = map.get(key);
        if(node !=null){
            node.value = value;
            moveToFront(node);
        }else{
            Node newNode = new Node(key,value);
            map.put(key, newNode);
            addToFront(newNode);
            if(map.size() > capacity){
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
        }
    }
     /** Detach node from its current position in the list. */
    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
 
    /** Insert node right after the dummy head (most-recent position). */
    private void addToFront(Node node) {
        node.next      = head.next;
        node.prev      = head;
        head.next.prev = node;
        head.next      = node;
    }
 
    /** Remove then re-insert — effectively marks node as most-recently used. */
    private void moveToFront(Node node) {
        remove(node);
        addToFront(node);
    }

     public static void main(String[] args) {
        System.out.println("=== LRU Cache Demo (capacity = 3) ===\n");
 
        LRUCache cache = new LRUCache(3);
 
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        System.out.println("After put(1,10), put(2,20), put(3,30)");
        // List: 3 <-> 2 <-> 1
 
        System.out.println("get(1) → " + cache.get(1));  // 10  | List: 1 <-> 3 <-> 2
        System.out.println("get(2) → " + cache.get(2));  // 20  | List: 2 <-> 1 <-> 3
 
        cache.put(4, 40);   // evicts key 3 (LRU)
        System.out.println("\nAfter put(4,40)  — key 3 should be evicted");
        System.out.println("get(3) → " + cache.get(3));  // -1
        System.out.println("get(4) → " + cache.get(4));  // 40
        System.out.println("get(1) → " + cache.get(1));  // 10
        System.out.println("get(2) → " + cache.get(2));  // 20
 
        cache.put(5, 50);   // evicts key 4 (LRU after above gets)
        System.out.println("\nAfter put(5,50)  — key 4 should be evicted");
        System.out.println("get(4) → " + cache.get(4));  // -1
        System.out.println("get(5) → " + cache.get(5));  // 50
 
        System.out.println("\n=== Update existing key ===");
        cache.put(1, 99);
        System.out.println("After put(1,99)");
        System.out.println("get(1) → " + cache.get(1));  // 99
    }

}

import java.util.Arrays;

public class Main {
    private static void showList(MyList list) {
        list.add("A");
        list.add("C");
        list.add(1, "B");
        list.addAll(new Object[]{"D", "E"});
        list.addAll(2, new Object[]{"X", "Y"});
        System.out.println("After add: " + Arrays.toString(list.toArray()));
        System.out.println("get(1): " + list.get(1));
        list.set(1, "BB");
        System.out.println("remove(2): " + list.remove(2));
        System.out.println("indexOf(Y): " + list.indexOf("Y"));
        System.out.println("size: " + list.size());
        System.out.println("Result: " + Arrays.toString(list.toArray()));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("MyArrayList");
        showList(new MyArrayList());
        System.out.println("\nMyLinkedList");
        showList(new MyLinkedList());

        System.out.println("\nMyLinkedHashSet");
        MyLinkedHashSet set = new MyLinkedHashSet();
        set.add("Java");
        set.add("Python");
        System.out.println("Add Java again: " + set.add("Java"));
        set.add("C++");
        System.out.println(Arrays.toString(set.toArray()));
        set.remove("Python");
        System.out.println("After remove: " + Arrays.toString(set.toArray()));

        System.out.println("\nMyCache: capacity=2, TTL=1000 ms");
        MyCache cache = new MyCache(2, 1000);
        cache.put("A", 10);
        cache.put("B", 20);
        System.out.println("get(A): " + cache.get("A"));
        cache.put("C", 30);
        System.out.println("B after eviction: " + cache.get("B"));
        Thread.sleep(1100);
        System.out.println("A after expiry: " + cache.get("A"));
        System.out.println("size: " + cache.size());
        try {
            cache.put(null, 40);
        } catch (NullPointerException e) {
            System.out.println("Null key: NullPointerException");
        }
    }
}

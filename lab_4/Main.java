import java.util.Arrays;
import java.util.Objects;

public class Main {
    public static <T> void copy(MyList<? extends T> source, MyList<? super T> target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        MyArrayList<T> snapshot = new MyArrayList<>();
        for (int i = 0; i < source.size(); i++) snapshot.add(source.get(i));
        for (int i = 0; i < snapshot.size(); i++) target.add(snapshot.get(i));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("ТИПІЗОВАНІ СПИСКИ");
        MyList<String> words = new MyArrayList<>();
        words.add("Java");
        words.addAll(new String[]{"Generics", "Collections"});
        String word = words.get(0);
        System.out.println("MyArrayList<String>: " + Arrays.toString(words.toArray()));
        System.out.println("String word = get(0): " + word);
        MyList<String> linked = new MyLinkedList<>();
        linked.addAll(words);
        linked.add(1, "List");
        linked.set(1, "LinkedList");
        System.out.println("remove(2): " + linked.remove(2));
        System.out.println("MyLinkedList<String>: " + Arrays.toString(linked.toArray()));

        System.out.println("\nWILDCARD: ? extends E");
        MyList<Integer> integers = new MyArrayList<>();
        integers.addAll(new Integer[]{10, 20, 30});
        MyList<Number> numbers = new MyLinkedList<>();
        numbers.add(1.5);
        numbers.addAll(integers);
        System.out.println("Integer -> Number: " + Arrays.toString(numbers.toArray()));
        MyList<Object> objects = new MyArrayList<>();
        copy(numbers, objects); 
        System.out.println("Number -> Object: " + Arrays.toString(objects.toArray()));

        System.out.println("\nТИПІЗОВАНА МНОЖИНА");
        MyLinkedHashSet<String> set = new MyLinkedHashSet<>();
        set.addAll(words);
        System.out.println("Повторне додавання Java: " + set.add("Java"));
        System.out.println("MyLinkedHashSet<String>: " + Arrays.toString(set.toArray()));

        System.out.println("\nТИПІЗОВАНИЙ КЕШ: MyCache<String, Integer>");
        MyCache<String, Integer> cache = new MyCache<>(2, 1000);
        cache.put("A", 10);
        cache.put("B", 20);
        Integer value = cache.get("A");
        System.out.println("Integer value = get(A): " + value);
        cache.put("C", 30);
        System.out.println("B після LRU: " + cache.get("B"));
        Thread.sleep(1100);
        System.out.println("A після TTL: " + cache.get("A"));
        System.out.println("Розмір кешу: " + cache.size());

    }
}

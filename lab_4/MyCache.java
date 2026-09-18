import java.util.Objects;
import java.util.function.LongSupplier;

public class MyCache<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        long writtenAt;
        Entry<K, V> previous, next;
        Entry(K key, V value, long writtenAt) {
            this.key = key;
            this.value = value;
            this.writtenAt = writtenAt;
        }
    }

    private final int capacity;
    private final long ttlNanos;
    private final LongSupplier clock;
    private Entry<K, V> first, last;
    private int size;

    public MyCache(int capacity, long ttlMillis) {
        this(capacity, ttlMillis, System::nanoTime);
    }

    MyCache(int capacity, long ttlMillis, LongSupplier clock) {
        if (capacity <= 0 || ttlMillis <= 0)
            throw new IllegalArgumentException("Capacity and TTL must be positive");
        this.capacity = capacity;
        this.ttlNanos = Math.multiplyExact(ttlMillis, 1_000_000L);
        this.clock = Objects.requireNonNull(clock);
    }

    private Entry<K, V> find(K key) {
        for (Entry<K, V> e = first; e != null; e = e.next)
            if (Objects.equals(e.key, key)) return e;
        return null;
    }

    private void unlink(Entry<K, V> e) {
        if (e.previous == null) first = e.next;
        else e.previous.next = e.next;
        if (e.next == null) last = e.previous;
        else e.next.previous = e.previous;
        e.previous = e.next = null;
        size--;
    }

    private void append(Entry<K, V> e) {
        e.previous = last;
        if (last == null) first = e;
        else last.next = e;
        last = e;
        size++;
    }

    private void removeExpired() {
        long now = clock.getAsLong();
        Entry<K, V> e = first;
        while (e != null) {
            Entry<K, V> next = e.next;
            if (now - e.writtenAt >= ttlNanos) unlink(e);
            e = next;
        }
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        removeExpired();
        Entry<K, V> e = find(key);
        if (e != null) {
            unlink(e);
            e.value = value;
            e.writtenAt = clock.getAsLong();
        } else {
            if (size == capacity) unlink(first);
            e = new Entry<>(key, value, clock.getAsLong());
        }
        append(e);
    }

    public V get(K key) {
        Objects.requireNonNull(key, "Key must not be null");
        removeExpired();
        Entry<K, V> e = find(key);
        if (e == null) return null;
        unlink(e);
        append(e); 
        return e.value;
    }

    public V remove(K key) {
        Objects.requireNonNull(key, "Key must not be null");
        removeExpired();
        Entry<K, V> e = find(key);
        if (e == null) return null;
        unlink(e);
        return e.value;
    }

    public int size() {
        removeExpired();
        return size;
    }
}

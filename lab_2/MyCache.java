import java.util.Objects;
import java.util.function.LongSupplier;

public class MyCache {
    private static class Entry {
        Object key, value;
        long writtenAt;
        Entry previous, next;
        Entry(Object key, Object value, long writtenAt) {
            this.key = key;
            this.value = value;
            this.writtenAt = writtenAt;
        }
    }

    private final int capacity;
    private final long ttlNanos;
    private final LongSupplier clock;
    private Entry first, last;
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

    private Entry find(Object key) {
        for (Entry e = first; e != null; e = e.next)
            if (Objects.equals(e.key, key)) return e;
        return null;
    }

    private void unlink(Entry e) {
        if (e.previous == null) first = e.next;
        else e.previous.next = e.next;
        if (e.next == null) last = e.previous;
        else e.next.previous = e.previous;
        e.previous = e.next = null;
        size--;
    }

    private void append(Entry e) {
        e.previous = last;
        if (last == null) first = e;
        else last.next = e;
        last = e;
        size++;
    }

    private void removeExpired() {
        long now = clock.getAsLong();
        Entry e = first;
        while (e != null) {
            Entry next = e.next;
            if (now - e.writtenAt >= ttlNanos) unlink(e);
            e = next;
        }
    }

    public void put(Object key, Object value) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(value, "Value must not be null");
        removeExpired();
        Entry e = find(key);
        if (e != null) {
            unlink(e);
            e.value = value;
            e.writtenAt = clock.getAsLong();
        } else {
            if (size == capacity) unlink(first);
            e = new Entry(key, value, clock.getAsLong());
        }
        append(e);
    }

    public Object get(Object key) {
        Objects.requireNonNull(key, "Key must not be null");
        removeExpired();
        Entry e = find(key);
        if (e == null) return null;
        unlink(e);
        append(e); 
        return e.value;
    }

    public Object remove(Object key) {
        Objects.requireNonNull(key, "Key must not be null");
        removeExpired();
        Entry e = find(key);
        if (e == null) return null;
        unlink(e);
        return e.value;
    }

    public int size() {
        removeExpired();
        return size;
    }
}

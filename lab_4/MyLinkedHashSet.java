import java.util.Objects;

public class MyLinkedHashSet<E> {
    private static class Node<E> {
        E value;
        Node<E> bucketNext; 
        Node<E> previous, next; 
        Node(E value) { this.value = value; }
    }

    private Node<E>[] buckets = newTable(16);
    private Node<E> first, last;
    private int size;

    private int bucketIndex(Object value) {
        int hash = value == null ? 0 : value.hashCode();
        return (hash & 0x7fffffff) % buckets.length;
    }

    public boolean contains(Object value) {
        for (Node<E> n = buckets[bucketIndex(value)]; n != null; n = n.bucketNext)
            if (Objects.equals(n.value, value)) return true;
        return false;
    }

    private void resize() {
        buckets = newTable(buckets.length * 2);
        for (Node<E> n = first; n != null; n = n.next) {
            int index = bucketIndex(n.value);
            n.bucketNext = buckets[index];
            buckets[index] = n;
        }
    }

    public boolean add(E value) {
        if (contains(value)) return false;
        if (size + 1 > buckets.length * 0.75) resize();
        int index = bucketIndex(value);
        Node<E> node = new Node<>(value);
        node.bucketNext = buckets[index];
        buckets[index] = node;
        node.previous = last;
        if (last == null) first = node;
        else last.next = node;
        last = node;
        size++;
        return true;
    }

    public boolean remove(Object value) {
        int index = bucketIndex(value);
        Node<E> previousInBucket = null;
        Node<E> node = buckets[index];
        while (node != null) {
            if (Objects.equals(node.value, value)) {
                if (previousInBucket == null) buckets[index] = node.bucketNext;
                else previousInBucket.bucketNext = node.bucketNext;
                if (node.previous == null) first = node.next;
                else node.previous.next = node.next;
                if (node.next == null) last = node.previous;
                else node.next.previous = node.previous;
                size--;
                return true;
            }
            previousInBucket = node;
            node = node.bucketNext;
        }
        return false;
    }

    public int size() { return size; }

    public Object[] toArray() {
        Object[] result = new Object[size];
        Node<E> node = first;
        for (int i = 0; i < size; i++, node = node.next)
            result[i] = node.value;
        return result;
    }

    @SuppressWarnings("unchecked")
    private Node<E>[] newTable(int capacity) {
        return (Node<E>[]) new Node<?>[capacity];
    }

    public void addAll(MyList<? extends E> source) {
        Objects.requireNonNull(source);
        for (int i = 0; i < source.size(); i++) add(source.get(i));
    }
}

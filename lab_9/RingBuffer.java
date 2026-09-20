import java.util.Objects;

public class RingBuffer<E> {
    private static class Node<E> {
        E value;
        Node<E> next;
    }

    private final int slots;
    private Node<E> head;
    private Node<E> tail;
    private int headIndex;
    private int tailIndex;

    public RingBuffer(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive");
        slots = Math.addExact(capacity, 1);
        head = new Node<>();
        Node<E> current = head;
        for (int i = 1; i < slots; i++) {
            current.next = new Node<>();
            current = current.next;
        }
        current.next = head;
        tail = head;
    }

    public synchronized void put(E value) throws InterruptedException {
        Objects.requireNonNull(value);
        while ((tailIndex + 1) % slots == headIndex) {
            wait();
        }
        tail.value = value;
        tail = tail.next;
        tailIndex = (tailIndex + 1) % slots;
        notifyAll();
    }

    public synchronized E take() throws InterruptedException {
        while (headIndex == tailIndex) {
            wait();
        }
        E value = head.value;
        head.value = null;
        head = head.next;
        headIndex = (headIndex + 1) % slots;
        notifyAll();
        return value;
    }

    public synchronized int size() {
        return (tailIndex - headIndex + slots) % slots;
    }
}

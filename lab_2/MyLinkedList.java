import java.util.Objects;

public class MyLinkedList implements MyList {
    private static class Node {
        Object value;
        Node previous, next;
        Node(Object value) { this.value = value; }
    }

    private Node first, last;
    private int size;

    private Node nodeAt(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index);
        if (index < size / 2) {
            Node node = first;
            for (int i = 0; i < index; i++) node = node.next;
            return node;
        }
        Node node = last;
        for (int i = size - 1; i > index; i--) node = node.previous;
        return node;
    }

    public void add(Object e) { add(size, e); }

    public void add(int index, Object element) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        Node next = index == size ? null : nodeAt(index);
        Node previous = next == null ? last : next.previous;
        Node node = new Node(element);
        node.previous = previous;
        node.next = next;
        if (previous == null) first = node;
        else previous.next = node;
        if (next == null) last = node;
        else next.previous = node;
        size++;
    }

    public void addAll(Object[] c) { addAll(size, c); }

    public void addAll(int index, Object[] c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        Objects.requireNonNull(c);
        for (Object element : c) add(index++, element);
    }

    public Object get(int index) { return nodeAt(index).value; }

    public Object remove(int index) {
        Node node = nodeAt(index);
        if (node.previous == null) first = node.next;
        else node.previous.next = node.next;
        if (node.next == null) last = node.previous;
        else node.next.previous = node.previous;
        size--;
        return node.value;
    }

    public void set(int index, Object element) {
        nodeAt(index).value = element;
    }

    public int indexOf(Object o) {
        Node node = first;
        for (int i = 0; node != null; i++, node = node.next)
            if (Objects.equals(node.value, o)) return i;
        return -1;
    }

    public int size() { return size; }

    public Object[] toArray() {
        Object[] result = new Object[size];
        Node node = first;
        for (int i = 0; i < size; i++, node = node.next)
            result[i] = node.value;
        return result;
    }
}

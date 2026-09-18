import java.util.Objects;

public class MyLinkedList<E> implements MyList<E> {
    private static class Node<E> {
        E value;
        Node<E> previous, next;
        Node(E value) { this.value = value; }
    }

    private Node<E> first, last;
    private int size;

    private Node<E> nodeAt(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index);
        if (index < size / 2) {
            Node<E> node = first;
            for (int i = 0; i < index; i++) node = node.next;
            return node;
        }
        Node<E> node = last;
        for (int i = size - 1; i > index; i--) node = node.previous;
        return node;
    }

    public void add(E e) { add(size, e); }

    public void add(int index, E element) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        Node<E> next = index == size ? null : nodeAt(index);
        Node<E> previous = next == null ? last : next.previous;
        Node<E> node = new Node<>(element);
        node.previous = previous;
        node.next = next;
        if (previous == null) first = node;
        else previous.next = node;
        if (next == null) last = node;
        else next.previous = node;
        size++;
    }

    public void addAll(E[] c) { addAll(size, c); }

    public void addAll(int index, E[] c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        Objects.requireNonNull(c);
        for (E element : c) add(index++, element);
    }

    public E get(int index) { return nodeAt(index).value; }

    public E remove(int index) {
        Node<E> node = nodeAt(index);
        if (node.previous == null) first = node.next;
        else node.previous.next = node.next;
        if (node.next == null) last = node.previous;
        else node.next.previous = node.previous;
        size--;
        return node.value;
    }

    public void set(int index, E element) {
        nodeAt(index).value = element;
    }

    public int indexOf(Object o) {
        Node<E> node = first;
        for (int i = 0; node != null; i++, node = node.next)
            if (Objects.equals(node.value, o)) return i;
        return -1;
    }

    public int size() { return size; }

    public Object[] toArray() {
        Object[] result = new Object[size];
        Node<E> node = first;
        for (int i = 0; i < size; i++, node = node.next)
            result[i] = node.value;
        return result;
    }

    public void addAll(MyList<? extends E> source) {
        addAll(size, source);
    }

    public void addAll(int index, MyList<? extends E> source) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        Objects.requireNonNull(source);
        MyArrayList<E> copy = new MyArrayList<>();
        for (int i = 0; i < source.size(); i++) copy.add(source.get(i));
        for (int i = 0; i < copy.size(); i++) add(index++, copy.get(i));
    }
}

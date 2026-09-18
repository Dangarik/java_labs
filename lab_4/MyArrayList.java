import java.util.RandomAccess;
import java.util.Objects;

public class MyArrayList<E> implements MyList<E>, RandomAccess {
    private Object[] data = new Object[10];
    private int size;

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index);
    }

    private void checkPosition(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
    }

    private void grow(int needed) {
        if (needed <= data.length) return;
        int capacity = Math.max(needed, data.length * 2);
        Object[] newData = new Object[capacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    public void add(E e) { add(size, e); }

    public void add(int index, E element) {
        checkPosition(index);
        grow(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    public void addAll(E[] c) { addAll(size, c); }

    public void addAll(int index, E[] c) {
        checkPosition(index);
        Objects.requireNonNull(c);
        grow(size + c.length);
        System.arraycopy(data, index, data, index + c.length, size - index);
        System.arraycopy(c, 0, data, index, c.length);
        size += c.length;
    }

    public E get(int index) {
        checkIndex(index);
        return elementAt(index);
    }

    public E remove(int index) {
        checkIndex(index);
        E removed = elementAt(index);
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null;
        return removed;
    }

    public void set(int index, E element) {
        checkIndex(index);
        data[index] = element;
    }

    public int indexOf(Object o) {
        for (int i = 0; i < size; i++)
            if (Objects.equals(data[i], o)) return i;
        return -1;
    }

    public int size() { return size; }

    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(data, 0, result, 0, size);
        return result;
    }


    @SuppressWarnings("unchecked")
    private E elementAt(int index) { return (E) data[index]; }

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

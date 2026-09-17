import java.util.RandomAccess;
import java.util.Objects;

public class MyArrayList implements MyList, RandomAccess {
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

    public void add(Object e) { add(size, e); }

    public void add(int index, Object element) {
        checkPosition(index);
        grow(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    public void addAll(Object[] c) { addAll(size, c); }

    public void addAll(int index, Object[] c) {
        checkPosition(index);
        Objects.requireNonNull(c);
        grow(size + c.length);
        System.arraycopy(data, index, data, index + c.length, size - index);
        System.arraycopy(c, 0, data, index, c.length);
        size += c.length;
    }

    public Object get(int index) {
        checkIndex(index);
        return data[index];
    }

    public Object remove(int index) {
        checkIndex(index);
        Object removed = data[index];
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null;
        return removed;
    }

    public void set(int index, Object element) {
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
}

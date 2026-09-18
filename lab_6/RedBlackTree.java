import java.util.Arrays;

public class RedBlackTree {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class Node {
        int value;
        boolean color = RED;
        Node left, right, parent;
        Node(int value) { this.value = value; }
    }

    private Node root;
    private int size;

    private boolean isRed(Node node) { return node != null && node.color == RED; }

    public boolean add(int value) {
        Node parent = null;
        Node current = root;
        while (current != null) {
            parent = current;
            if (value < current.value) current = current.left;
            else if (value > current.value) current = current.right;
            else return false;
        }
        Node node = new Node(value);
        node.parent = parent;
        if (parent == null) root = node;
        else if (value < parent.value) parent.left = node;
        else parent.right = node;
        size++;
        fixAfterAdd(node);
        return true;
    }

    private void fixAfterAdd(Node node) {
        while (node != root && isRed(node.parent)) {
            Node parent = node.parent;
            Node grand = parent.parent;
            if (parent == grand.left) {
                Node uncle = grand.right;
                if (isRed(uncle)) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grand.color = RED;
                    node = grand;
                } else {
                    if (node == parent.right) {
                        node = parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = grand.left;
                if (isRed(uncle)) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grand.color = RED;
                    node = grand;
                } else {
                    if (node == parent.left) {
                        node = parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void rotateLeft(Node node) {
        Node right = node.right;
        node.right = right.left;
        if (right.left != null) right.left.parent = node;
        right.parent = node.parent;
        if (node.parent == null) root = right;
        else if (node == node.parent.left) node.parent.left = right;
        else node.parent.right = right;
        right.left = node;
        node.parent = right;
    }

    private void rotateRight(Node node) {
        Node left = node.left;
        node.left = left.right;
        if (left.right != null) left.right.parent = node;
        left.parent = node.parent;
        if (node.parent == null) root = left;
        else if (node == node.parent.left) node.parent.left = left;
        else node.parent.right = left;
        left.right = node;
        node.parent = left;
    }

    public boolean contains(int value) {
        Node current = root;
        while (current != null) {
            if (value == current.value) return true;
            current = value < current.value ? current.left : current.right;
        }
        return false;
    }

    public int[] toArray() {
        int[] result = new int[size];
        fill(root, result, 0);
        return result;
    }

    private int fill(Node node, int[] result, int index) {
        if (node == null) return index;
        index = fill(node.left, result, index);
        result[index++] = node.value;
        return fill(node.right, result, index);
    }

    public int size() { return size; }
    public int height() { return height(root); }
    private int height(Node node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    public void printTree() {
        System.out.println("B — чорний, R — червоний; L — лівий, P — правий нащадок");
        if (root == null) { System.out.println("Дерево порожнє"); return; }
        System.out.println(root.value + (root.color == RED ? " [R]" : " [B]"));
        printChildren(root, "");
    }

    private void printChildren(Node node, String prefix) {
        if (node.left != null) {
            boolean last = node.right == null;
            printNode(node.left, prefix, last, "L: ");
        }
        if (node.right != null) printNode(node.right, prefix, true, "P: ");
    }
    private void printNode(Node node, String prefix, boolean last, String side) {
        System.out.println(prefix + (last ? "└── " : "├── ") + side
                + node.value + (node.color == RED ? " [R]" : " [B]"));
        printChildren(node, prefix + (last ? "    " : "│   "));
    }

    public boolean isValid() {
        if (root == null) return size == 0;
        if (root.color != BLACK || root.parent != null) return false;
        return blackHeight(root, null, Long.MIN_VALUE, Long.MAX_VALUE) > 0
                && count(root) == size;
    }
    private int blackHeight(Node node, Node parent, long min, long max) {
        if (node == null) return 1;
        if (node.parent != parent || node.value <= min || node.value >= max) return -1;
        if (isRed(node) && (isRed(node.left) || isRed(node.right))) return -1;
        int left = blackHeight(node.left, node, min, node.value);
        int right = blackHeight(node.right, node, node.value, max);
        if (left < 0 || left != right) return -1;
        return left + (node.color == BLACK ? 1 : 0);
    }
    private int count(Node node) {
        return node == null ? 0 : 1 + count(node.left) + count(node.right);
    }
    public void show() {
        printTree();
        System.out.println("Симетричний обхід: " + Arrays.toString(toArray()));
        System.out.println("Вузлів: " + size + ", висота: " + height());
        System.out.println("Правила дерева виконуються: " + isValid());
    }
}

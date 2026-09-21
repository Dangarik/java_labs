import java.lang.reflect.Field;
import java.util.Scanner;

public class ReflectionDemo {
    public static void change(String target, String replacement) throws ReflectiveOperationException {
        Field value = String.class.getDeclaredField("value");
        value.setAccessible(true);
        if (value.getType() == char[].class) {
            value.set(target, replacement.toCharArray());
        } else if (value.getType() == byte[].class) {
            Field coder = String.class.getDeclaredField("coder");
            coder.setAccessible(true);
            byte[] bytes = ((byte[]) value.get(replacement)).clone();
            byte encoding = coder.getByte(replacement);
            value.set(target, bytes);
            coder.setByte(target, encoding);
        } else {
            throw new IllegalStateException("Unsupported String storage");
        }
        Field hash = String.class.getDeclaredField("hash");
        hash.setAccessible(true);
        hash.setInt(target, 0);
        try {
            Field hashIsZero = String.class.getDeclaredField("hashIsZero");
            hashIsZero.setAccessible(true);
            hashIsZero.setBoolean(target, false);
        } catch (NoSuchFieldException e) {
        }
    }
    public static void run(Scanner scanner, I18n ui) throws ReflectiveOperationException {
        String literal = "Lab10 literal example";
        System.out.println(ui.text("literal"));
        System.out.println(ui.text("before", literal));
        change(literal, "Changed literal");
        System.out.println(ui.text("after", literal));
        System.out.println(ui.text("input.string"));
        String entered = scanner.nextLine();
        System.out.println(ui.text("input.replacement"));
        String replacement = scanner.nextLine();
        System.out.println(ui.text("before", entered));
        change(entered, replacement);
        System.out.println(ui.text("after", entered));
    }
}

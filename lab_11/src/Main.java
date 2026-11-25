import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);

    private static final String EMPLOYEES = """
            SELECT e.id AS 'ID', e.last_name AS 'Прізвище', e.first_name AS "Ім'я",
                   COALESCE(e.position, '-') AS 'Посада', d.name AS 'Відділ'
            FROM employees e
            JOIN departments d ON e.department_id = d.id
            """;

    private static final String TASKS = """
            SELECT t.id AS 'ID', t.description AS 'Опис завдання',
                   e.id AS 'ID співробітника',
                   e.last_name || ' ' || e.first_name AS 'Співробітник'
            FROM tasks t
            JOIN employees e ON t.employee_id = e.id
            """;

    public static void main(String[] args) {
        try (Connection connection = Database.connect()) {
            Database.initialize(connection);
            System.out.println("З'єднання з SQLite встановлено.");
            runMenu(connection);
        } catch (IOException | SQLException e) {
            System.err.println("Не вдалося відкрити базу даних: " + e.getMessage());
        }
    }

    private static void runMenu(Connection connection) {
        while (true) {
            System.out.println("""

                    1. Усі співробітники
                    2. Усі завдання
                    3. Співробітники зазначеного відділу
                    4. Додати завдання співробітнику
                    5. Завдання зазначеного співробітника
                    6. Видалити співробітника
                    0. Вихід
                    """);
            String choice = readLine("Ваш вибір: ");
            if (choice == null || choice.equals("0")) {
                return;
            }

            try {
                switch (choice) {
                    case "1" -> printQuery(connection, EMPLOYEES + " ORDER BY e.id");
                    case "2" -> printQuery(connection, TASKS + " ORDER BY t.id");
                    case "3" -> showDepartmentEmployees(connection);
                    case "4" -> addTask(connection);
                    case "5" -> showEmployeeTasks(connection);
                    case "6" -> deleteEmployee(connection);
                    default -> System.out.println("Оберіть пункт від 0 до 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Номер має бути цілим додатним числом.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.out.println("Помилка бази даних: " + e.getMessage());
            }
        }
    }

    private static void showDepartmentEmployees(Connection connection) throws SQLException {
        printQuery(connection, "SELECT id AS 'ID', name AS 'Відділ', "
                + "COALESCE(phone, '-') AS 'Телефон' FROM departments ORDER BY id");
        int id = readId("Номер відділу: ");
        if (!exists(connection, "SELECT id FROM departments WHERE id = ?", id)) {
            System.out.println("Відділ не знайдено.");
            return;
        }
        printQuery(connection, EMPLOYEES + " WHERE e.department_id = ? ORDER BY e.id", id);
    }

    private static void addTask(Connection connection) throws SQLException {
        int id = readId("Номер співробітника: ");
        if (!employeeExists(connection, id)) {
            return;
        }
        String description = readLine("Опис завдання (до 50 символів): ");
        if (description == null || description.isBlank() || description.length() > 50) {
            System.out.println("Введіть опис довжиною від 1 до 50 символів.");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO tasks (description, employee_id) VALUES (?, ?)")) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
            System.out.println("Завдання додано.");
        }
    }

    private static void showEmployeeTasks(Connection connection) throws SQLException {
        int id = readId("Номер співробітника: ");
        if (employeeExists(connection, id)) {
            printQuery(connection, TASKS + " WHERE t.employee_id = ? ORDER BY t.id", id);
        }
    }

    private static void deleteEmployee(Connection connection) throws SQLException {
        int id = readId("Номер співробітника для видалення: ");
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM employees WHERE id = ?")) {
            statement.setInt(1, id);
            int count = statement.executeUpdate();
            System.out.println(count > 0
                    ? "Співробітника та його завдання видалено."
                    : "Співробітника не знайдено.");
        }
    }

    private static boolean employeeExists(Connection connection, int id) throws SQLException {
        if (exists(connection, "SELECT id FROM employees WHERE id = ?", id)) {
            return true;
        }
        System.out.println("Співробітника не знайдено.");
        return false;
    }

    private static boolean exists(Connection connection, String sql, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    // Один метод друкує результат будь-якого SELECT-запиту.
    private static void printQuery(Connection connection, String sql, int... ids) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < ids.length; i++) {
                statement.setInt(i + 1, ids[i]);
            }
            try (ResultSet result = statement.executeQuery()) {
                ResultSetMetaData columns = result.getMetaData();
                int count = columns.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    System.out.print((i == 1 ? "" : " | ") + columns.getColumnLabel(i));
                }
                System.out.println();
                boolean found = false;
                while (result.next()) {
                    found = true;
                    for (int i = 1; i <= count; i++) {
                        System.out.print((i == 1 ? "" : " | ") + result.getString(i));
                    }
                    System.out.println();
                }
                if (!found) {
                    System.out.println("Записів немає.");
                }
            }
        }
    }

    private static int readId(String prompt) {
        String value = readLine(prompt);
        if (value == null) {
            throw new IllegalArgumentException("Введення завершено.");
        }
        int id = Integer.parseInt(value);
        if (id <= 0) {
            throw new IllegalArgumentException("Номер має бути цілим додатним числом.");
        }
        return id;
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return input.hasNextLine() ? input.nextLine().trim() : null;
    }
}

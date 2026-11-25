import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Database {
    public static Connection connect() throws IOException, SQLException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of("db.properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        String url = properties.getProperty("db.url");
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IOException("У db.properties потрібно вказати db.url=jdbc:sqlite:lab11.db");
        }

        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            // SQLite потребує явного ввімкнення зовнішніх ключів.
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    public static void initialize(Connection connection) throws IOException, SQLException {
        connection.setAutoCommit(false);
        try {
            executeFile(connection, "schema.sql");
            // Початкові дані додаються лише до порожньої бази.
            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM departments")) {
                if (result.next() && result.getInt(1) == 0) {
                    executeFile(connection, "data.sql");
                }
            }
            connection.commit();
        } catch (IOException | SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void executeFile(Connection connection, String file) throws IOException, SQLException {
        String sql = Files.readString(Path.of(file), StandardCharsets.UTF_8);
        try (Statement statement = connection.createStatement()) {
            for (String query : sql.split(";")) {
                if (!query.isBlank()) {
                    statement.executeUpdate(query);
                }
            }
        }
    }
}

package app.computer_school.system.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance; // Единственный экземпляр класса
    private Connection connection; // Объект соединения с БД
    private final String host;
    private final String port;
    private final String url;
    private final String user;
    private final String password;
    private final String db;

    private DatabaseConnection() throws SQLException {
        Dotenv dotenv = Dotenv.load();

        // Получаем параметры подключения из .env
        this.user = dotenv.get("DATABASE_USER");
        this.password = dotenv.get("DATABASE_PASSWORD");
        this.host = dotenv.get("DATABASE_HOST");
        this.port = dotenv.get("DATABASE_PORT");
        this.db = dotenv.get("DATABASE_DB");

        this.url = String.format(
                "jdbc:postgresql://%s:%s/%s",
                this.host,
                this.port,
                this.db
        );

        if (this.url == null || this.user == null || this.password == null) {
            throw new SQLException("Не удалось загрузить параметры подключения из .env файла.");
        }

        try {
            this.connection = DriverManager.getConnection(url, user, password);

            System.out.println("Подключение к базе данных успешно установлено.");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());

            throw e;
        }
    }

    /**
     * Возвращает единственный экземпляр класса DatabaseConnection.
     * Если экземпляр еще не создан, он создается.
     *
     * @return Единственный экземпляр DatabaseConnection
     * @throws SQLException если произошла ошибка при создании соединения
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Возвращает текущее соединение с базой данных.
     *
     * @return Объект Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Закрывает соединение с базой данных.
     * После закрытия соединения, следующий вызов getInstance() создаст новое.
     *
     * @throws SQLException если произошла ошибка при закрытии соединения
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Соединение с базой данных закрыто.");
        }
        instance = null; // Сбросить ссылку на закрытое соединение
    }
}
package app.computer_school.system.database;

import app.computer_school.system.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAL {

    // Метод для поиска всех записей
    public static <T> List<T> findAll(IModelMapper<T> mapper) throws SQLException {
        String tableName = mapper.getTableName();
        String sql = "SELECT * FROM " + tableName;

        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                T model = mapper.fromResultSet(rs); // Используем маппер для создания объекта
                results.add(model);
            }
        }
        return results;
    }

    // Метод для поиска одной записи по ID
    public static <T> T getById(Object id, IModelMapper<T> mapper) throws SQLException {
        String tableName = mapper.getTableName();
        String idColumn = mapper.getIdColumn();

        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id); // Устанавливаем ID как параметр запроса
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper.fromResultSet(rs); // Если запись найдена, создаём модель
            }
        }
        return null; // Если запись не найдена, возвращаем null
    }

    // Метод для сохранения (вставка или обновление)
    public static <T> void save(T model, IModelMapper<T> mapper) throws SQLException {
        Object idValue = mapper.getIdValue(model);
        if (idValue == null) {
            insert(model, mapper);
        } else {
            update(model, mapper);
        }
    }

    private static <T> void insert(T model, IModelMapper<T> mapper) throws SQLException {
        String tableName = mapper.getTableName();
        String[] columnNames = mapper.getColumnNames();
        Object[] values = mapper.toValuesArray(model);

        // Строим SQL: INSERT INTO table (col1, col2) VALUES (?, ?)
        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(", ", columnNames))
                .append(") VALUES (");
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    mapper.setIdValue(model, newId); // Устанавливаем ID через маппер
                }
            }
        }
    }

    private static <T> void update(T model, IModelMapper<T> mapper) throws SQLException {
        String tableName = mapper.getTableName();
        String[] columnNames = mapper.getColumnNames();
        Object[] values = mapper.toValuesArray(model);
        String idColumn = mapper.getIdColumn();
        Object idValue = mapper.getIdValue(model);

        // Строим SQL: UPDATE table SET col1 = ?, col2 = ? WHERE id = ?
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(tableName)
                .append(" SET ");
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append(columnNames[i]).append(" = ?");
        }
        sql.append(" WHERE ").append(idColumn).append(" = ?");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
            stmt.setObject(values.length + 1, idValue); // Последним параметром - ID

            stmt.executeUpdate();
        }
    }

    // Метод для удаления
    public static <T> void delete(T model, IModelMapper<T> mapper) throws SQLException {
        Object idValue = mapper.getIdValue(model);
        if (idValue == null) return; // Нечего удалять

        String tableName = mapper.getTableName();
        String idColumn = mapper.getIdColumn();

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, idValue);
            stmt.executeUpdate();
        }
        mapper.setIdValue(model, null); // Сбрасываем ID через маппер
    }

    // --- МЕТОД ДЛЯ РАБОТЫ С ТРАНЗАКЦИЯМИ ---
    // Функциональный интерфейс для блока кода транзакции
    @FunctionalInterface
    public interface TransactionBlock {
        void execute() throws SQLException; // Код, который будет выполняться в транзакции
    }

    // Метод для выполнения блока кода в транзакции
    public static void executeInTransaction(TransactionBlock block) throws SQLException {
        Connection conn = null;
        boolean initialAutoCommit = true; // Сохраняем начальное состояние

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            initialAutoCommit = conn.getAutoCommit(); // Сохраняем текущий режим autocommit
            conn.setAutoCommit(false); // Отключаем автокоммит -> начинаем транзакцию

            block.execute(); // Выполняем переданный блок кода

            conn.commit(); // Если блок выполнился без ошибок - фиксируем транзакцию
            System.out.println("Транзакция успешно завершена (committed).");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Если произошла ошибка - откатываем транзакцию
                    System.out.println("Транзакция откачена (rolled back) из-за ошибки: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    // Лучше пробросить первоначальное исключение
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e; // Пробрасываем исходное исключение дальше
        } finally {
            if (conn != null) {
                conn.setAutoCommit(initialAutoCommit); // Восстанавливаем начальный режим autocommit
                // Закрытие Connection здесь не требуется, так как DatabaseConnection - Singleton
                // и управление соединением лежит на нём.
            }
        }
    }
    // --- КОНЕЦ МЕТОДА ДЛЯ РАБОТЫ С ТРАНЗАКЦИЯМИ ---
}
// app/computer_school/system/database/DBAL.java
package app.computer_school.system.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAL {

    public static <T> List<T> findAll(IModelMapper<T> mapper) throws SQLException {
        String tableName = mapper.getTableName();
        String sql = "SELECT * FROM " + tableName;

        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                T model = mapper.fromResultSet(rs);
                results.add(model);
            }
        }
        return results;
    }

    public static <T> T getById(Object id, IModelMapper<T> mapper) throws SQLException {
        String tableName = mapper.getTableName();
        String idColumn = mapper.getIdColumn();

        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper.fromResultSet(rs);
            }
        }
        return null;
    }

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
                    mapper.setIdValue(model, newId);
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
            stmt.setObject(values.length + 1, idValue);

            stmt.executeUpdate();
        }
    }

    public static <T> void delete(T model, IModelMapper<T> mapper) throws SQLException {
        Object idValue = mapper.getIdValue(model);
        if (idValue == null) return;

        String tableName = mapper.getTableName();
        String idColumn = mapper.getIdColumn();

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, idValue);
            stmt.executeUpdate();
        }
        mapper.setIdValue(model, null);
    }

    @FunctionalInterface
    public interface TransactionBlock {
        void execute() throws SQLException;
    }

    public static void executeInTransaction(TransactionBlock block) throws SQLException {
        Connection conn = null;
        boolean initialAutoCommit = true;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            initialAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            block.execute();

            conn.commit();
            System.out.println("Транзакция успешно завершена (committed).");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Транзакция откачена (rolled back) из-за ошибки: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(initialAutoCommit);
            }
        }
    }
}
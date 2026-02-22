package app.computer_school.system.database;

import app.computer_school.models.Model;
import app.computer_school.system.database.DBAL;
import app.computer_school.system.database.IModelMapper;
import app.computer_school.system.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap; // LinkedHashMap сохраняет порядок вставки

public class QueryBuilder<T> {
    private final Class<T> modelClass;
    private final IModelMapper<? extends Model> mapper;
    private final String tableName;

    // --- Параметры запроса ---
    private final StringBuilder whereClause = new StringBuilder();
    private final List<Object> whereParams = new ArrayList<>(); // Для безопасной передачи значений в PreparedStatement
    private String orderByClause = "";
    private Integer limitValue = null;
    // --- --- --- --- --- --- ---

    public QueryBuilder(Class<T> modelClass, IModelMapper<? extends Model> mapper) {
        this.modelClass = modelClass;
        this.mapper = mapper;
        this.tableName = mapper.getTableName();
    }

    // Метод для добавления условия WHERE
    public QueryBuilder<T> where(String column, String operator, Object value) {
        if (whereClause.length() > 0) {
            whereClause.append(" AND "); // Если уже есть условия, добавляем AND
        }
        whereClause.append(column).append(" ").append(operator).append(" ?"); // ? - место для параметра
        whereParams.add(value); // Сохраняем значение параметра
        return this; // Возвращаем this для цепочки вызовов
    }

    // Метод для добавления условия WHERE с IN
    public QueryBuilder<T> whereIn(String column, List<Object> values) {
        if (values == null || values.isEmpty()) {
            // Если список пуст, условие не имеет смысла, можно пропустить или добавить невозможное условие
            // Просто пропустим для этого примера
            return this;
        }

        if (whereClause.length() > 0) {
            whereClause.append(" AND ");
        }

        // Строим строку (?, ?, ?) в зависимости от размера списка
        StringBuilder inClause = new StringBuilder("(");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) inClause.append(", ");
            inClause.append("?");
        }
        inClause.append(")");
        whereClause.append(column).append(" IN ").append(inClause);

        // Добавляем все значения в список параметров
        whereParams.addAll(values);
        return this;
    }

    // Метод для добавления сортировки ORDER BY
    public QueryBuilder<T> orderBy(String column, String direction) {
        // Простая проверка безопасности для direction, чтобы избежать SQL-инъекции
        if ("ASC".equalsIgnoreCase(direction) || "DESC".equalsIgnoreCase(direction)) {
            this.orderByClause = "ORDER BY " + column + " " + direction;
        } else {
            // Логируем или игнорируем недопустимое значение
            System.err.println("Недопустимое направление сортировки: " + direction + ". Игнорируется.");
        }
        return this;
    }

    // Метод для добавления лимита LIMIT
    public QueryBuilder<T> limit(int limit) {
        this.limitValue = limit;
        return this;
    }

    // --- Метод для выполнения запроса SELECT ---
    public List<T> find() throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);

        // Добавляем WHERE, если есть условия
        if (whereClause.length() > 0) {
            sql.append(" WHERE ").append(whereClause);
        }

        // Добавляем ORDER BY, если указан
        if (!orderByClause.isEmpty()) {
            sql.append(" ").append(orderByClause);
        }

        // Добавляем LIMIT, если указан
        if (limitValue != null) {
            sql.append(" LIMIT ").append(limitValue);
        }

        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Устанавливаем параметры в PreparedStatement
            for (int i = 0; i < whereParams.size(); i++) {
                stmt.setObject(i + 1, whereParams.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                T model = (T)mapper.fromResultSet(rs); // Используем маппер для создания модели
                results.add(model);
            }
        }
        return results;
    }

    // --- Метод для выполнения запроса COUNT ---
    public int count() throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName);

        // Добавляем WHERE, если есть условия
        if (whereClause.length() > 0) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Устанавливаем параметры
            for (int i = 0; i < whereParams.size(); i++) {
                stmt.setObject(i + 1, whereParams.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Возвращаем результат COUNT(*)
            }
        }
        return 0; // Если запрос не вернул результат (маловероятно для COUNT)
    }

    // --- Метод для получения одной записи ---
    public T first() throws SQLException {
        List<T> results = this.limit(1).find(); // Устанавливаем LIMIT 1 и выполняем find
        return results.isEmpty() ? null : results.get(0); // Возвращаем первый элемент или null
    }
}
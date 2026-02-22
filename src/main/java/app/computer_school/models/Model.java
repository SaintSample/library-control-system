package app.computer_school.models;

import app.computer_school.system.database.QueryBuilder;
import app.computer_school.system.database.DBAL;
import app.computer_school.system.database.IModelMapper;

import java.sql.SQLException;
import java.util.List;

public abstract class Model {
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // --- АБСТРАКТНЫЙ МЕТОД ---
    // Каждая модель обязана предоставить свой маппер
    public abstract IModelMapper<? extends Model> getMapper();

    public abstract Class<? extends Model> getClassInstance();
    // --- КОНЕЦ АБСТРАКТНОГО МЕТОДА ---

    // --- НОВЫЙ СТАТИЧЕСКИЙ МЕТОД ---
    // Метод для инициализации QueryBuilder для текущего класса модели
    // Он вызывает getMapper() у конкретной модели
    public static <T extends Model> QueryBuilder<T> query(Class<T> classReference) {
        try {
            // Создаём временный экземпляр модели, чтобы вызвать getMapper()
            // Это может быть неэффективно, если конструктор модели делает что-то тяжёлое.
            // Альтернатива - хранить маппер в статическом поле или использовать рефлексию для поиска.
            // Но для простоты и чистоты с абстракцией, этот способ рабочий.
            T tempInstance = classReference.getDeclaredConstructor().newInstance();
            @SuppressWarnings("unchecked") // Приведение к ModelMapper<T> после newInstance
            IModelMapper<T> mapper = tempInstance.getMapper();
            return new QueryBuilder<>(classReference, mapper);
        } catch (Exception e) {
            // Обработка исключений конструктора/инициализации
            throw new RuntimeException("Ошибка при создании маппера для " + classReference.getSimpleName(), e);
        }
    }
    // --- КОНЕЦ НОВОГО МЕТОДА ---

    // Статический метод для поиска по ID
    public static <T extends Model> T findById(Long id, Class<T> clazz) throws SQLException {
        try {
            T tempInstance = clazz.getDeclaredConstructor().newInstance();
            @SuppressWarnings("unchecked")
            IModelMapper<T> mapper = (IModelMapper<T>) tempInstance.getMapper();
            return DBAL.getById(id, mapper);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании маппера для " + clazz.getSimpleName(), e);
        }
    }

    // Статический метод для получения всех
    public static <T extends Model> List<T> all(Class<T> clazz) throws SQLException {
        try {
            T tempInstance = clazz.getDeclaredConstructor().newInstance();
            @SuppressWarnings("unchecked")
            IModelMapper<T> mapper = (IModelMapper<T>) tempInstance.getMapper();
            return DBAL.findAll(mapper);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании маппера для " + clazz.getSimpleName(), e);
        }
    }

    // Методы экземпляра для save и delete
    public void save() throws SQLException {
        @SuppressWarnings("unchecked")
        IModelMapper<Model> mapper = (IModelMapper<Model>) this.getMapper();
        DBAL.save(this, mapper);
    }

    public void delete() throws SQLException {
        @SuppressWarnings("unchecked")
        IModelMapper<Model> mapper = (IModelMapper<Model>) this.getMapper();
        DBAL.delete(this, mapper);
    }
}

// app/computer_school/system/models/Model.java
package app.computer_school.models;

import app.computer_school.system.database.DBAL;
import app.computer_school.system.database.IModelMapper;
import app.computer_school.system.database.QueryBuilder;

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
    public abstract IModelMapper<? extends Model> getMapper();

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
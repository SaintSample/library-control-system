package app.computer_school.system.database;

import app.computer_school.models.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IModelMapper<T> {
    T fromResultSet(ResultSet rs) throws SQLException;
    Object[] toValuesArray(T model);
    String[] getColumnNames();
    String getTableName();
    String getIdColumn();
    Object getIdValue(T model);
    void setIdValue(T model, Object id);
}

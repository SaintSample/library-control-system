package app.computer_school.system.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IModelMapper<T> {
    public T fromResultSet(ResultSet rs) throws SQLException;
    public Object[] toValuesArray(T model);
    public String[] getColumnNames();
    public String getTableName();
    public String getIdColumn();
    public Object getIdValue(T model);
    public void setIdValue(T model, Object id);
}

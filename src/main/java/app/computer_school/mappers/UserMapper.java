package app.computer_school.mappers;

import app.computer_school.models.User;
import app.computer_school.system.database.IModelMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements IModelMapper<User> {
    @Override
    public User fromResultSet(ResultSet rs) throws SQLException {

    }

    @Override
    public Object[] toValuesArray(User model) {
        return new Object[0];
    }

    @Override
    public String[] getColumnNames() {
        return new String[0];
    }

    @Override
    public String getTableName() {
        return "";
    }

    @Override
    public String getIdColumn() {
        return "";
    }

    @Override
    public Object getIdValue(User model) {
        return null;
    }

    @Override
    public void setIdValue(User model, Object id) {

    }
}

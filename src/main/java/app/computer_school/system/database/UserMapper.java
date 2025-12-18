package app.computer_school.system.database;

import app.computer_school.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements IModelMapper<User> {
    @Override
    public User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("id"));

        return user;
    }

    @Override
    public Object[] toValuesArray(User model) {
        return new Object[]{model.getBitrthDate()};
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

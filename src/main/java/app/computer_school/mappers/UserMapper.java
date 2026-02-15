package app.computer_school.mappers;

import app.computer_school.models.User;
import app.computer_school.system.database.IModelMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements IModelMapper<User> {
    @Override
    public User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("id"));
        user.setFirstname(rs.getString("firstname"));
        user.setLastname(rs.getString("lastname"));
        user.setMiddlename(rs.getString("middlename"));
        user.setBitrthDate(rs.getString("birth_date"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));

        return user;
    }

    @Override
    public Object[] toValuesArray(User model) {
        return new Object[]{
                model.getId(),
                model.getFirstname(),
                model.getLastname(),
                model.getMiddlename(),
                model.getBitrthDate(),
                model.getPhone(),
                model.getEmail(),
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
                "id",
                "firstname",
                "lastname",
                "middlename",
                "birth_date",
                "phone",
                "email",
        };
    }

    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    public String getIdColumn() {
        return "id";
    }

    @Override
    public Object getIdValue(User model) {
        return model.getId();
    }

    @Override
    public void setIdValue(User model, Object id) {
        model.setId(new Long((long)id));
    }
}

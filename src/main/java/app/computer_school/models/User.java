package app.computer_school.models;

import app.computer_school.mappers.UserMapper;
import app.computer_school.system.database.DBAL;
import app.computer_school.system.database.IModelMapper;
import app.computer_school.system.database.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class User extends Model {
    protected Long id;
    protected String firstname;
    protected String lastname;
    protected String middlename;
    protected String bitrthDate;
    protected String phone;
    protected String email;

    public User() {}

    public User(
            Long id,
            String firstname,
            String lastname,
            String middlename,
            String bitrthDate,
            String phone,
            String email
    ) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.bitrthDate = bitrthDate;
        this.phone = phone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public IModelMapper<? extends Model> getMapper() {
        return new UserMapper(); // Возвращает свой маппер
    }

    public static QueryBuilder<User> query() {
        User tempInstance = new User(); // Создаём временный экземпляр
        @SuppressWarnings("unchecked")
        IModelMapper<User> mapper = (IModelMapper<User>) tempInstance.getMapper(); // Получаем маппер
        return new QueryBuilder<>(User.class, mapper); // Возвращаем QueryBuilder
    }

    public static User findById(Long id) throws SQLException {
        User tempInstance = new User();
        @SuppressWarnings("unchecked")
        IModelMapper<User> mapper = (IModelMapper<User>) tempInstance.getMapper();
        return DBAL.getById(id, mapper);
    }

    public static List<User> all() throws SQLException {
        User tempInstance = new User();
        @SuppressWarnings("unchecked")
        IModelMapper<User> mapper = (IModelMapper<User>) tempInstance.getMapper();
        return DBAL.findAll(mapper);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getBitrthDate() {
        return bitrthDate;
    }

    public void setBitrthDate(String bitrthDate) {
        this.bitrthDate = bitrthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s %s",
                this.getLastname(),
                this.getFirstname(),
                this.getMiddlename()
        );
    }
}

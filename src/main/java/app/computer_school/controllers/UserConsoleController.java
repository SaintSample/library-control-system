package app.computer_school.controllers;

import app.computer_school.models.User;
import app.computer_school.system.database.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class UserConsoleController {
    public void run() throws SQLException {
        try {
            QueryBuilder<User> builder = User.query();

            List<User> result = builder.find();

            for (User user : result) {
                System.out.println(user);
            }
        } catch (Throwable exception) {
            System.out.println(exception.getMessage());
        }
    }
}

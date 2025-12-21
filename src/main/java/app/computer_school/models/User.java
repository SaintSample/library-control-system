package app.computer_school.models;

public class User {
    protected Long id;
    protected String firstname;
    protected String lastname;
    protected String middlename;
    protected String bitrthDate;
    protected String phone;
    protected String email;

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
}

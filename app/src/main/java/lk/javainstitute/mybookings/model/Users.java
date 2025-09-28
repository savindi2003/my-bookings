package lk.javainstitute.mybookings.model;

public class Users {

    private String fname;
    private String lname;
    private String email;
    private String mobile;
    private String password;

    public Users(String fname, String lname, String email, String mobile, String password) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
    }

    public Users() {
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

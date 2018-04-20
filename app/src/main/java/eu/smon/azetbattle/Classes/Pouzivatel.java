package eu.smon.azetbattle.Classes;

public class Pouzivatel {
    public String name;
    public String email;
    public String token;

    public Pouzivatel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Pouzivatel(String name, String email, String token) {
        this.name = name;
        this.email = email;
        this.token = token;
    }
}

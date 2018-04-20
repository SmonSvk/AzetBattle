package eu.smon.azetbattle.Classes;

public class Pouzivatel {
    public String name;
    public String email;

    public Pouzivatel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Pouzivatel(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

package eu.smon.azetbattle.Classes;

public class ZapisanieDoRady {

    public String doktorID;
    public String poradie;


    public ZapisanieDoRady() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ZapisanieDoRady(String doktorID, String poradie) {
        this.doktorID = doktorID;
        this.poradie = poradie;
    }
}

package Entities;

/**
 * Organizer is a class that participates in events and also plans them.
 */
public class Organizer extends Attendee{
    public String role = "Volunteer";

    public Organizer(String username,String password){
        super(username, password);
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }
}

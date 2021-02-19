package Entities;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendee is a class that participates in events and signs up for them
 */
public class Attendee extends User {

    private List<String> eventAttending;
    private boolean VIP;

    /**
     * Class Constructor specifying Attendee's username and password
     */
    public Attendee(String username, String password){
        super(username, password);
        this.eventAttending = new ArrayList<>();
        this.VIP = false;
    }

    /**
     * Getter for the eventTitles that this attendee attend
     * @return list of eventTitles that this attendee attend
     */
    public List<String> getEventAttending() {
        return eventAttending;
    }

    /**
     * Setter for the eventTitles that this Attendee attend
     * @param eventAttending the new list of eventTitles that this Attendee attend
     */
    public void setEventAttending(List<String> eventAttending) {
        this.eventAttending = eventAttending;
    }

    /**
     * Getter for the the vip status of this Attendee
     * @return true iff it is a vip attendee
     */
    public boolean isVIP() {
        return VIP;
    }

    /**
     * Setter for the the vip status of this Attendee
     * @param VIP whether or not to set this Attendee to be vip
     */
    public void setVIP(boolean VIP) {
        this.VIP = VIP;
    }

}

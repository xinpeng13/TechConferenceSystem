package Entities;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A Message in our program. It stores related info such as sender/time.
 * <p>
 * All info stored is final and cannot be modified.
 * @author William Wang
 */
public class Message implements Serializable {
    private final String senderUsername;
    // I removed the receiver since if we have chats, the chat will include all members of the chat
    private final LocalDateTime time;
    private final String content;

    /**
     * Creates a new Message
     * @param senderUsername the username of the sender
     * @param time the date/time of the message
     * @param content the literal string representing the message
     */
    public Message(String senderUsername, LocalDateTime time, String content){
        this.senderUsername = senderUsername;
        this.time = time;
        this.content = content;
    }

    /**
     * getter for the sender username
     * @return The username of a user who sent the message
     */
    public String getSenderUsername(){
        return senderUsername;
    }

    /**
     * getter for the time
     * @return The time stamp for this message
     */
    public LocalDateTime getTimeStamp(){
        return time;
    }

    /**
     * getter for the message content
     * @return The string representing the message
     */
    public String getContent(){
        return content;
    }

    /**
     * Check message type
     * @return true iff message is an image message, false otherwise.
     */
    public boolean isImageMessage() {
        return false;
    }

    /**
     * Get the image string for this class, if it has one.
     * @return The image string if it exists. Null otherwise
     */
    public String getImageString() {
        return null;
    }


}

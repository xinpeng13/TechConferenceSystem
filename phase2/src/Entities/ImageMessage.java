package Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ImageMessage extends Message implements Serializable {
    private final String imageString;

    /**
     * Creates a new ImageMessage with imageString that represents the Base64 string of the image.
     *
     * @param senderUsername the username of the sender
     * @param time           the date/time of the message
     * @param content        the content of the message
     */
    public ImageMessage(String senderUsername, LocalDateTime time, String content, String imageString) {
        super(senderUsername, time, content);
        this.imageString = imageString;
    }

    /**
     * Getter for the image Base64 string.
     * @return The image base64 string
     */
    public String getImageString(){
        return imageString;
    }

    /**
     * Check message type
     * @return true iff message is an image message, false otherwise.
     */
    public boolean isImageMessage() {
        return true;
    }
}

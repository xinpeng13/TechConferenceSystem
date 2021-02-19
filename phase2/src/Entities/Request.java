package Entities;

import java.io.Serializable;

public class Request implements Serializable {
    public Integer requestNum;
    public String senderUsername;
    public String content;
    public boolean status;

    /**
     * Creates a new Request
     *
     * @param requestNum the request number
     * @param senderUsername the username of the sender
     * @param content        the literal string representing the request
     */
    public Request(int requestNum, String senderUsername, String content) {
        this.content = content;
        this.requestNum = requestNum;
        this.senderUsername = senderUsername;
    }

    /**
     * Returns the status for this Request object.
     *
     * @return return the status of the Request (true: addressed, false: pending)
     */
    public boolean getStatus(){
        return status;
    }

    /**
     * Sets the status for this Request object as addressed.
     */
    public void setStatusAddressed(){
        this.status = true;
    }

    /**
     * Sets the status for this Request object as pending.
     */
    public void setStatusPending(){
        this.status = false;
    }

    /**
     * Return the request number for this Request Object.
     * @return the request number for this Request
     */
    public Integer getRequestNum() {
        return requestNum;
    }

    /**
     * getter for the sender username
     * @return The username of a user who sent the request
     */
    public String getSenderUsername(){
        return senderUsername;
    }

    /**
     * getter for the request content
     * @return The string representing the request
     */
    public String getContent(){
        return content;
    }

}

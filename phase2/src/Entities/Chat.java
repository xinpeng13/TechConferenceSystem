package Entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A chat in our program. It stores info related to chats and preforms operations on chats (ex. sending messages).
 * <p>
 * This object also stores Messages since they are a part of chats.
 * @author William Wang
 */
public class Chat implements Serializable {
    private LinkedHashMap<UUID, Message> chatMessages;  //stores all messages by pairing them with an id. Should be sorted by time
    private Map<String, UUID> lastViewedMessage; //pairs username with a message id. If chatMessages/memberUsernames is changed, this must be changed as well.
    private List<String> memberUsernames;  //users in the chat
    private String chatName;

    /**
     * Creates a new chat
     * @param memberUsernames The users that are in this chat
     */
    public Chat(List<String> memberUsernames){
        this.chatMessages = new LinkedHashMap<>();
        this.lastViewedMessage = new HashMap<>();
        this.memberUsernames = memberUsernames;
        this.chatName = String.join(", ", this.memberUsernames); //we can overload and create another constructor to accept a chat name

        // adds username to lastViewedMessage
        for (String username : this.memberUsernames){
            this.lastViewedMessage.put(username, null);  //sets the last viewed message to null for each user
        }
    }

    /**
     * getter for the all the message ids
     * @return Sorted list of all the message ids in the chat
     */
    public List<UUID> getAllMessages(){
        return new ArrayList<>(chatMessages.keySet()); // the use case needs to update the last viewed message if the user is viewing
    }

    /**
     * getter for the message object
     * PRECONDITION : The message exists in this chat
     * @param messageId The id of the message
     * @return The message object
     */
    public Message getMessageObject(UUID messageId){
        return chatMessages.get(messageId);
    }

    /**
     * getter for last viewed message for the user. If there are no messages in this chat, or the user has not viewed any messages, this will return null.
     * PRECONDITION : Username is in this chat
     * @param username The user who we are referencing
     * @return The last viewed message id of the user
     */
    public UUID getLastViewedMessage(String username){
        return lastViewedMessage.get(username);
    }

    /**
     * getter for members in the chat
     * @return The usernames of all the members
     */
    public List<String> getMemberUsernames() {
        return new ArrayList<>(memberUsernames);
    }

    /**
     * getter for the chat name
     * @return The name of this chat
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * Add a message to the chat
     * PRECONDITION : Added message has a timestamp after the last message in the chat, message does not already exist in this chat or another one
     * @param newMessageId The message id being added to the chat
     * @param message The message being added to the chat
     */
    public void addChatMessage(UUID newMessageId, Message message) {
        chatMessages.put(newMessageId, message); // the use case needs to update the last viewed message (since sending a message probably means they view the previous ones)
    }

    /**
     * Remove a message from the chat
     * PRECONDITION : The message id must exist in this chat, the last viewed message should be updated to the previous, chronological message
     * @param newMessageId The message id being added to the chat
     */
    public void removeMessage(UUID newMessageId){
        chatMessages.remove(newMessageId);
    }

    /**
     * Change the last viewed message for this user.
     * PRECONDITION : the user and the message exist in this chat
     * @param username The username of the user being changed
     * @param lastMessageId The message id that is being set to the last viewed one
     */
    public void setLastViewedMessage(String username, UUID lastMessageId) {
        lastViewedMessage.put(username, lastMessageId);
    }

//    /**
//     * Add a user to this chat
//     * PRECONDITION : the user does not already exist in this chat
//     * @param username The username of the user being added
//     */
//    public void addUser(String username){
//        memberUsernames.add(username);  //make sure to set the last viewed message after adding
//    }

//    /**
//     * Change the name of the chat
//     * @param newName The new name for the chat
//     */
//    public void setChatName(String newName){
//        chatName = newName;
//    }

}

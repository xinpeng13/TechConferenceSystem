package Controllers;
import UseCase.ChatManager;
import UseCase.EventManager;
import UseCase.UserManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Messaging system for the program. It can message users and view chats/messages.
 * @author William Wang and Kailas Moon
 */
public class MessagingSystem {
    private final ChatManager userChatManager;
    private final UserManager userManager;
    private final EventManager eventManager;

    /**
     * Creates the Messaging System
     * @param chatManager The chat manager
     * @param userManager The user manager
     * @param eventManager The event manager
     */
    public MessagingSystem(ChatManager chatManager, UserManager userManager, EventManager eventManager) {
        this.userChatManager = chatManager;
        this.userManager = userManager;
        this.eventManager = eventManager;
    }

    /**
     * Send a message to one user. Checks if the recipient is a friend before sending
     * @param senderUsername The username of the user sending the message
     * @param recipient The recipient of the message
     * @param content The content of the message
     * @param imagePath The filepath of the image
     * @return Null if the message was sent successfully or an Error message otherwise
     */
    public String messageOneUser(String senderUsername, String recipient, String content, String imagePath) {
        List<String> recipients = new ArrayList<>();
        recipients.add(recipient);
        if (!userManager.isUserExists(recipient)) {
            return("The user " + recipient + " does not exist.");
        } else if (userManager.isAddFriend(senderUsername, recipient)) {
            return sendMessageToUsers(recipients, senderUsername, LocalDateTime.now(), content, imagePath);
        } else {
            return(recipient + " is not your friend.");
        }
    }

    /**
     * View all the new chat messages. Note : chat is only added into the map if there are new messages
     * @param userName The username of the current user
     * @param peek Whether the user wants to mark the messages as read or not
     * @return A map of chat ids to their respective lists of new messages (message ids)
     */
    public Map<UUID, List<UUID>> viewAllNewMessages(String userName, Boolean peek){
        List<UUID> userChats = userChatManager.getUserChats(userName);  //includes archived chats
        Map<UUID, List<UUID>> newMessages = new HashMap<>();
        for (UUID id: userChats){
            List<UUID> chatNewMessages = userChatManager.getNewMessages(userName, id, peek);
            if (!chatNewMessages.isEmpty()){   //checks if there are new messages
                newMessages.put(id, chatNewMessages);
                if (userChatManager.getArchivedChats(userName).contains(id) && !peek){  //checks if chat is unarchived
                    userChatManager.unarchiveChat(userName, id);
                }
            }
        }
        return(newMessages);
    }

    /**
     * get Messages in the chat
     * @param username The username of the current user
     * @param chatId The id of the chat
     * @return The list of message ids
     */
    public List<UUID> viewChatMessages(String username, UUID chatId) {
        return userChatManager.getChatMessages(username, chatId);
    }

    /**
     * get sender of a message
     * @param chatId The id of the chat
     * @param messageId The id of the message
     * @return The message sender's username
     */
    public String getMessageSender(UUID chatId, UUID messageId){
        return userChatManager.getMessageSenderUsername(chatId, messageId);
    }

    /**
     * Get timestamp of the message
     * @param chatId The id of the chat
     * @param messageId The id of the message
     * @return The Local Date Time of the message
     */
    public LocalDateTime getMessageTimestamp(UUID chatId, UUID messageId){
        return userChatManager.getMessageTimeStamp(chatId, messageId);
    }

    /**
     * Get the content of the message
     * @param chatId The id of the chat
     * @param messageId The id of the message
     * @return The string representing the content
     */
    public String getMessageContent(UUID chatId, UUID messageId){
        return userChatManager.getMessageContent(chatId, messageId);
    }

    /**
     * Get the name of the chat
     * @param chatId The id of the chat
     * @return The name of the chat
     */
    public String getChatName(UUID chatId){
        return userChatManager.getChatName(chatId);
    }

    /**
     * Get the members of a chat
     * @param chatId The id of the chat
     * @return The list of chat member usernames
     */
    public List<String> getChatMembers(UUID chatId){
        return userChatManager.getChatMemberUsernames(chatId);
    }

    /**
     * get the user's current chats
     * @param userName The username of the current user
     * @return The list of the user's chats
     */
    public List<UUID> getCurrentChats(String userName) {
        List<UUID> allUserChats = userChatManager.getUserChats(userName);
        List<UUID> archivedChats = userChatManager.getArchivedChats(userName);
        for (UUID chatId : archivedChats) {
            if (userChatManager.areNewMessages(userName, chatId)) { //removes chat from being unarchived if new messages are received
                userChatManager.unarchiveChat(userName, chatId);
            }
        }

        List<UUID> currentChats = new ArrayList<>(allUserChats);
        currentChats.removeAll(userChatManager.getArchivedChats(userName));
        return currentChats;
    }

    /**
     * send a message to a list of users. Each message is sent in an individual chat.
     * @param usernames The usernames that the message is being sent to
     * @param senderUsername The username of the sender
     * @param time The time the message was sent
     * @param content The content of the message
     * @param imagePath The file path of the image
     * @return Null if message was sent successfully or an error message otherwise.
     */
    public String sendMessageToUsers(List<String> usernames, String senderUsername, LocalDateTime time, String content, String imagePath) {
        if (content.length() == 0 && imagePath.length() ==0) {
            return "You cannot send an empty message.";
        }
        // Handle images
        String imageString = imageToBase64(imagePath); //Returns an empty string, error message or base64 string
        if (imageString.equals("InvalidFileExtensionException!")){
            return "Invalid file extension.";
        } else if (imageString.equals("FileNotFoundException!")) {
            return "Image does not exist or cannot be found.";
        } else if(imageString.equals("IOException!")){
            return "IO exception occurred.";
        }

        for (String username : usernames) {
            List<String> thisChatUsernames = new ArrayList<>(Arrays.asList(senderUsername, username));
            UUID chat = userChatManager.getChatContainingUsers(thisChatUsernames); // Get the chat between the sender and the recipient

            if (chat == null) {
                chat = userChatManager.createChat(thisChatUsernames); // If no such chat exists, create it
            }
            if (!imageString.isEmpty()) { //If the Base64 String is not empty, then call sendImageMessageToChat
                userChatManager.sendImageMessageToChat(chat, senderUsername, time, content, imageString); //send an image/message
            } else {
                userChatManager.sendMessageToChat(chat, senderUsername, time, content); //else just send a message normally
            }
            if (userChatManager.getArchivedChats(senderUsername).contains(chat)){  //un-archives chat if it is archived
                userChatManager.unarchiveChat(senderUsername, chat);
            }
        }
        return null;
    }

    /**
     * delete a user's message from a chat
     * @param username the username of the user
     * @param chatId the id of the chat
     * @param messageId the id of the message
     * @return An error message, or null if there are no errors.
     */
    public String deleteUserMessage(String username, UUID chatId, UUID messageId){ // make sure you discard messageId after since it's gone from the system
        if (userChatManager.getMessageSenderUsername(chatId, messageId).equals(username)){
            userChatManager.deleteMessageFromChat(chatId, messageId);
            return null;
        } else{
            return "You cannot delete someone else's message";
        }
    }

    /**
     * mark a user's chat as unread
     * @param username the username of the user
     * @param chatId the id of the chat
     * @return An error message, or null if there are no errors.
     */
    public String markUserChatAsUnread(String username, UUID chatId){
        if (!userChatManager.isChatEmpty(chatId)){
            userChatManager.markChatAsUnread(username, chatId);
            return null;
        }else{
            return "Cannot mark empty chat as unread";
        }
    }

    /**
     * archive a user's chat
     * @param username the username of the user
     * @param chatId the id of the chat
     * @return An error message, or null if there are no errors.
     */
    public String archiveUserChat(String username, UUID chatId){
        if (!userChatManager.getArchivedChats(username).contains(chatId)){
            userChatManager.archiveChat(username, chatId);
            return null;
        } else{
            return "cannot archive chat that is already archived";
        }
    }

    /**
     * method for organizers to send a message to all attendees
     * @param senderUsername Username of the sender
     * @param content Content of message
     * @param imagePath The file path of the image
     * @return Null if the message was successfully sent, or an error message otherwise
     */
    public String organizerMessageAllAttendees(String senderUsername, String content, String imagePath) {
        List<String> allAttendees = userManager.getAllAttendee();
        return sendMessageToUsers(allAttendees, senderUsername, LocalDateTime.now(), content, imagePath);
    }

    /**
     * method for organizers to send a message to all speakers
     * @param senderUsername Username of the sender
     * @param content Content of message
     * @param imagePath The file path of the image
     * @return Null if the message was successfully sent, or an error message otherwise
     */
    public String organizerMessageAllSpeakers(String senderUsername, String content, String imagePath) {
        List<String> allSpeakers = userManager.getAllSpeaker();
        return sendMessageToUsers(allSpeakers, senderUsername, LocalDateTime.now(), content, imagePath);
    }

    /**
     * method for speakers to send a message to attendees of their events
     * @param senderUsername Username of the sender
     * @param eventTitles The titles of the events
     * @param content Content of message
     * @param imagePath The file path of the image
     * @return An error message, or null if there are no errors.
     */
    public String speakerMessageEventAttendees(String senderUsername, List<String> eventTitles, String content, String imagePath) {
        List<String> allEvents = eventManager.getAllEventTitle();
        List<String> recipients = new ArrayList<>();

        for (String title: eventTitles) {
            boolean found = false;
            for (String event : allEvents) {
                if (event.equals(title)) {
                    if (eventManager.getSpeakerUsernameByTitle(event).contains(senderUsername)) {
                        found = true;
                        for (String recipient: eventManager.getAllAttendeesByTitle(event)) {
                            if (!recipients.contains(recipient)) {
                                recipients.add(recipient);
                            }
                        }
                    } else {
                        return "Sender is not the speaker of " + title;
                    }
                }
            }
            if (!found) {
                return "No event with title " + title + " found.";
            }
        }
        return sendMessageToUsers(recipients, senderUsername, LocalDateTime.now(), content, imagePath);
    }

    /**
     * method for organizers to send a message to speakers and attendees of their events
     * @param senderUsername The username of the sender
     * @param eventTitles The list of event titles
     * @param content The content of the message
     * @param imagePath The image path
     * @return An error message, or null if there are no errors.
     */
    public String organizerMessageEventSpeakersAndAttendees(String senderUsername, List<String> eventTitles, String content, String imagePath) {
        List<String> allEvents = eventManager.getAllEventTitle();
        List<String> recipients = new ArrayList<>();

        for (String title: eventTitles) {
            boolean found = false;
            for (String event : allEvents) {
                if (event.equals(title)) {
                    found = true;
                    for (String recipient: eventManager.getAllAttendeesByTitle(event)) {
                        if (!recipients.contains(recipient)) {
                            recipients.add(recipient);
                        }
                    }
                    for (String recipient: eventManager.getSpeakerUsernameByTitle(event)) {
                        if (!recipients.contains(recipient)) {
                            recipients.add(recipient);
                        }
                    }
                }
            }
            if (!found) {
                return "No event with title " + title + " found.";
            }
        }
        sendMessageToUsers(recipients, senderUsername, LocalDateTime.now(), content, imagePath);
        return null;
    }

    /**
     * Add friends to message
     * @param mainUserUsername the current user
     * @param newFriend the user that the current user is adding
     * @return An error message, or null if there is none.
     */
    public String addPeopleToMessage(String mainUserUsername, String newFriend){
        if (userManager.userType(mainUserUsername).equals("Speaker")) {
            if (userManager.userType(newFriend).equals("Attendee")) {
                UUID chat = userChatManager.getChatContainingUsers(Arrays.asList(mainUserUsername, newFriend));
                if (chat == null || !userChatManager.doesChatHaveMessageFrom(chat, newFriend)) {
                    return("You may not add attendees as a friend until they have messaged you.");
                }
            } else {
                return("You may not add this user as a friend.");
            }
        } else if (userManager.userType(newFriend).equals("Organizer")) {
            return("You may not add an organizer as a friend.");
        }

        if (!userManager.isUserExists(newFriend)) {
            return("The user " + newFriend + " does not exist.");
        } else if (mainUserUsername.equals(newFriend)){
            return("You cannot add yourself as a friend");
        } else if (userManager.addFriend(mainUserUsername, newFriend)) {
            return(null);
        } else {
            return(newFriend + " is already a friend.");
        }
    }

    /**
     * Get a message id by index
     * @param chatId The id of the chat
     * @param messageIndex The index of the chat
     * @return The message id corresponding to the index or null if index is out of bounds
     */
    public UUID getMessageByIndex(UUID chatId, int messageIndex) {
        return userChatManager.getMessageUUIDbyIndex(chatId, messageIndex);
    }

    /**
     * Checks to see if the message includes an image
     * @param chatId The id of the chat
     * @param messageId The id of the message
     * @return A boolean representing whether this message has an image
     */
    public boolean doesMessageHaveImage(UUID chatId, UUID messageId) {
        return userChatManager.hasImage(chatId, messageId);
    }

    /**
     * Gets the base64 image string for a message
     * @param chatId The id of the chat
     * @param messageId The id of the message
     * @return The base64 string representing this image
     */
    public String getMessageImageString(UUID chatId, UUID messageId) {
        return userChatManager.getImage(chatId, messageId);
    }


//-----------------------------------------Private Methods-------------------------------------------

    private String imageToBase64(String imagePath) {
        if (!imagePath.equals("")) { //Checks to see if imageString is not empty
            String extension = imagePath.substring(imagePath.lastIndexOf(".")+1);
            String[] validExtensions = new String[] {"jpg", "jpeg", "png", "gif", "bmp", "tiff"};
            if (!Arrays.asList(validExtensions).contains(extension.toLowerCase())) {
                return "InvalidFileExtensionException!";
            }

            //Read file from file path
            String encodedFile;
            File file = new File(imagePath);
            try {
                FileInputStream imageFile = new FileInputStream(file);
                byte[] imageBytes = new byte[(int)file.length()];
                imageFile.read(imageBytes);
                encodedFile = Base64.getEncoder().encodeToString(imageBytes);
            } catch (FileNotFoundException e) {
                return "FileNotFoundException!";
            } catch (IOException f) {
                return "IOException!";
            }
            return encodedFile;
        }
        return "";  //if image path is empty
    }

}


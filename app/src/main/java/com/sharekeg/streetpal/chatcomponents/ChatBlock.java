package com.sharekeg.streetpal.chatcomponents;

import java.util.ArrayList;

/**
 * Created by Khalid on 8/16/2017.
 */

public class ChatBlock {
    private ArrayList<ChatMessage> chatMessages;
    private UserOptions userOptions;

    public ChatBlock(ArrayList<ChatMessage> chatMessages, UserOptions userOptions) {
        this.chatMessages = chatMessages;
        this.userOptions = userOptions;
    }

    public ChatBlock(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.userOptions = null;
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public UserOptions getUserOptions() {
        return userOptions;
    }

    public void setUserOptions(UserOptions userOptions) {
        this.userOptions = userOptions;
    }
}

package com.sharekeg.streetpal.chatcomponents;

/**
 * Created by Khalid on 7/29/2017.
 */
public class ChatMessage {
    private String messageText, navigationTag;
    private boolean isUserMessage, hasNavigationLink;

    public ChatMessage(String messageText, boolean isUserMessage) {
        this.messageText = messageText;
        this.isUserMessage = isUserMessage;
        this.hasNavigationLink = false;
    }

    public ChatMessage(String messageText, String navigationTag, boolean isUserMessage) {
        this.messageText = messageText;
        this.navigationTag = navigationTag;
        this.isUserMessage = isUserMessage;
        this.hasNavigationLink = true;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getNavigationTag() {
        return navigationTag;
    }

    public void setNavigationTag(String navigationTag) {
        this.navigationTag = navigationTag;
    }

    public boolean isHasNavigationLink() {
        return hasNavigationLink;
    }

    public void setHasNavigationLink(boolean hasNavigationLink) {
        this.hasNavigationLink = hasNavigationLink;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }

    public void setUserMessage(boolean userMessage) {
        this.isUserMessage = userMessage;
    }
    public String toString() {
        return messageText;
    }
}

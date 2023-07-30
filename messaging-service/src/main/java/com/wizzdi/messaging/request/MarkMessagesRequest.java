package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.ChatUser;

import java.time.OffsetDateTime;

public class MarkMessagesRequest {

    private MessageFilter messageFilter;
    private String chatUserId;
    private OffsetDateTime dateRead;
    @JsonIgnore
    private ChatUser chatUser;

    public MessageFilter getMessageFilter() {
        return messageFilter;
    }

    public <T extends MarkMessagesRequest> T setMessageFilter(MessageFilter messageFilter) {
        this.messageFilter = messageFilter;
        return (T) this;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public <T extends MarkMessagesRequest> T setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
        return (T) this;
    }

    @JsonIgnore
    public ChatUser getChatUser() {
        return chatUser;
    }

    public <T extends MarkMessagesRequest> T setChatUser(ChatUser chatUser) {
        this.chatUser = chatUser;
        return (T) this;
    }

    public OffsetDateTime getDateRead() {
        return dateRead;
    }

    public <T extends MarkMessagesRequest> T setDateRead(OffsetDateTime dateRead) {
        this.dateRead = dateRead;
        return (T) this;
    }
}

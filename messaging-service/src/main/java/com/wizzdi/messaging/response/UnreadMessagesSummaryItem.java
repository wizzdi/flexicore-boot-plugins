package com.wizzdi.messaging.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.Chat;

public class UnreadMessagesSummaryItem {
    @JsonIgnore
    private String chatId;
    private Chat chat;
    private long count;

    public UnreadMessagesSummaryItem() {
    }

    public UnreadMessagesSummaryItem(String chatId, long count) {
        this.chatId = chatId;
        this.count = count;
    }

    @JsonIgnore
    public String getChatId() {
        return chatId;
    }

    public <T extends UnreadMessagesSummaryItem> T setChatId(String chatId) {
        this.chatId = chatId;
        return (T) this;
    }

    public long getCount() {
        return count;
    }

    public <T extends UnreadMessagesSummaryItem> T setCount(long count) {
        this.count = count;
        return (T) this;
    }

    public Chat getChat() {
        return chat;
    }

    public <T extends UnreadMessagesSummaryItem> T setChat(Chat chat) {
        this.chat = chat;
        return (T) this;
    }
}

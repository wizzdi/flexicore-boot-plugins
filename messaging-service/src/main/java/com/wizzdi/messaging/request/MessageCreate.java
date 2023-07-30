package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageCreate extends BasicCreate {

	private Map<String, OffsetDateTime> chatUsers;
	private List<String> media;
	private String content;
	@JsonIgnore
	private Map<String, Object> other = new HashMap<>();
	private String chatId;
	@JsonIgnore
	private Chat chat;
	@JsonIgnore
	private ChatUser sender;

	public String getChatId() {
		return chatId;
	}

	public <T extends MessageCreate> T setChatId(String chatId) {
		this.chatId = chatId;
		return (T) this;
	}

	@JsonIgnore
	public Chat getChat() {
		return chat;
	}

	public <T extends MessageCreate> T setChat(Chat chat) {
		this.chat = chat;
		return (T) this;
	}

	@JsonIgnore
	public ChatUser getSender() {
		return sender;
	}

	public <T extends MessageCreate> T setSender(ChatUser sender) {
		this.sender = sender;
		return (T) this;
	}
	@JsonIgnore
	public Map<String, Object> getOther() {
		return other;
	}
	public <T extends MessageCreate> T setOther(Map<String, Object> other) {
		this.other = other;
		return (T) this;
	}
	@JsonAnySetter
	public void set(String key, Object val) {
		other.put(key, val);
	}

	@JsonAnyGetter
	public Object get(String key) {
		return other.get(key);
	}

	public Map<String, OffsetDateTime> getChatUsers() {
		return chatUsers;
	}

	public <T extends MessageCreate> T setChatUsers(Map<String, OffsetDateTime> chatUsers) {
		this.chatUsers = chatUsers;
		return (T) this;
	}

	public List<String> getMedia() {
		return media;
	}

	public <T extends MessageCreate> T setMedia(List<String> media) {
		this.media = media;
		return (T) this;
	}

	public String getContent() {
		return content;
	}

	public <T extends MessageCreate> T setContent(String content) {
		this.content = content;
		return (T) this;
	}
}

package com.wizzdi.messaging.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Basic;

import com.wizzdi.dynamic.properties.converter.JsonConverter;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Access(AccessType.PROPERTY)
public class Message extends Basic {
public static final String CHATUSERS_FIELD="chatUsers";
public static final String CONTENT_FIELD="content";
public static final String MEDIA_FIELD="media";
	@Convert(converter = JsonConverter.class)
	@JsonIgnore
	private Map<String, Object> other = new HashMap<>();
	@ManyToOne(targetEntity = Chat.class)
	private Chat chat;
	@ManyToOne(targetEntity = ChatUser.class)
	private ChatUser sender;


	@JsonAnySetter
	public void set(String key, Object val) {
		other.put(key, val);
	}


	@Column(columnDefinition = "jsonb")
	@Convert(converter = JsonConverter.class)
	@JsonIgnore
	public Map<String, Object> getOther() {
		return other;
	}

	@JsonAnyGetter
	public Map<String, Object> any() {
		return other;
	}


	public <T extends Message> T setOther(Map<String, Object> other) {
		this.other= other;
		if(other!=null){
			Object o = other.get(CHATUSERS_FIELD);
			if(o instanceof Map){
				Map<?,?> map= (Map<?, ?>) o;
				for (Map.Entry entry : map.entrySet()) {
					if(entry.getValue() instanceof String){
						OffsetDateTime date=OffsetDateTime.parse((String)entry.getValue());
						entry.setValue(date);
					}
				}

			}

		}
		return (T) this;
	}

	@ManyToOne(targetEntity = Chat.class)
	public Chat getChat() {
		return chat;
	}

	public <T extends Message> T setChat(Chat chat) {
		this.chat = chat;
		return (T) this;
	}

	@ManyToOne(targetEntity = ChatUser.class)
	public ChatUser getSender() {
		return sender;
	}

	public <T extends Message> T setSender(ChatUser sender) {
		this.sender = sender;
		return (T) this;
	}

	@Transient
	public List<String> getMedia(){
		return (List<String>) other.get(MEDIA_FIELD);
	}

	public <T extends Message> T setMedia(List<String> media) {
		other.put(MEDIA_FIELD,media);
		return (T) this;
	}

	@Transient
	public String getContent(){
		return (String) other.get(CONTENT_FIELD);
	}

	public <T extends Message> T setContent(String content) {
		other.put(CONTENT_FIELD,content);
		return (T) this;
	}


	@Transient
	public Map<String, OffsetDateTime> getChatUsers(){
		return (Map<String, OffsetDateTime>) other.get(CHATUSERS_FIELD);
	}

	public <T extends Message> T setChatUsers(Map<String, OffsetDateTime> chatUsers) {
		other.put(CHATUSERS_FIELD,chatUsers);
		return (T) this;
	}
}

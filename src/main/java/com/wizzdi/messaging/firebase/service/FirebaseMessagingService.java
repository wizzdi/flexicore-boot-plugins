package com.wizzdi.messaging.firebase.service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.events.NewMessageEvent;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceFilter;
import com.wizzdi.messaging.model.*;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.ChatToChatUserFilter;
import com.wizzdi.messaging.service.ChatToChatUserService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Extension
public class FirebaseMessagingService implements Plugin {

	private static final DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ISO_DATE_TIME;
	private static final Logger logger= LoggerFactory.getLogger(FirebaseMessagingService.class);
	public static final String FROM_NAME = "fromName";
	public static final String FROM_ID = "fromId";
	public static final String CHAT_ID = "chatID";
	public static final String MESSAGE_ID = "messageId";
	public static final String TIME = "time";

	@Autowired
	private ChatToChatUserService chatToChatUserService;
	@Autowired
	private FirebaseEnabledDeviceService firebaseEnabledDeviceService;

	@Autowired
	private FirebaseMessaging firebaseMessaging;

	@EventListener
	@Async
	public void onNewMessageEvent(NewMessageEvent event) throws FirebaseMessagingException {
		Chat chat = event.getMessage().getChat();
		ChatUser sender = event.getMessage().getSender();
		Message message = event.getMessage();
		Map<String, ChatUser> chatUsers = chatToChatUserService.listAllChatToChatUsers(new ChatToChatUserFilter().setChats(Collections.singletonList(chat)), null).stream().map(ChatToChatUser::getChatUser).filter(chatUser -> !chatUser.getId().equals(sender.getId())).collect(Collectors.toMap(f -> f.getId(), f -> f, (a, b) -> a));
		List<FirebaseEnabledDevice> devices = chatUsers.isEmpty() ? new ArrayList<>() : firebaseEnabledDeviceService.listAllFirebaseEnabledDevices(new FirebaseEnabledDeviceFilter().setChatUsers(new ArrayList<>(chatUsers.values())), null);
		Set<String> tokens = devices.stream().map(MessageReceiverDevice::getExternalId).filter(Objects::nonNull).collect(Collectors.toSet());
		if (!tokens.isEmpty()) {
			String content = message.getContent();
			if(content==null||content.trim().isEmpty()){
				content="Empty Message";
			}
			Notification notification = Notification
					.builder()
					.setTitle(content)
					.setBody(content)
					.build();

			Map<String, String> stringProps = message.getOther().entrySet().stream().filter(f -> f.getValue() instanceof String).collect(Collectors.toMap(f -> f.getKey(), f -> (String) f.getValue()));
			stringProps.put(FROM_NAME,sender.getName());
			stringProps.put(FROM_ID,sender.getId());
			stringProps.put(CHAT_ID,chat.getId());
			stringProps.put(MESSAGE_ID,message.getId());
			stringProps.put(TIME,dateTimeFormatter.format(message.getUpdateDate()));

			MulticastMessage fbMessage = MulticastMessage
					.builder()
					.addAllTokens(tokens)
					.setNotification(notification)
					.putAllData(stringProps)
					.build();

			BatchResponse batchResponse = firebaseMessaging.sendMulticast(fbMessage);
			if(batchResponse.getFailureCount()>0){
				String errorString = batchResponse.getResponses().stream().map(f -> f.getException()).filter(Objects::nonNull).map(f -> "error:" + f.getMessage() + " , code: " + f.getMessagingErrorCode()).collect(Collectors.joining(System.lineSeparator()));
				logger.warn("failed sending "+batchResponse.getFailureCount() +" to Firebase , "+batchResponse.getSuccessCount() +" were sent: "+System.lineSeparator()+errorString);
			}
			else{

				logger.debug("sent "+batchResponse.getSuccessCount() +" to firebase");
			}

		}
	}
}

package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.MessageRepository;
import com.wizzdi.messaging.events.BeforeMessageEvent;
import com.wizzdi.messaging.events.NewMessageEvent;
import com.wizzdi.messaging.events.UpdatedMessageEvent;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.ChatUser_;
import com.wizzdi.messaging.model.Chat_;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.MarkMessagesRequest;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import com.wizzdi.messaging.response.UnreadMessagesSummary;
import com.wizzdi.messaging.response.UnreadMessagesSummaryItem;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component
public class MessageService implements Plugin {


    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private BasicService basicService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ChatUserService chatUserService;

    public Message createMessage(MessageCreate messageCreate, SecurityContext securityContext) {
        Message message = createMessageNoMerge(messageCreate, securityContext);
        messageRepository.merge(message);
        applicationEventPublisher.publishEvent(new BeforeMessageEvent(message, securityContext));

        applicationEventPublisher.publishEvent(new NewMessageEvent(message, securityContext));
        return message;
    }

    public void merge(Object o) {
        messageRepository.merge(o);
    }

    public void massMerge(List<Object> list) {
        messageRepository.massMerge(list);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return messageRepository.listByIds(c, ids, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return messageRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public Message createMessageNoMerge(MessageCreate messageCreate, SecurityContext securityContext) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        updateMessageNoMerge(messageCreate, message);
        return message;
    }

    public boolean updateMessageNoMerge(MessageCreate messageCreate, Message message) {
        boolean update = basicService.updateBasicNoMerge(messageCreate, message);
        if (messageCreate.getChat() != null && (message.getChat() == null || !messageCreate.getChat().getId().equals(message.getChat().getId()))) {
            message.setChat(messageCreate.getChat());
            update = true;
        }
        if (messageCreate.getSender() != null && (message.getSender() == null || !messageCreate.getSender().getId().equals(message.getSender().getId()))) {
            message.setSender(messageCreate.getSender());
            update = true;
        }
        if (messageCreate.getContent() != null && !messageCreate.getContent().equals(message.getContent())) {
            messageCreate.getOther().put(Message.CONTENT_FIELD, messageCreate.getContent());
        }
        if (messageCreate.getMedia() != null && !messageCreate.getMedia().equals(message.getMedia())) {
            messageCreate.getOther().put(Message.MEDIA_FIELD, messageCreate.getMedia());

        }
        if (messageCreate.getChatUsers() != null && !messageCreate.getChatUsers().equals(message.getChatUsers())) {
            Map<String, OffsetDateTime> mergedValues = message.getChatUsers() != null ? new HashMap<>(message.getChatUsers()) : new HashMap<>();
            mergedValues.putAll(messageCreate.getChatUsers());
            messageCreate.getOther().put(Message.CHATUSERS_FIELD, mergedValues);
        }
        Map<String, Object> mergedValues = DynamicPropertiesUtils.updateDynamic(messageCreate.getOther(), message.getOther());

        if (mergedValues != null) {
            message.setOther(mergedValues);
            update = true;
        }
        return update;
    }

    public Message updateMessage(MessageUpdate messageUpdate, SecurityContext securityContext) {
        Message message = messageUpdate.getMessage();
        if (updateMessageNoMerge(messageUpdate, message)) {
            messageRepository.merge(message);
            applicationEventPublisher.publishEvent(new UpdatedMessageEvent(message));

        }
        return message;
    }

    public void validate(MessageCreate messageCreate, SecurityContext securityContext) {
        basicService.validate(messageCreate, securityContext);
        if (messageCreate.getSender() == null) {
            ChatUser chatUser = chatUserService.getChatUser(securityContext);
            if (chatUser == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "must be a chat user");
            }
            messageCreate.setSender(chatUser);

        }
        String chatId = messageCreate.getChatId();
        Chat chat = chatId != null ? getByIdOrNull(chatId, Chat.class,securityContext) : null;
        if (chatId != null && (chat == null || chat.isSoftDelete())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat with id " + chatId);
        }
        messageCreate.setChat(chat);

    }

    private ChatUser getChatUserById(String chatUserId, SecurityContext SecurityContext) {
        ChatUser thisChatUser = chatUserService.getChatUser(SecurityContext);
        if (chatUserId == null) {
            return thisChatUser;
        }
        if (thisChatUser != null && thisChatUser.getId().equals(chatUserId)) {
            return thisChatUser;
        }

        return getByIdOrNull(chatUserId, ChatUser.class,SecurityContext);
    }

    public void validate(MarkMessagesRequest markMessagesRequest, SecurityContext securityContext) {
        MessageFilter messageFilter = markMessagesRequest.getMessageFilter();
        if (messageFilter == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message Filter must be send");
        }
        validate(messageFilter, securityContext);
        if (markMessagesRequest.getDateRead() == null) {
            markMessagesRequest.setDateRead(OffsetDateTime.now());
        }
        String chatUserId = markMessagesRequest.getChatUserId();
        ChatUser chatUser =getChatUserById(chatUserId,securityContext);
        if ( chatUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat user with id " + chatUserId);
        }

        markMessagesRequest.setChatUser(chatUser);
    }


    public void validate(MessageFilter messageFilter, SecurityContext securityContext) {
        basicService.validate(messageFilter, securityContext);
        ChatUser thisChatUser = chatUserService.getChatUser(securityContext);
        Set<String> chatIds = messageFilter.getChatsIds();
        Map<String, Chat> chatMap = chatIds.isEmpty() ? new HashMap<>() : messageRepository.listByIds(Chat.class, chatIds,securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        chatIds.removeAll(chatMap.keySet());
        if (!chatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chats with ids " + chatIds);
        }
        messageFilter.setChats(new ArrayList<>(chatMap.values()));

        Set<String> senderIds = messageFilter.getSenderIds();
        Map<String, ChatUser> sendersMap = senderIds.isEmpty() ? new HashMap<>() : listByIds(ChatUser.class, senderIds,securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        if (thisChatUser != null && senderIds.contains(thisChatUser.getId())) {
            sendersMap.put(thisChatUser.getId(), thisChatUser);
        }
        senderIds.removeAll(sendersMap.keySet());
        if (!senderIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat users with ids " + senderIds);
        }
        messageFilter.setSenders(new ArrayList<>(sendersMap.values()));

        Set<String> addressedToIds = messageFilter.getAddressedToIds();
        Map<String, ChatUser> addressedToMap = addressedToIds.isEmpty() ? new HashMap<>() : listByIds(ChatUser.class, addressedToIds,securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        if (thisChatUser != null && addressedToIds.contains(thisChatUser.getId())) {
            addressedToMap.put(thisChatUser.getId(), thisChatUser);
        }
        addressedToIds.removeAll(addressedToMap.keySet());
        if (!addressedToIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat users with ids " + addressedToIds);
        }
        messageFilter.setAddressedTo(new ArrayList<>(addressedToMap.values()));

        if (messageFilter.getSenders().isEmpty() && messageFilter.getChats().isEmpty() && messageFilter.getAddressedTo().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"must specify at least one chat or chat user(sender or addressed to)");
        }


        Set<String> unreadByIds = messageFilter.getUnreadByIds();
        Map<String, ChatUser> unreadByMap = unreadByIds.isEmpty() ? new HashMap<>() : listByIds(ChatUser.class, unreadByIds,securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        if (thisChatUser != null && unreadByIds.contains(thisChatUser.getId())) {
            unreadByMap.put(thisChatUser.getId(), thisChatUser);
        }
        unreadByIds.removeAll(unreadByMap.keySet());
        if (!unreadByIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat users with ids " + unreadByIds);
        }
        messageFilter.setUnreadBy(new ArrayList<>(unreadByMap.values()));


    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return messageRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return messageRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return messageRepository.findByIdOrNull(type, id);
    }

    public PaginationResponse<Message> getAllMessages(MessageFilter MessageFilter, SecurityContext securityContext) {
        long count = messageRepository.countAllMessages(MessageFilter, securityContext);
        PaginationResponse<Message> messagePaginationResponse = new PaginationResponse<>(new ArrayList<>(), MessageFilter, count);
        if (MessageFilter.isLastPage()) {
            MessageFilter.setCurrentPage((int) (long) messagePaginationResponse.getEndPage());
        }
        List<Message> list = listAllMessages(MessageFilter, securityContext);
        messagePaginationResponse.setList(list);
        return messagePaginationResponse;
    }

    public List<Message> listAllMessages(MessageFilter MessageFilter, SecurityContext securityContext) {
        return messageRepository.listAllMessages(MessageFilter, securityContext);
    }

    public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
        return messageRepository.findByIds(c, requested);
    }

    public PaginationResponse<Message> markRead(MarkMessagesRequest markMessagesRequest, SecurityContext securityContext) {
        PaginationResponse<Message> messagePaginationResponse = getAllMessages(markMessagesRequest.getMessageFilter(), securityContext);
        List<Message> messages = messagePaginationResponse.getList();
        Map<String, OffsetDateTime> chatUsersMap = new HashMap<>();
        chatUsersMap.put(markMessagesRequest.getChatUser().getId(), markMessagesRequest.getDateRead());
        List<Object> toMerge = new ArrayList<>();
        for (Message message : messages) {
            MessageCreate messageCreate = new MessageCreate()
                    .setChatUsers(chatUsersMap);
            if (updateMessageNoMerge(messageCreate, message)) {
                toMerge.add(message);
            }
        }
        messageRepository.massMerge(toMerge);
        return messagePaginationResponse;
    }

    public UnreadMessagesSummary getMessageSummary(MessageFilter messageFilter, SecurityContext securityContext) {
        List<UnreadMessagesSummaryItem> messagesSummaryItems=messageRepository.getMessageSummary(messageFilter,securityContext);
        Set<String> chatIds=messagesSummaryItems.stream().map(f->f.getChatId()).filter(f->f!=null).collect(Collectors.toSet());
        Map<String,Chat> chats=chatIds.isEmpty()?new HashMap<>():listByIds(Chat.class,chatIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        for (UnreadMessagesSummaryItem messagesSummaryItem : messagesSummaryItems) {

            Chat chat=chats.get(messagesSummaryItem.getChatId());
            if(chat!=null){
                messagesSummaryItem.setChat(chat);
            }
        }
        List<UnreadMessagesSummaryItem> itemsToReturn = messagesSummaryItems.stream().filter(f -> f.getChat() != null).collect(Collectors.toList());
        return new UnreadMessagesSummary().setItems(itemsToReturn).setTotal(itemsToReturn.stream().mapToLong(f->f.getCount()).sum());
    }
}

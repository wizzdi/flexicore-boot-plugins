package com.wizzdi.maps.service.ws;

import com.flexicore.annotations.Protected;
import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.model.Basic;
import com.flexicore.model.SecurityTenant;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.ws.encoders.MappedPOINotificationDecoder;
import com.wizzdi.maps.service.ws.encoders.MappedPOINotificationEncoder;
import com.wizzdi.maps.service.ws.messages.MapIconChangedNotification;
import com.wizzdi.maps.service.ws.messages.MappedPOINotification;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ServerEndpoint(
		value = "/ws/mappedPOI/{authenticationKey}",
		encoders = {MappedPOINotificationEncoder.class},
		decoders = {MappedPOINotificationDecoder.class})


@Component
@Extension
public class MapsSocketHandler implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(MapsSocketHandler.class);
	private final Map<String, ConcurrentLinkedQueue<Session>> sessions = new ConcurrentHashMap<>();

	@EventListener
	@Async
	public void onMappedPOINotification(BasicCreated<MappedPOI> statusHistoryBasicCreated){
		MappedPOI mappedPOI = statusHistoryBasicCreated.getBaseclass();
		onMappedPOIChanged(mappedPOI);

	}

	@EventListener
	@Async
	public void onMappedPOINotification(BasicUpdated<MappedPOI> statusHistoryBasicCreated){

		onMappedPOIChanged(statusHistoryBasicCreated.getBaseclass());

	}

	private void onMappedPOIChanged(MappedPOI mappedPOI) {
		MapIconChangedNotification mapIconChangedNotification = new MapIconChangedNotification()
				.setMappedIconId(mappedPOI.getMapIcon().getId())
				.setMappedPOIId(mappedPOI.getId())
				.setTenantId(mappedPOI.getSecurity().getTenant().getId())
				.setId(UUID.randomUUID().toString());

		sendMessageToTenantsSessions(mapIconChangedNotification);
	}

	private void sendMessageToTenantsSessions(MappedPOINotification mappedPOINotification) {
		ConcurrentLinkedQueue<Session> webSocketSessions = sessions.computeIfAbsent(mappedPOINotification.getTenantId(), f -> new ConcurrentLinkedQueue<>());
		List<Session> toRemove=new ArrayList<>();
		try {
			for (Session webSocketSession : webSocketSessions) {
				try {
					webSocketSession.getBasicRemote().sendObject(mappedPOINotification);
				}
				catch (Throwable e){
					logger.error("failed sending WS message to "+webSocketSession.getId() +" closing",e);
					toRemove.add(webSocketSession);

					try {
						webSocketSession.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY,"WS failed sending message"));
					}
					catch (Throwable e1){
						logger.debug("failed closing session "+webSocketSession.getId(),e);
					}

				}
			}
		}
		catch (Throwable e){
			logger.error("failed sending WS message",e);
		}
		finally {
			webSocketSessions.removeAll(toRemove);
		}
	}

	@OnMessage
	@Update
	@Protected
	public void receiveMessage(@PathParam("authenticationKey") String authenticationKey,MappedPOINotification message, Session session) {
		SecurityContextBase securityContext = (SecurityContextBase) session.getUserProperties().get("securityContext");

		logger.info("Received : " + message + ", session:" + session.getId());
	}
	@OnOpen
	@Write
	@Protected
	public void open(@PathParam("authenticationKey") String authenticationKey, Session session) {
		SecurityContextBase securityContext = (SecurityContextBase) session.getUserProperties().get("securityContext");
		if(securityContext==null){
			try {
				session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"invalid authentication key"));
			} catch (IOException e) {
				logger.error("failed closing WS",e);
			}
			return;
		}
		List<?> tenants = securityContext.getTenants();
		tenants.stream().filter(f->f instanceof SecurityTenant).map(f->(SecurityTenant)f).map(f->f.getId()).forEach(
				f->sessions.computeIfAbsent(f,e->new ConcurrentLinkedQueue<>()).add(session)
		);

	}

	@OnClose
	@Update
	@Protected
	public void close(@PathParam("authenticationKey") String authenticationKey, CloseReason c, Session session) {

		SecurityContextBase securityContext = (SecurityContextBase) session.getUserProperties().get("securityContext");
		if(securityContext==null){
			return;
		}
		List<?> tenants = securityContext.getTenants();
		tenants.stream().filter(f->f instanceof SecurityTenant).map(f->(SecurityTenant)f).map(Basic::getId).map(sessions::get)
				.filter(f->f!=null).forEach(f-> f.remove(session));

	}
	@OnError
	@Read
	public void error(@PathParam("authenticationKey") String authenticationKey, Throwable error, Session session) {
		logger.error("error ",error);

	}
}
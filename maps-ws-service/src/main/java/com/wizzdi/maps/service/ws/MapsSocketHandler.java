package com.wizzdi.maps.service.ws;

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
import com.wizzdi.security.adapter.FlexiCoreAuthentication;
import com.wizzdi.security.bearer.jwt.JWTSecurityContextCreator;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
	public static final String SECURITY_CONTEXT_KEY = "securityContext";
	private final Map<String, ConcurrentLinkedQueue<Session>> sessions = new ConcurrentHashMap<>();

	@Autowired
	@Lazy
	private JWTSecurityContextCreator jwtSecurityContextCreator;

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
				.setMappedIconId(Optional.ofNullable(mappedPOI.getMapIcon()).map(f->f.getId()).orElse(null))
				.setMappedPOIId(mappedPOI.getId())
				.setTenantId(mappedPOI.getSecurity().getTenant().getId())
				.setId(UUID.randomUUID().toString());

		sendMessageToTenantsSessions(mapIconChangedNotification);
	}

	private void sendMessageToTenantsSessions(MappedPOINotification mappedPOINotification) {
		logger.debug("sending WS message {} , for mapped poi {}",mappedPOINotification.getId(),mappedPOINotification.getMappedPOIId());
		ConcurrentLinkedQueue<Session> webSocketSessions = sessions.computeIfAbsent(mappedPOINotification.getTenantId(), f -> new ConcurrentLinkedQueue<>());
		List<Session> toRemove=new ArrayList<>();
		try {
			for (Session webSocketSession : webSocketSessions) {
				try {
					webSocketSession.getBasicRemote().sendObject(mappedPOINotification);
					logger.debug("sent WS message to "+webSocketSession.getId());
				}
				catch (Throwable e){
					logger.warn("failed sending WS message to "+webSocketSession.getId() +" closing",e);
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
			logger.debug("removed {} sessions",toRemove.size());
			webSocketSessions.removeAll(toRemove);
		}
	}

	@OnMessage
	@Update
	public void receiveMessage(@PathParam("authenticationKey") String authenticationKey,MappedPOINotification message, Session session) {
		if (!validateSecurity(authenticationKey, session)) return;
		SecurityContextBase securityContext = (SecurityContextBase) session.getUserProperties().get(SECURITY_CONTEXT_KEY);

		logger.info("Received : " + message + ", session:" + session.getId());
	}

	private boolean validateSecurity(String authenticationKey, Session session) {
		FlexiCoreAuthentication authentication = jwtSecurityContextCreator.getSecurityContext(authenticationKey);
		if(authentication==null){
			try {
				session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "not Authorized"));
			} catch (IOException e) {
				logger.error("failed closing WS",e);
			}
			return false;
		}
		SecurityContextBase securityContextBase = authentication.getSecurityContextBase();
		session.getUserProperties().put(SECURITY_CONTEXT_KEY, securityContextBase);
		return true;
	}

	@OnOpen
	@Write
	public void open(@PathParam("authenticationKey") String authenticationKey, Session session) {
		if (!validateSecurity(authenticationKey, session)) return;

		SecurityContextBase securityContext = (SecurityContextBase) session.getUserProperties().get(SECURITY_CONTEXT_KEY);
		List<?> tenants = securityContext.getTenants();
		tenants.stream().filter(f->f instanceof SecurityTenant).map(f->(SecurityTenant)f).map(f->f.getId()).forEach(
				f->sessions.computeIfAbsent(f,e->new ConcurrentLinkedQueue<>()).add(session)
		);

	}

	@OnClose
	@Update
	public void close(@PathParam("authenticationKey") String authenticationKey, CloseReason c, Session session) {
		if (!validateSecurity(authenticationKey, session)) return;

		SecurityContextBase securityContext = (SecurityContextBase) session.getUserProperties().get(SECURITY_CONTEXT_KEY);
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
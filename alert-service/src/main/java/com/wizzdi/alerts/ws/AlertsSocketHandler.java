package com.wizzdi.alerts.ws;

import com.flexicore.annotations.rest.Read;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.model.Basic;
import com.flexicore.model.SecurityTenant;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.alerts.Alert;
import com.wizzdi.alerts.ws.encoders.AlertMessageDecoder;
import com.wizzdi.alerts.ws.encoders.AlertMessageEncoder;
import com.wizzdi.alerts.ws.messages.AlertMessage;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.security.adapter.FlexiCoreAuthentication;
import com.wizzdi.security.bearer.jwt.JWTSecurityContextCreator;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(
		value = "/ws/alerts/{authenticationKey}",
		encoders = {AlertMessageEncoder.class},
		decoders = {AlertMessageDecoder.class})


@Component
@Extension
public class AlertsSocketHandler implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(AlertsSocketHandler.class);
	public static final String SECURITY_CONTEXT_KEY = "securityContext";

	private final Map<String, ConcurrentHashMap<String,Session>> sessions = new ConcurrentHashMap<>();

	@Autowired
	@Lazy
	private JWTSecurityContextCreator jwtSecurityContextCreator;

	@EventListener
	@Async
	public void onAlertCreated(BasicCreated<Alert> alertCreated){

		onAlertCreated(alertCreated.getBaseclass());

	}

	private void onAlertCreated(Alert alert) {
		AlertMessage alertMessage = new AlertMessage(alert);


		sendMessageToTenantsSessions(alertMessage);
	}

	private void sendMessageToTenantsSessions(AlertMessage mappedPOINotification) {
		ConcurrentHashMap<String,Session> webSocketSessions = sessions.computeIfAbsent(mappedPOINotification.tenantId(), f -> new ConcurrentHashMap<>());
		List<Session> toRemove=new ArrayList<>();
		try {
			for (Session webSocketSession : webSocketSessions.values()) {
				try {
					webSocketSession.getBasicRemote().sendObject(mappedPOINotification);
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
			for (Session session : toRemove) {
				webSocketSessions.remove(session.getId(),session);

			}
		}
	}

	@OnMessage
	@Update
	public void receiveMessage(@PathParam("authenticationKey") String authenticationKey,AlertMessage message, Session session) {
		if (!validateSecurity(authenticationKey, session)) return;
		SecurityContext securityContext = (SecurityContext) session.getUserProperties().get(SECURITY_CONTEXT_KEY);

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
		SecurityContext SecurityContext = authentication.getSecurityContext();
		session.getUserProperties().put(SECURITY_CONTEXT_KEY, SecurityContext);
		return true;
	}
	@OnOpen
	@Write
	public void open(@PathParam("authenticationKey") String authenticationKey, Session session) {
		if (!validateSecurity(authenticationKey, session)) return;

		SecurityContext securityContext = (SecurityContext) session.getUserProperties().get(SECURITY_CONTEXT_KEY);
		List<?> tenants = securityContext.getTenants();
		tenants.stream().filter(f->f instanceof SecurityTenant).map(f->(SecurityTenant)f).map(f->f.getId()).forEach(
				f->sessions.computeIfAbsent(f,e->new ConcurrentHashMap<>()).put(session.getId(),session)
		);

	}

	@OnClose
	@Update
	public void close(@PathParam("authenticationKey") String authenticationKey, CloseReason c, Session session) {

		if (!validateSecurity(authenticationKey, session)) return;
		SecurityContext securityContext = (SecurityContext) session.getUserProperties().get(SECURITY_CONTEXT_KEY);
		List<?> tenants = securityContext.getTenants();
		tenants.stream().filter(f->f instanceof SecurityTenant).map(f->(SecurityTenant)f).map(Basic::getId).map(sessions::get)
				.filter(f->f!=null).forEach(f-> f.remove(session.getId()));

	}
	@OnError
	@Read
	public void error(@PathParam("authenticationKey") String authenticationKey, Throwable error, Session session) {
		logger.error("error ",error);

	}
}

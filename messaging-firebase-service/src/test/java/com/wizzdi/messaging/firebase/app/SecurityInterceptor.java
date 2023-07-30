package com.wizzdi.messaging.firebase.app;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.messaging.model.ChatUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicReference;

/**
 * this is a fake security interceptor using the admin security context every time used for testing.
 */

@Component
public class SecurityInterceptor implements HandlerInterceptor, InitializingBean {

	private static final String AUTHENTICATION_KEY = "authenticationKey";

	@Autowired
	@Qualifier("adminSecurityContext")
	@Lazy
	private SecurityContextBase<?,?,?,?> securityContextBase;

	private static final AtomicReference<ChatUserSecurityContext> ref=new AtomicReference<>(null);



	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader(AUTHENTICATION_KEY);
		if(token!=null){
			request.setAttribute("securityContext",ref.get());
		}
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ref.set(new ChatUserSecurityContext(securityContextBase));
	}


	public static void setChatUser(ChatUser chatUser){
		ref.get().setChatUser(chatUser);

	}
}

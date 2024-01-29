package com.flexicore.ui.component.rest.app;

import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * this is a fake security interceptor using the admin security context every time used for testing.
 */

@Component
public class SecurityInterceptor implements HandlerInterceptor {

	private static final String AUTHENTICATION_KEY = "authenticationKey";

	@Autowired
	@Qualifier("adminSecurityContext")
	@Lazy
	private SecurityContextBase securityContext;
	@Autowired
	@Lazy
	private SecurityContextProvider securityContextProvider;
	@Autowired
	private SecurityUser user;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader(AUTHENTICATION_KEY);
		if(token!=null){
			request.setAttribute("securityContext",token.equals("user")?securityContextProvider.getSecurityContext(user):securityContext);
		}
		return true;
	}
}

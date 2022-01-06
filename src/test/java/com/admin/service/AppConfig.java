package com.admin.service;

import com.flexicore.security.SecurityContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Autowired
  @Qualifier("adminSecurityContext")
  private SecurityContextBase securityContext;
}

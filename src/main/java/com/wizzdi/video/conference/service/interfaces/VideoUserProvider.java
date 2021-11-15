package com.wizzdi.video.conference.service.interfaces;

import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.video.conference.model.VideoServerUser;

public interface VideoUserProvider<T extends SecurityUser> {

    VideoServerUser getChatUser(SecurityContextBase<?,T,?,?> securityContextBase);
    Class<T> getType();
}

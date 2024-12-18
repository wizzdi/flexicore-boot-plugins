package com.wizzdi.video.conference.service.interfaces;

import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.video.conference.model.VideoServerUser;

public interface VideoUserProvider<T extends SecurityUser> {

    VideoServerUser getVideoServerUser(SecurityContext SecurityContext);
    Class<T> getType();
}

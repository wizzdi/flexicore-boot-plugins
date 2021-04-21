package com.wizzdi.user.profile.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.user.profile.model.Gender;

public class UserProfileCreate extends BasicCreate {

    private String userId;
    @JsonIgnore
    private SecurityUser securityUser;
    private String avatarId;
    @JsonIgnore
    private FileResource avatar;
    private Gender gender;

    public String getUserId() {
        return userId;
    }

    public <T extends UserProfileCreate> T setUserId(String userId) {
        this.userId = userId;
        return (T) this;
    }

    public SecurityUser getSecurityUser() {
        return securityUser;
    }

    public <T extends UserProfileCreate> T setSecurityUser(SecurityUser securityUser) {
        this.securityUser = securityUser;
        return (T) this;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public <T extends UserProfileCreate> T setAvatarId(String avatarId) {
        this.avatarId = avatarId;
        return (T) this;
    }

    public FileResource getAvatar() {
        return avatar;
    }

    public <T extends UserProfileCreate> T setAvatar(FileResource avatar) {
        this.avatar = avatar;
        return (T) this;
    }

    public Gender getGender() {
        return gender;
    }

    public <T extends UserProfileCreate> T setGender(Gender gender) {
        this.gender = gender;
        return (T) this;
    }
}

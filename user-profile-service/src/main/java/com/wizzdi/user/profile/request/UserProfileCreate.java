package com.wizzdi.user.profile.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.user.profile.model.Gender;

import java.util.HashMap;
import java.util.Map;

@IdValid.List({
        @IdValid(field = "userId",targetField = "securityUser",fieldType = SecurityUser.class,groups = {Create.class, Update.class}),
        @IdValid(field = "avatarId",targetField = "avatar",fieldType = FileResource.class,groups = {Create.class, Update.class})
})
public class UserProfileCreate extends BasicCreate {

    private String lastName;
    private String userId;
    @JsonIgnore
    private SecurityUser securityUser;
    private String avatarId;
    @JsonIgnore
    private FileResource avatar;
    private Gender gender;
    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();

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

    public String getLastName() {
        return lastName;
    }

    public <T extends UserProfileCreate> T setLastName(String lastName) {
        this.lastName = lastName;
        return (T) this;
    }

    @JsonIgnore
    public Map<String, Object> getOther() {
        return other;
    }

    public <T extends UserProfileCreate> T setOther(Map<String, Object> other) {
        this.other = other;
        return (T) this;
    }

    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }

    @JsonAnyGetter
    public Object get(String key) {
        return other.get(key);
    }
}

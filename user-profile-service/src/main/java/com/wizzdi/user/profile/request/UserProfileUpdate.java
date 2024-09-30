package com.wizzdi.user.profile.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.user.profile.model.UserProfile;

@IdValid(field = "id",targetField = "userProfile",fieldType = UserProfile.class,groups = {Update.class})
public class UserProfileUpdate extends UserProfileCreate{

    private String id;
    @JsonIgnore
    private UserProfile userProfile;

    public String getId() {
        return id;
    }

    public <T extends UserProfileUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public UserProfile getUserProfile() {
        return userProfile;
    }

    public <T extends UserProfileUpdate> T setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        return (T) this;
    }
}

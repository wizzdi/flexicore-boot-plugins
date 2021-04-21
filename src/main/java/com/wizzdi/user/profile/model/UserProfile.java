package com.wizzdi.user.profile.model;

import com.flexicore.model.Basic;
import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.file.model.FileResource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class UserProfile extends Basic {


    @ManyToOne(targetEntity = FileResource.class)
    private FileResource avatar;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser securityUser;
    private Gender gender;


    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getAvatar() {
        return avatar;
    }

    public <T extends UserProfile> T setAvatar(FileResource avatar) {
        this.avatar = avatar;
        return (T) this;
    }

    @ManyToOne(targetEntity = SecurityUser.class)
    public SecurityUser getSecurityUser() {
        return securityUser;
    }

    public <T extends UserProfile> T setSecurityUser(SecurityUser securityUser) {
        this.securityUser = securityUser;
        return (T) this;
    }

    public Gender getGender() {
        return gender;
    }

    public <T extends UserProfile> T setGender(Gender gender) {
        this.gender = gender;
        return (T) this;
    }
}

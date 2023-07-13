package com.wizzdi.user.profile.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Basic;
import com.flexicore.model.SecurityUser;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.file.model.FileResource;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

@Entity
public class UserProfile extends Basic {


    private String lastName;
    @ManyToOne(targetEntity = FileResource.class)
    private FileResource avatar;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser securityUser;
    private Gender gender;
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();


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

    public String getLastName() {
        return lastName;
    }

    public <T extends UserProfile> T setLastName(String lastName) {
        this.lastName = lastName;
        return (T) this;
    }

    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }


    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    @JsonIgnore
    public Map<String, Object> getOther() {
        return other;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return other;
    }


    public <T extends UserProfile> T setOther(Map<String, Object> other) {
        this.other=other;
        return (T) this;
    }

}

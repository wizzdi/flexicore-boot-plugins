package com.wizzdi.user.profile.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserProfileFilter extends PaginationFilter {
    private Set<String> userIds=new HashSet<>();
    @JsonIgnore
    private List<SecurityUser> users;


    public Set<String> getUserIds() {
        return userIds;
    }

    public <T extends UserProfileFilter> T setUserIds(Set<String> userIds) {
        this.userIds = userIds;
        return (T) this;
    }

    @JsonIgnore
    public List<SecurityUser> getUsers() {
        return users;
    }

    public <T extends UserProfileFilter> T setUsers(List<SecurityUser> users) {
        this.users = users;
        return (T) this;
    }
}

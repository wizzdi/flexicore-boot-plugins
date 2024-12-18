package com.wizzdi.user.profile.service;

import com.flexicore.model.Baseclass;

import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.model.FileResource_;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.flexicore.security.service.SecurityUserService;
import com.wizzdi.user.profile.data.UserProfileRepository;
import com.wizzdi.user.profile.model.UserProfile;
import com.wizzdi.user.profile.request.UserProfileCreate;
import com.wizzdi.user.profile.request.UserProfileFilter;
import com.wizzdi.user.profile.request.UserProfileUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class UserProfileService implements Plugin {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private BasicService basicService;


    public UserProfile createUserProfileNoMerge(UserProfileCreate userProfileCreate) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(UUID.randomUUID().toString());
        updateUserProfileNoMerge(userProfile, userProfileCreate);
        return userProfile;
    }

    public UserProfile createUserProfile(UserProfileCreate userProfileCreate,SecurityContext SecurityContext) {
        UserProfile userProfile = createUserProfileNoMerge(userProfileCreate);
        userProfileRepository.merge(userProfile);
        return userProfile;

    }

    public boolean updateUserProfileNoMerge(UserProfile userProfile, UserProfileCreate userProfileCreate) {
        boolean update = basicService.updateBasicNoMerge(userProfileCreate, userProfile);
        if (userProfileCreate.getLastName() != null && !userProfileCreate.getLastName().equals(userProfile.getLastName())) {
            userProfile.setLastName(userProfileCreate.getLastName());
            update = true;
        }
        if (userProfileCreate.getGender() != null && !userProfileCreate.getGender().equals(userProfile.getGender())) {
            userProfile.setGender(userProfileCreate.getGender());
            update = true;
        }

        if (userProfileCreate.getAvatar() != null && (userProfile.getAvatar() == null || !userProfileCreate.getAvatar().getId().equals(userProfile.getAvatar().getId()))) {
            userProfile.setAvatar(userProfileCreate.getAvatar());
            update = true;
        }
        if (userProfileCreate.getSecurityUser() != null && (userProfile.getSecurityUser() == null || !userProfileCreate.getSecurityUser().getId().equals(userProfile.getSecurityUser().getId()))) {
            userProfile.setSecurityUser(userProfileCreate.getSecurityUser());
            update = true;
        }
        Map<String, Object> mergedValues = DynamicPropertiesUtils.updateDynamic(userProfileCreate.getOther(), userProfile.getOther());

        if (mergedValues != null) {
            userProfile.setOther(mergedValues);
            update = true;
        }
        return update;


    }



    public PaginationResponse<UserProfile> getAllUserProfiles(UserProfileFilter userProfileFilter, SecurityContext SecurityContext) {
        List<UserProfile> list = listAllUserProfiles(userProfileFilter, SecurityContext);
        long count = userProfileRepository.countAllUserProfiles(userProfileFilter, SecurityContext);
        return new PaginationResponse<>(list, userProfileFilter, count);
    }

    public List<UserProfile> listAllUserProfiles(UserProfileFilter userProfileFilter, SecurityContext SecurityContext) {
        return userProfileRepository.listAllUserProfiles(userProfileFilter, SecurityContext);
    }


    public <T> T findByIdOrNull(Class<T> type, String id) {
        return userProfileRepository.findByIdOrNull(type, id);
    }

    public void merge(Object base) {
        userProfileRepository.merge(base);
    }

    public void massMerge(List<?> toMerge) {
        userProfileRepository.massMerge(toMerge);
    }

    public UserProfile updateUserProfile(UserProfileUpdate userProfileUpdate, SecurityContext securityContext) {
        UserProfile userProfile = userProfileUpdate.getUserProfile();
        if(updateUserProfileNoMerge(userProfile, userProfileUpdate)){
            merge(userProfile);
        }
        return userProfile;
    }


}

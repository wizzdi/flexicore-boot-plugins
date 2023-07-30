package com.wizzdi.user.profile.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
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
    @Autowired
    private SecurityUserService securityUserService;
    @Autowired
    private FileResourceService fileResourceService;


    public UserProfile createUserProfileNoMerge(UserProfileCreate userProfileCreate) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(Baseclass.getBase64ID());
        updateUserProfileNoMerge(userProfile, userProfileCreate);
        return userProfile;
    }

    public UserProfile createUserProfile(UserProfileCreate userProfileCreate,SecurityContextBase securityContextBase) {
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

    public void validate(UserProfileCreate userProfileCreate, SecurityContextBase securityContextBase) {
        basicService.validate(userProfileCreate, securityContextBase);
        String userId = userProfileCreate.getUserId();
        SecurityUser user= userId!=null?securityUserService.getByIdOrNull(userId,SecurityUser.class,securityContextBase):null;
        if(userId!=null&&user==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no user with id "+userId);
        }
        userProfileCreate.setSecurityUser(user);

        String avatarId = userProfileCreate.getAvatarId();
        FileResource avatar= avatarId!=null?fileResourceService.getByIdOrNull(avatarId,FileResource.class, FileResource_.security,securityContextBase):null;
        if(avatarId!=null&&avatar==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no file resource  with id "+avatarId);
        }
        userProfileCreate.setAvatar(avatar);
    }

    public void validate(UserProfileFilter userProfileFilter, SecurityContextBase securityContextBase) {
        basicService.validate(userProfileFilter, securityContextBase);
        Set<String> userIds = userProfileFilter.getUserIds();
        Map<String,SecurityUser> userMap= userIds.isEmpty()?new HashMap<>():securityUserService.listByIds(SecurityUser.class,userIds,securityContextBase).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        userIds.removeAll(userMap.keySet());
        if(!userIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no users with ids "+userIds);
        }
        userProfileFilter.setUsers(new ArrayList<>(userMap.values()));

    }

    public PaginationResponse<UserProfile> getAllUserProfiles(UserProfileFilter userProfileFilter, SecurityContextBase securityContextBase) {
        List<UserProfile> list = listAllUserProfiles(userProfileFilter, securityContextBase);
        long count = userProfileRepository.countAllUserProfiles(userProfileFilter, securityContextBase);
        return new PaginationResponse<>(list, userProfileFilter, count);
    }

    public List<UserProfile> listAllUserProfiles(UserProfileFilter userProfileFilter, SecurityContextBase securityContextBase) {
        return userProfileRepository.listAllUserProfiles(userProfileFilter, securityContextBase);
    }


    public <T> T findByIdOrNull(Class<T> type, String id) {
        return userProfileRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        userProfileRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        userProfileRepository.massMerge(toMerge);
    }

    public UserProfile updateUserProfile(UserProfileUpdate userProfileUpdate, SecurityContextBase securityContext) {
        UserProfile userProfile = userProfileUpdate.getUserProfile();
        if(updateUserProfileNoMerge(userProfile, userProfileUpdate)){
            merge(userProfile);
        }
        return userProfile;
    }

    public void validateCreate(UserProfileCreate userProfileCreate, SecurityContextBase securityContext) {
        validate(userProfileCreate,securityContext);
        if(userProfileCreate.getSecurityUser()==null){
            userProfileCreate.setSecurityUser(securityContext.getUser());
        }
    }
}

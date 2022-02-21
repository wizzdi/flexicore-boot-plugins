package com.wizzdi.user.profile.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.user.profile.model.UserProfile;
import com.wizzdi.user.profile.request.UserProfileCreate;
import com.wizzdi.user.profile.request.UserProfileFilter;
import com.wizzdi.user.profile.request.UserProfileUpdate;
import com.wizzdi.user.profile.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/userProfile")
@Extension
@Tag(name = "userProfile")
@OperationsInside
public class UserProfileController implements Plugin {

	@Autowired
	private UserProfileService userProfileService;

	@PostMapping("/createUserProfile")
	@Operation(description = "creates UserProfile",summary = "creates UserProfile")
	public UserProfile createUserProfile( @RequestBody UserProfileCreate userProfileCreate, @RequestAttribute SecurityContextBase securityContext){
		userProfileService.validateCreate(userProfileCreate,securityContext);
		return userProfileService.createUserProfile(userProfileCreate,securityContext);
	}

	@PostMapping("/getAllUserProfiles")
	@Operation(description = "returns UserProfiles",summary = "returns UserProfiles")

	public PaginationResponse<UserProfile> getAllUserProfiles( @RequestBody
			UserProfileFilter userProfileFilter, @RequestAttribute SecurityContextBase securityContext){
		userProfileService.validate(userProfileFilter,securityContext);
		return userProfileService.getAllUserProfiles(userProfileFilter,securityContext);
	}

	@PutMapping("/updateUserProfile")
	@Operation(description = "updates UserProfile",summary = "updates UserProfile")

	public UserProfile updateUserProfile( @RequestBody UserProfileUpdate userProfileUpdate, @RequestAttribute SecurityContextBase securityContext){
		String id=userProfileUpdate.getId();
		UserProfile userProfile=id!=null? userProfileService.findByIdOrNull(UserProfile.class,id):null;
		if(userProfile==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no UserProfile with id "+id);
		}
		if(!userProfile.getSecurityUser().getId().equals(securityContext.getUser().getId())){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"not allowed to change userProfile "+id);

		}
		userProfileUpdate.setUserProfile(userProfile);
		userProfileService.validate(userProfileUpdate,securityContext);
		return userProfileService.updateUserProfile(userProfileUpdate,securityContext);
	}
}

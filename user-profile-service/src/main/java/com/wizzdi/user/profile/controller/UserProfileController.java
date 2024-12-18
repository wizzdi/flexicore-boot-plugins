package com.wizzdi.user.profile.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.user.profile.model.UserProfile;
import com.wizzdi.user.profile.request.UserProfileCreate;
import com.wizzdi.user.profile.request.UserProfileFilter;
import com.wizzdi.user.profile.request.UserProfileUpdate;
import com.wizzdi.user.profile.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

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
	public UserProfile createUserProfile(@RequestBody @Validated(Create.class) UserProfileCreate userProfileCreate,
										 @RequestAttribute SecurityContext securityContext){
		if(userProfileCreate.getSecurityUser()==null){
			userProfileCreate.setSecurityUser(securityContext.getUser());
		}
		return userProfileService.createUserProfile(userProfileCreate,securityContext);
	}

	@PostMapping("/getAllUserProfiles")
	@Operation(description = "returns UserProfiles",summary = "returns UserProfiles")

	public PaginationResponse<UserProfile> getAllUserProfiles(@RequestBody
			@Valid UserProfileFilter userProfileFilter, @RequestAttribute SecurityContext securityContext){
		if(userProfileFilter.getUsers()==null||userProfileFilter.getUsers().isEmpty()){
			userProfileFilter.setUsers(Collections.singletonList(securityContext.getUser()));
		}
		return userProfileService.getAllUserProfiles(userProfileFilter,securityContext);
	}

	@GetMapping
	@Operation(description = "returns UserProfile",summary = "returns UserProfile")
	public UserProfile getUserProfile( @RequestAttribute SecurityContext securityContext){
		return userProfileService.listAllUserProfiles(new UserProfileFilter().setUsers(Collections.singletonList(securityContext.getUser())),securityContext).stream().findFirst().orElse(null);
	}


	@PutMapping("/updateUserProfile")
	@Operation(description = "updates UserProfile",summary = "updates UserProfile")

	public UserProfile updateUserProfile(@RequestBody @Validated(Update.class) UserProfileUpdate userProfileUpdate,
										 @RequestAttribute SecurityContext securityContext){
		return userProfileService.updateUserProfile(userProfileUpdate,securityContext);
	}
}

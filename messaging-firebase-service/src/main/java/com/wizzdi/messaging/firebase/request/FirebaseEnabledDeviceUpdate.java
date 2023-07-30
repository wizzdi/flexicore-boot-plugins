package com.wizzdi.messaging.firebase.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;

public class FirebaseEnabledDeviceUpdate extends FirebaseEnabledDeviceCreate {

	private String id;
	@JsonIgnore
	private FirebaseEnabledDevice firebaseEnabledDevice;

	public String getId() {
		return id;
	}

	public <T extends FirebaseEnabledDeviceUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public FirebaseEnabledDevice getFirebaseEnabledDevice() {
		return firebaseEnabledDevice;
	}

	public <T extends FirebaseEnabledDeviceUpdate> T setFirebaseEnabledDevice(FirebaseEnabledDevice firebaseEnabledDevice) {
		this.firebaseEnabledDevice = firebaseEnabledDevice;
		return (T) this;
	}
}

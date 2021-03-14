package com.wizzdi.messaging.firebase.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.firebase.data.FirebaseEnabledDeviceRepository;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceCreate;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceFilter;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceUpdate;
import com.wizzdi.messaging.service.MessageReceiverDeviceService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class FirebaseEnabledDeviceService implements Plugin {



	@Autowired
	private FirebaseEnabledDeviceRepository firebaseEnabledDeviceRepository;
	@Autowired
	private MessageReceiverDeviceService messageReceiverDeviceService;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public FirebaseEnabledDevice createFirebaseEnabledDevice(FirebaseEnabledDeviceCreate firebaseEnabledDeviceCreate, SecurityContextBase securityContext) {
		FirebaseEnabledDevice firebaseEnabledDevice = createFirebaseEnabledDeviceNoMerge(firebaseEnabledDeviceCreate, securityContext);
		firebaseEnabledDeviceRepository.merge(firebaseEnabledDevice);
		return firebaseEnabledDevice;
	}

	public void merge(Object o) {
		firebaseEnabledDeviceRepository.merge(o);
	}

	public void massMerge(List<Object> list) {
		firebaseEnabledDeviceRepository.massMerge(list);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return firebaseEnabledDeviceRepository.listByIds(c, ids, securityContext);
	}

	public FirebaseEnabledDevice createFirebaseEnabledDeviceNoMerge(FirebaseEnabledDeviceCreate firebaseEnabledDeviceCreate, SecurityContextBase securityContext) {
		FirebaseEnabledDevice firebaseEnabledDevice = new FirebaseEnabledDevice();
		firebaseEnabledDevice.setId(Baseclass.getBase64ID());
		updateFirebaseEnabledDeviceNoMerge(firebaseEnabledDeviceCreate, firebaseEnabledDevice);
		return firebaseEnabledDevice;
	}

	public boolean updateFirebaseEnabledDeviceNoMerge(FirebaseEnabledDeviceCreate firebaseEnabledDeviceCreate, FirebaseEnabledDevice firebaseEnabledDevice) {
		boolean update = messageReceiverDeviceService.updateMessageReceiverDeviceNoMerge(firebaseEnabledDeviceCreate, firebaseEnabledDevice);
		return update;
	}

	public FirebaseEnabledDevice updateFirebaseEnabledDevice(FirebaseEnabledDeviceUpdate firebaseEnabledDeviceUpdate, SecurityContextBase securityContext) {
		FirebaseEnabledDevice firebaseEnabledDevice = firebaseEnabledDeviceUpdate.getFirebaseEnabledDevice();
		if (updateFirebaseEnabledDeviceNoMerge(firebaseEnabledDeviceUpdate, firebaseEnabledDevice)) {
			firebaseEnabledDeviceRepository.merge(firebaseEnabledDevice);

		}
		return firebaseEnabledDevice;
	}

	public void validate(FirebaseEnabledDeviceCreate firebaseEnabledDeviceCreate, SecurityContextBase securityContext) {
		messageReceiverDeviceService.validate(firebaseEnabledDeviceCreate,securityContext);

	}

	public void validate(FirebaseEnabledDeviceFilter firebaseEnabledDeviceFilter, SecurityContextBase securityContext) {
		messageReceiverDeviceService.validate(firebaseEnabledDeviceFilter, securityContext);



	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return firebaseEnabledDeviceRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return firebaseEnabledDeviceRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return firebaseEnabledDeviceRepository.findByIdOrNull(type, id);
	}

	public PaginationResponse<FirebaseEnabledDevice> getAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter FirebaseEnabledDeviceFilter, SecurityContextBase securityContext) {
		List<FirebaseEnabledDevice> list = listAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter, securityContext);
		long count = firebaseEnabledDeviceRepository.countAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter, securityContext);
		return new PaginationResponse<>(list, FirebaseEnabledDeviceFilter, count);
	}

	public List<FirebaseEnabledDevice> listAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter FirebaseEnabledDeviceFilter, SecurityContextBase securityContext) {
		return firebaseEnabledDeviceRepository.listAllFirebaseEnabledDevices(FirebaseEnabledDeviceFilter, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return firebaseEnabledDeviceRepository.findByIds(c, requested);
	}

}

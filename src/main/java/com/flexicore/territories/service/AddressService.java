package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.model.territories.Street;
import com.flexicore.territories.data.AddressRepository;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.data.request.AddressFiltering;
import com.flexicore.territories.interfaces.IAddressService;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.data.request.AddressUpdateContainer;
import com.flexicore.model.territories.Address;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.territories.data.request.AddressCreationContainer;

@PluginInfo(version = 1)
public class AddressService implements IAddressService {

    @Inject
    @PluginInfo(version = 1)
    private AddressRepository repository;
    @Inject
    private Logger logger;

    @Override
    public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
                                                 Class<T> c, List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    @Override
    public Address updateAddress(AddressUpdateContainer updateContainer,
                                 com.flexicore.security.SecurityContext securityContext) {
        Address address = updateContainer.getAddress();
        if (updateAddressNoMerge(address, updateContainer)) {
            repository.merge(address);

        }
        return address;
    }
    @Override
    public void validate(AddressCreationContainer addressCreationContainer, SecurityContext securityContext) {
        Street street = addressCreationContainer.getStreetId()!=null?getByIdOrNull(addressCreationContainer.getStreetId(),
                Street.class, null, securityContext):null;
        if (street == null&&addressCreationContainer.getStreetId()!=null) {
            throw new BadRequestException("no Street with id " + addressCreationContainer.getStreetId());
        }
        addressCreationContainer.setStreet(street);
    }

    private boolean updateAddressNoMerge(Address address, AddressCreationContainer creationContainer) {
        boolean update = false;
        if (creationContainer.getName() != null && !creationContainer.getName().equals(address.getName())) {
            address.setName(creationContainer.getName());
            update = true;
        }
        if (creationContainer.getDescription() != null && !creationContainer.getDescription().equals(address.getDescription())) {
            address.setDescription(creationContainer.getDescription());
            update = true;
        }
        if (creationContainer.getFloor() != null && !creationContainer.getFloor().equals(address.getFloorForAddress())) {
            address.setFloorForAddress(creationContainer.getFloor());
            update = true;
        }
        if (creationContainer.getNeighbourhood() != null && (address.getNeighbourhood() == null || !creationContainer.getNeighbourhood().getId().equals(address.getNeighbourhood().getId()))) {
            address.setNeighbourhood(creationContainer.getNeighbourhood());
            update = true;
        }
        if (creationContainer.getStreet() != null && (address.getStreet() == null || !creationContainer.getStreet().getId().equals(address.getStreet().getId()))) {
            address.setStreet(creationContainer.getStreet());
            update = true;
        }
        return update;
    }

    @Override
    public PaginationResponse<Address> listAllAddresses(SecurityContext securityContext, AddressFiltering filtering) {
        QueryInformationHolder<Address> queryInfo = new QueryInformationHolder<>(filtering, Address.class, securityContext);
        return new PaginationResponse<>(repository.getAllFiltered(queryInfo), filtering, repository.countAllFiltered(queryInfo));
    }

    @Override
    public Address createAddress(AddressCreationContainer creationContainer,
                                 com.flexicore.security.SecurityContext securityContext) {
        Address address = createAddressNoMerge(creationContainer, securityContext);
        repository.merge(address);
        return address;
    }

    private Address createAddressNoMerge(AddressCreationContainer creationContainer, SecurityContext securityContext) {

        Address address = Address.s().CreateUnchecked(creationContainer.getName(), securityContext);
        address.Init();
        updateAddressNoMerge(address, creationContainer);
        return address;
    }


}
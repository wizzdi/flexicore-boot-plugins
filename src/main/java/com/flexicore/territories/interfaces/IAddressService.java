package com.flexicore.territories.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Address;
import com.flexicore.territories.data.request.AddressCreationContainer;
import com.flexicore.territories.data.request.AddressFiltering;
import com.flexicore.territories.data.request.AddressUpdateContainer;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IAddressService extends ServicePlugin {
    <T extends Baseclass> T getByIdOrNull(String id,
                                          Class<T> c, List<String> batch, SecurityContext securityContext);

    Address updateAddress(AddressUpdateContainer updateContainer,
                          SecurityContext securityContext);

    void validate(AddressCreationContainer addressCreationContainer, SecurityContext securityContext);

    PaginationResponse<Address> listAllAddresses(SecurityContext securityContext, AddressFiltering filtering);

    Address createAddress(AddressCreationContainer creationContainer,
                          SecurityContext securityContext);
}

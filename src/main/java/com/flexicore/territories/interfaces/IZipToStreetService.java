package com.flexicore.territories.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.ZipToStreet;
import com.flexicore.territories.data.request.ZipToStreetCreationContainer;
import com.flexicore.territories.data.request.ZipToStreetUpdateContainer;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IZipToStreetService extends ServicePlugin {
    <T extends Baseclass> T getByIdOrNull(String id,
                                          Class<T> c, List<String> batch, SecurityContext securityContext);

    ZipToStreet createZipToStreet(
            ZipToStreetCreationContainer creationContainer,
            SecurityContext securityContext);

    void deleteZipToStreet(String ziptostreetid,
                           SecurityContext securityContext);

    List<ZipToStreet> listAllZipToStreets(
            SecurityContext securityContext,
            com.flexicore.territories.data.request.ZipToStreetFiltering filtering);

    ZipToStreet updateZipToStreet(
            ZipToStreetUpdateContainer updateContainer,
            SecurityContext securityContext);
}

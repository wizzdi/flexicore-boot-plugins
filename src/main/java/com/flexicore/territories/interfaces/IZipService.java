package com.flexicore.territories.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Zip;
import com.flexicore.territories.data.request.ZipCreationContainer;
import com.flexicore.territories.data.request.ZipUpdateContainer;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IZipService extends ServicePlugin {
    <T extends Baseclass> T getByIdOrNull(String id,
                                          Class<T> c, List<String> batch, SecurityContext securityContext);

    Zip updateZip(ZipUpdateContainer updateContainer,
                  SecurityContext securityContext);

    Zip createZip(ZipCreationContainer creationContainer,
                  SecurityContext securityContext);

    void deleteZip(String zipid, SecurityContext securityContext);

    List<Zip> listAllZips(
            SecurityContext securityContext,
            com.flexicore.territories.data.request.ZipFiltering filtering);
}

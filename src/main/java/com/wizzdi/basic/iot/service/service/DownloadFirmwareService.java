package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.client.IOTMessage;
import com.wizzdi.basic.iot.client.PublicKeyProvider;
import com.wizzdi.basic.iot.client.PublicKeyResponse;
import com.wizzdi.basic.iot.client.VerificationException;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.service.FileResourceService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import static com.wizzdi.basic.iot.client.BasicIOTClient.SIGNATURE_ALGORITHM;

@Extension
@Component
public class DownloadFirmwareService implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DownloadFirmwareService.class);
    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    private PublicKeyProvider publicKeyProvider;
    @Autowired
    private RemoteService remoteService;


    public ResponseEntity<Resource> download(long offset, long size, String gatewayId, String firmwareInstallationId, String signedId, String remoteAddr) {
        FirmwareUpdateInstallation firmwareUpdateInstallation = fileResourceService.findByIdOrNull(FirmwareUpdateInstallation.class, firmwareInstallationId);
        if (firmwareUpdateInstallation == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot find firmwareUpdateInstallation with id " + firmwareInstallationId);
        }
        if (firmwareUpdateInstallation.getTargetRemote() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid installation , remote was not set");
        }
        Gateway gateway = remoteService.getGateway(firmwareUpdateInstallation.getTargetRemote());
        if (gateway == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot find gateway for given installation's remote ");
        }
        verifySignedId(gatewayId, firmwareUpdateInstallation.getId(), signedId);
        if (firmwareUpdateInstallation.getFirmwareInstallationState() != FirmwareInstallationState.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "installation is not pending");
        }
        if (firmwareUpdateInstallation.getFirmwareUpdate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no firmware update mentioned");
        }
        if (firmwareUpdateInstallation.getFirmwareUpdate().getFileResource() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no file resource for firmware update");
        }
        return fileResourceService.download(offset, size, firmwareUpdateInstallation.getFirmwareUpdate().getFileResource().getId(), remoteAddr, null);
    }


    private void verifySignedId(String gatewayId, String id, String signature) {
        PublicKeyResponse publicKeyResponse = publicKeyProvider.getPublicKey(gatewayId);
        if(!publicKeyResponse.signatureMandatory()){
            return;
        }
        PublicKey publicKey = publicKeyResponse.publicKey();

        if (publicKey == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find public key for gateway " + gatewayId);
        }
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(id.getBytes(StandardCharsets.UTF_8));
            boolean verify = sig.verify(Base64.getDecoder().decode(signature));
            if (!verify) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "download verify failed");

            }


        } catch (Throwable e) {
            logger.error("failed verification", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "failed verification");
        }


    }


}

package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "device_type_idx",columnList = "name")
})
public class DeviceType extends SecuredBasic {
}

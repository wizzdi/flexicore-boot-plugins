package com.wizzdi.basic.iot.model;

/**
 * Defines how the authoritative population of a RemoteGroup is selected.
 */
public enum RemoteGroupPopulationType {
    /** Membership is managed only through the RemoteGroupToRemote API. */
    STATIC,
    /** All Devices referencing sourceDeviceType are materialized as explicit memberships. */
    DEVICE_TYPE
}

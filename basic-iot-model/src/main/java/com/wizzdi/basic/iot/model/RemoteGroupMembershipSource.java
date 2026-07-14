package com.wizzdi.basic.iot.model;

/**
 * Identifies who owns a RemoteGroupToRemote membership lifecycle.
 */
public enum RemoteGroupMembershipSource {
    /** Membership was explicitly created through the API. */
    MANUAL,
    /** Membership is a materialized projection of Device.deviceType. */
    DEVICE_TYPE
}

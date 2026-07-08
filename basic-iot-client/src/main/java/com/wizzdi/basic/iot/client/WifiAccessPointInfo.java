package com.wizzdi.basic.iot.client;

/**
 * Wi-Fi access-point fingerprint sent by a gateway during registration.
 * macAddress is the BSSID. signalStrength is RSSI in dBm.
 */
public class WifiAccessPointInfo {

    private String macAddress;
    private Integer signalStrength;
    private Integer channel;
    private Integer frequencyMhz;

    public String getMacAddress() {
        return macAddress;
    }

    public <T extends WifiAccessPointInfo> T setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return (T) this;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public <T extends WifiAccessPointInfo> T setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
        return (T) this;
    }

    public Integer getChannel() {
        return channel;
    }

    public <T extends WifiAccessPointInfo> T setChannel(Integer channel) {
        this.channel = channel;
        return (T) this;
    }

    public Integer getFrequencyMhz() {
        return frequencyMhz;
    }

    public <T extends WifiAccessPointInfo> T setFrequencyMhz(Integer frequencyMhz) {
        this.frequencyMhz = frequencyMhz;
        return (T) this;
    }
}

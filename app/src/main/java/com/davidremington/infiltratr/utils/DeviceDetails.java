package com.davidremington.infiltratr.utils;

import android.os.Build;

import com.davidremington.infiltratr.BuildConfig;

public class DeviceDetails {
    private String deviceId;
    private String osVersion = Build.VERSION.RELEASE;
    private String manufacturer = Build.MANUFACTURER;
    private String brand = Build.BRAND;
    private String device = Build.DEVICE;
    private String model = Build.MODEL;

    public DeviceDetails(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public String getDevice() {
        return device;
    }

    public String getModel() {
        return model;
    }

    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public Integer getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }
}

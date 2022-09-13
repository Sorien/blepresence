package com.sorien.ppbthome;

public enum BtHomeBinarySensorId {
    Generic((byte)0x0F),
    Power((byte)0x10),
    Opening((byte)0x11),
    Battery((byte)0x15),
    BatteryCharging((byte)0x16),
    CarbonMonoxide((byte)0x17),
    Cold((byte)0x18),
    Connectivity((byte)0x19),
    Door((byte)0x1A),
    GarageDoor((byte)0x1B),
    Gas((byte)0x1C),
    Heat((byte)0x1D),
    Light((byte)0x1E),
    Lock((byte)0x1F),
    Moisture((byte)0x20),
    Motion((byte)0x21),
    Moving((byte)0x22),
    Occupancy((byte)0x23),
    Plug((byte)0x24),
    Pressence((byte)0x25),
    Problem((byte)0x26),
    Running((byte)0x27),
    Safety((byte)0x28),
    Smoke((byte)0x29),
    Sound((byte)0x2A),
    Tamper((byte)0x2B),
    Update((byte)0x2C),
    Vibration((byte)0x2D),
    Window((byte)0x2E);

    private final byte code;

    BtHomeBinarySensorId(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return this.code;
    }
}
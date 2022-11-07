package com.sorien.ppbthome;

import android.bluetooth.le.AdvertiseData;

import java.nio.ByteBuffer;
import java.util.UUID;

public class IBeaconAdvertiseDataBuilder {

    private final UUID Uuid;
    private final short Major;
    private final short Minor;
    private final byte Tx;

    public IBeaconAdvertiseDataBuilder(UUID Uuid, short Major, short Minor, byte Tx) {
        this.Uuid = Uuid;
        this.Major = Major;
        this.Minor = Minor;
        this.Tx = Tx;
    }

    private static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public AdvertiseData build() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        ByteBuffer data = ByteBuffer.allocate(24);
        byte[] uuid = asBytes(Uuid);
        data.put(0, (byte) 0x02); // Beacon Identifier
        data.put(1, (byte) 0x15); // Beacon Identifier
        for (int i = 2; i <= 17; i++) {
            data.put(i, uuid[i - 2]); // adding the UUID
        }
        data.put(18, (byte)(Major >>> 8)); // first byte of Major
        data.put(19, (byte)Major); // second byte of Major
        data.put(20, (byte)(Minor >>> 8)); // first minor
        data.put(21, (byte)Minor); // second minor
        data.put(22, Tx); // txPower
        builder.addManufacturerData(0x4c, data.array()); // using google's company ID
        return builder.build();
    }
}

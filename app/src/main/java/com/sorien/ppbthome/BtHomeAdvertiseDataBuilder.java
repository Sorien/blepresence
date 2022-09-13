package com.sorien.ppbthome;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BtHomeAdvertiseDataBuilder {

    public static class Pair {
        private final BtHomeBinarySensorId id;
        private final byte[] value;
        private final byte preamble;

        public Pair(BtHomeBinarySensorId id, byte[] value, byte preamble)
        {
            this.id = id;
            this.value = value;
            this.preamble = preamble;
        }
    }

    private final ArrayList<Pair> binarySensorPairs = new ArrayList<Pair>();

    public BtHomeAdvertiseDataBuilder AddBinarySensorData(BtHomeBinarySensorId Id, boolean Value)
    {
        binarySensorPairs.add(new Pair(Id, new byte[]{(byte)(Value ? 1 : 0)}, (byte)0x02));
        return this;
    }

    public byte[] build() {

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        for (Pair p : binarySensorPairs) {
            try {
                result.write(p.preamble);
                result.write(p.id.getCode());
                result.write(p.value);
            } catch (IOException ignored) {
            }
        }

        return result.toByteArray();
    }
}

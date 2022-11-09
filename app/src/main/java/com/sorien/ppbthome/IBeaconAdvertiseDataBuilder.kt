package com.sorien.ppbthome

import android.bluetooth.le.AdvertiseData
import com.sorien.ppbthome.IBeaconAdvertiseDataBuilder
import java.nio.ByteBuffer
import java.util.*

class IBeaconAdvertiseDataBuilder(private val Uuid: UUID, private val Major: Short, private val Minor: Short, private val Tx: Byte) {
    fun build(): AdvertiseData {
        val builder = AdvertiseData.Builder()
        val data = ByteBuffer.allocate(24)
        val uuid = asBytes(Uuid)
        data.put(0, 0x02.toByte()) // Beacon Identifier
        data.put(1, 0x15.toByte()) // Beacon Identifier
        for (i in 2..17) {
            data.put(i, uuid[i - 2]) // adding the UUID
        }
        data.put(18, (Major ushr 8).toByte()) // first byte of Major
        data.put(19, Major.toByte()) // second byte of Major
        data.put(20, (Minor ushr 8).toByte()) // first minor
        data.put(21, Minor.toByte()) // second minor
        data.put(22, Tx) // txPower
        builder.addManufacturerData(0x4c, data.array()) // using google's company ID
        return builder.build()
    }

    companion object {
        private fun asBytes(uuid: UUID): ByteArray {
            val bb = ByteBuffer.wrap(ByteArray(16))
            bb.putLong(uuid.mostSignificantBits)
            bb.putLong(uuid.leastSignificantBits)
            return bb.array()
        }
    }
}
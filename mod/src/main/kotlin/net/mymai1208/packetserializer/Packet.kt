package net.mymai1208.packetserializer

import net.minecraft.network.PacketByteBuf

interface Packet {
    fun readPacket(buf: PacketByteBuf) {
        throw NotImplementedError("This method is not implemented")
    }

    fun writePacket(buf: PacketByteBuf) {
        throw NotImplementedError("This method is not implemented")
    }
}
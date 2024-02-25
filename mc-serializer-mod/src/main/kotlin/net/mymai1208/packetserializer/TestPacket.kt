package net.mymai1208.packetserializer

import net.mymai1208.packetserializer.annotation.Serializer

@Serializer
data class TestPacket(val map: Map<String, String>)
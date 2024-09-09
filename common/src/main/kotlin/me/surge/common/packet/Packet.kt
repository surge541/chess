package me.surge.common.packet

import org.json.JSONObject
import java.net.Socket
import java.nio.charset.Charset

open class Packet(val id: String, val properties: JSONObject) {

    lateinit var client: Socket

    fun write() = "${properties.put("id", id)}"

    fun respond(packet: Packet) {
        client.getOutputStream().write((packet.write() + '\n').toByteArray(Charset.defaultCharset()))
    }

    companion object {

        fun decode(json: String): Packet {
            val obj = JSONObject(json)

            val id = obj.getString("id")

            return when (id) {
                "login" -> LoginPacket(obj)
                "login-response" -> LoginPacket.LoginResponsePacket(obj)
                "register" -> RegisterPacket(obj)
                "register-response" -> RegisterPacket.RegistrationResponsePacket(obj)
                "gamecreationrequest" -> GameCreationRequestPacket(obj)
                "gamecreationrequest-response" -> GameCreationRequestPacket.GameCreationRequestResponsePacket(obj)
                "cgameupt" -> ClientGameUpdate(obj)
                "gupdatereq" -> GameUpdateRequestPacket(obj)
                "gupdatereq-response" -> GameUpdateRequestPacket.GameUpdateRequestResponsePacket(obj)
                else -> Packet("unknown", obj)
            }
        }

    }

}
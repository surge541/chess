package me.surge.common.packet

import me.surge.common.networking.Connection
import org.json.JSONObject

open class Packet(val id: String, private val properties: JSONObject) {

    lateinit var connection: Connection

    fun write() = "${properties.put("id", id)}"

    fun respond(packet: Packet) {
        //connection.getOutputStream().write((packet.write() + '\n').toByteArray(Charset.defaultCharset()))
        connection.send(packet)
    }

    companion object {

        fun decode(json: String, connection: Connection): Packet {
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
            }.also {
                it.connection = connection
            }
        }

    }

}
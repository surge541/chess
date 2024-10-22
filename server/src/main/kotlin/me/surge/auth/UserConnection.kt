package me.surge.auth

import me.surge.Main
import me.surge.common.networking.Connection
import me.surge.common.packet.Packet
import java.net.Socket

class UserConnection(socket: Socket) : Connection(socket) {

    fun start() {
        while (connected) {
            if (!reader.hasNextLine()) {
                continue
            }

            // get data sent by client
            val accepted = reader.nextLine()

            // translate into a packet object
            val decoded = Packet.decode(accepted, this)

            Main.bus.post(decoded)
        }
    }

}
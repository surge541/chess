package me.surge.client

import me.surge.Main
import me.surge.common.managers.ThreadManager.submit
import me.surge.common.networking.Connection
import me.surge.common.packet.Packet

class ServerConnection(private val address: String, private val port: Int) : Connection(address, port) {

    init {
        Main.logger.info("Connecting to $address:$port")
    }

    fun begin() {
        if (!connected) {
            return
        }

        submit("$address:$port") {
            while (connected) {
                val line = reader.nextLine()
                Main.bus.post(Packet.decode(line, this))
            }
        }
    }

}
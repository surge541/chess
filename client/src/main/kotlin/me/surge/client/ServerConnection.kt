package me.surge.client

import me.surge.Main
import me.surge.common.managers.ThreadManager.submit
import me.surge.common.networking.Connection
import me.surge.common.packet.Packet
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

class ServerConnection(val address: String, val port: Int) : Connection(address, port) {

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
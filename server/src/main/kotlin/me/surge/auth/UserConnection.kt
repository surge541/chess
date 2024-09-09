package me.surge.auth

import me.surge.Main
import me.surge.common.auth.Account
import me.surge.common.packet.Packet
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

class UserConnection(val socket: Socket) {

    lateinit var account: Account

    val reader = Scanner(socket.getInputStream())
    val writer = socket.getOutputStream()
    var running = true

    fun start() {
        while (running) {
            if (!reader.hasNextLine()) {
                continue
            }

            runCatching {
                val accepted = reader.nextLine()
                val decoded = Packet.decode(accepted)

                decoded.client = this.socket

                Main.bus.post(decoded)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun post(packet: Packet) {
        val data = (packet.write() + '\n')

        writer.write(data.toByteArray(Charset.defaultCharset()))
        writer.flush()
    }

    fun close() {
        running = false
        socket.close()
    }

}
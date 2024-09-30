package me.surge.client

import me.surge.Main
import me.surge.common.managers.ThreadManager.submit
import me.surge.common.packet.Packet
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

class Connection(val address: String, val port: Int) {

    var connected = true

    private lateinit var socket: Socket

    lateinit var reader: Scanner
    lateinit var writer: OutputStream

    init {
        Main.logger.info("Connecting to $address:$port")
    }

    fun begin() {
        try {
            socket = Socket(address, port)
        } catch (exception: Exception) {
            exception.printStackTrace()
            connected = false
            return
        }

        reader = Scanner(socket.getInputStream())
        writer = socket.getOutputStream()

        submit("$address:$port") {
            while (connected) {
                val line = reader.nextLine()
                Main.bus.post(Packet.decode(line))
            }
        }
    }

    fun post(packet: Packet) {
        val data = (packet.write() + '\n')

        writer.write(data.toByteArray(Charset.defaultCharset()))
        writer.flush()
    }

}
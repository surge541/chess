package me.surge.client

import me.surge.Main
import me.surge.common.background
import me.surge.common.packet.Packet
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner
import kotlin.concurrent.thread

class Connection(val address: String, val port: Int) {

    var connected = true

    lateinit var socket: Socket

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

        thread { read() }.background(Main.backgroundThreads)
    }

    fun post(packet: Packet) {
        val data = (packet.write() + '\n')

        writer.write(data.toByteArray(Charset.defaultCharset()))
        writer.flush()
    }

    private fun read() {
        while (connected) {
            val line = reader.nextLine()
            Main.bus.post(Packet.decode(line))
        }
    }

}
package me.surge.common.networking

import me.surge.common.packet.Packet
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

open class Connection(var socket: Socket?) {

    lateinit var reader: Scanner
    private lateinit var writer: OutputStream

    val connected: Boolean
        get() = socket != null && !socket!!.isClosed

    init {
        if (socket != null) {
            reader = Scanner(socket!!.getInputStream())
            writer = socket!!.getOutputStream()
        }
    }

    constructor(address: String, port: Int): this(null) {
        try {
            socket = Socket(address, port)
        } catch (exception: Exception) {
            exception.printStackTrace()
            socket = null
            return
        }

        reader = Scanner(socket!!.getInputStream())
        writer = socket!!.getOutputStream()
    }

    fun send(packet: Packet) {
        writer.write((packet.write() + '\n').toByteArray(Charset.defaultCharset()))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Connection) {
            return false
        }

        return socket == other.socket
    }

}
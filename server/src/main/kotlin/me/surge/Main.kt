package me.surge

import me.surge.amalia.Bus
import me.surge.server.Server

object Main {

    val bus = Bus()
    lateinit var server: Server

    var open = true

    @JvmStatic fun main(args: Array<String>) {
        val port: Int = runCatching {
            args[0].toInt()
        }.getOrElse { 5000 }

        server = Server(port).start()
    }

}
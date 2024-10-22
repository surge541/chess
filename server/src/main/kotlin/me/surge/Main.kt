package me.surge

import me.surge.amalia.Bus
import me.surge.gui.GUI
import me.surge.server.Server

object Main {

    val bus = Bus()
    var open = true

    @JvmStatic fun main(args: Array<String>) {
        Server.start()

        GUI.display()
    }

}
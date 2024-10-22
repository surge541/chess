package me.surge.server

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.auth.AuthorisationHandler
import me.surge.common.managers.ThreadManager.loopingThread
import me.surge.common.networking.Connection
import me.surge.common.packet.KeepAlivePacket
import me.surge.gui.GUI

object NetworkHandler {

    private val connections = mutableListOf<MultiConnectionHandler>()

    fun init() {
        loopingThread("connection-checker") {
            connections.forEach { connection ->
                connection.check()
            }

            GUI.update()

            Thread.sleep(1000L)
        }
    }

    fun submitConnection(id: Int, connection: Connection) {
        if (!connections.any { it.id == id }) {
            connections.add(MultiConnectionHandler(id))
        }

        connections.first { it.id == id }.submitConnection(connection)
    }

    data class MultiConnectionHandler(val id: Int) {

        private val connections = hashMapOf<Connection, Long>()

        init {
            Main.bus.subscribe(this)
        }

        fun check() {
            connections.forEach { (connection, time) ->
                // last keep-alive was 10 seconds ago
                if (System.currentTimeMillis() - time > 10000) {
                    connections.remove(connection)
                }
            }

            AuthorisationHandler.fetchId(id)!!.online = connections.isNotEmpty()
        }

        fun submitConnection(connection: Connection) {
            connections[connection] = System.currentTimeMillis()
        }

        @Listener
        fun keepAlive(packet: KeepAlivePacket) {
            val connection = connections.entries.firstOrNull { (connection, _) -> connection == packet.connection }

            if (connection != null) {
                connections[connection.key] = System.currentTimeMillis()
            }
        }

    }

}
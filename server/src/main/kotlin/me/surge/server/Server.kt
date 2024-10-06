package me.surge.server

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.auth.AuthorisationHandler
import me.surge.auth.UserConnection
import me.surge.common.log.Logger
import me.surge.common.managers.ThreadManager
import me.surge.common.managers.ThreadManager.submit
import me.surge.common.packet.LoginPacket
import me.surge.common.packet.RegisterPacket
import me.surge.games.GameManager
import java.net.ServerSocket
import kotlin.concurrent.thread

class Server(private val port: Int) {

    val logger = Logger("SERVER $port")
    private val serverSocket = ServerSocket(port)

    init {
        Main.bus.subscribe(this)
        Main.bus.subscribe(GameManager)
    }

    fun start(): Server {
        logger.info("Running Chess server on port $port")

        GameManager.init()

        thread {
            while (Main.open) {
                val client = serverSocket.accept()
                logger.info("Accepted client from address ${client.inetAddress.hostAddress}")

                submit("thread-${client.inetAddress.hostAddress}") {
                    UserConnection(client).start()
                }
            }

            ThreadManager.destroy()
        }

        return this
    }

    @Listener
    fun login(packet: LoginPacket) {
        logger.info("Login Packet received: ${packet.email}, ${packet.password}")

        val response = AuthorisationHandler.login(
            packet.email,
            packet.password
        )

        packet.respond(LoginPacket.LoginResponsePacket(response.first, response.second))
    }

    @Listener
    fun register(packet: RegisterPacket) {
        logger.info("Registration Packet received: ${packet.email}, ${packet.password}")

        val response = AuthorisationHandler.register(
            packet.username,
            packet.email,
            packet.password
        )

        packet.respond(RegisterPacket.RegistrationResponsePacket(response.first, response.second))
    }

}
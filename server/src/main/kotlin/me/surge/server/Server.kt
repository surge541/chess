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

object Server {

    const val PORT = 5000

    val logger = Logger("SERVER $PORT")
    private val serverSocket = ServerSocket(PORT)

    init {
        Main.bus.subscribe(this)
        Main.bus.subscribe(GameManager)
    }

    fun start(): Server {
        logger.info("Running Chess server on port $PORT")

        GameManager.init()
        NetworkHandler.init()

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

        if (response.first == LoginPacket.LoginStatus.SUCCESS) {
            NetworkHandler.submitConnection(response.second!!.id, packet.connection)
        }

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

        if (response.first == RegisterPacket.RegistrationStatus.SUCCESS) {
            NetworkHandler.submitConnection(response.second!!.id, packet.connection)
        }

        packet.respond(RegisterPacket.RegistrationResponsePacket(response.first, response.second))
    }

}
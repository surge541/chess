package me.surge

import me.surge.amalia.Bus
import me.surge.server.Server

object Main {

    val bus = Bus()
    lateinit var server: Server
    val backgroundThreads = mutableListOf<Thread>()

    var open = true

    @JvmStatic fun main(args: Array<String>) {
        /*val game = JSONObject().put("embed-game", ChessGame.embed(
            ChessGame(
                0,
                PublicAccountDetails(0, "AccountA"),
                PublicAccountDetails(1, "AccountB")
            )
        ))

        println(game.toString(4))

        return*/

        val port: Int = runCatching {
            args[0].toInt()
        }.getOrElse { 5000 }

        server = Server(port).start()
    }

}
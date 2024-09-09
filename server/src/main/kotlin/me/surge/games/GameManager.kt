package me.surge.games

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.auth.AuthorisationHandler
import me.surge.common.auth.Account
import me.surge.common.background
import me.surge.common.chess.ChessGame
import me.surge.common.chess.Side
import me.surge.common.packet.ClientGameUpdate
import me.surge.common.packet.GameCreationRequestPacket
import me.surge.common.packet.GameUpdateRequestPacket
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import kotlin.random.Random

object GameManager {

    val games = mutableListOf<ChessGame>()
    val matchmaking = CopyOnWriteArrayList<MatchRequest>()

    val matchmakingThread = thread(name = "matchmaking") {
        while (true) {
            matchmaking.forEach { request ->
                val validSides = when (request.packet.requestedSide) {
                    Side.EITHER -> listOf(Side.EITHER, Side.BLACK, Side.WHITE)
                    Side.WHITE -> listOf(Side.EITHER, Side.BLACK)
                    Side.BLACK -> listOf(Side.EITHER, Side.WHITE)
                }

                val opponent = matchmaking.firstOrNull {
                    !it.met && // not marked as having found a match
                    it.account != request.account && // not the current account
                    it.requested in validSides
                }

                if (opponent != null) {
                    Main.server.logger.info("Creating match between ${request.account.username} and ${opponent.account.username}")

                    val game = when (request.requested) {
                        Side.WHITE -> {
                            ChessGame(games.size, request.account.public, opponent.account.public)
                        }

                        Side.BLACK -> {
                            ChessGame(games.size, opponent.account.public, request.account.public)
                        }

                        Side.EITHER -> {
                            if (Random.nextInt(2) == 0) {
                                ChessGame(games.size, request.account.public, opponent.account.public)
                            } else {
                                ChessGame(games.size, opponent.account.public, request.account.public)
                            }
                        }
                    }

                    games.add(game)

                    request.packet.respond(GameCreationRequestPacket.GameCreationRequestResponsePacket(
                        request.packet.account,
                        game
                    ))

                    opponent.packet.respond(GameCreationRequestPacket.GameCreationRequestResponsePacket(
                        opponent.packet.account,
                        game
                    ))

                    request.met = true
                    opponent.met = true
                }
            }

            matchmaking.removeIf { it.met }

            Thread.sleep(500L)
        }
    }.background(Main.backgroundThreads)

    @Listener
    fun gameCreationRequest(packet: GameCreationRequestPacket) {
        Main.server.logger.info("Creating random matchmake for ${packet.account.username}")

        // remove any existing matchmaking requests
        matchmaking.removeIf { it.account == packet.account }

        matchmaking.add(MatchRequest(packet.account, packet.requestedSide, packet))
    }

    @Listener
    fun clientGameUpdate(packet: ClientGameUpdate) {
        Main.server.logger.info("Client Game Update on game ${packet.gameId}")

        with(games.first { it.id == packet.gameId }) {
            update(packet.move!!)
        }
    }

    @Listener
    fun gameUpdateRequest(packet: GameUpdateRequestPacket) {
        val game = games.firstOrNull { it.id == packet.gameId }

        if (game != null) {
            packet.respond(GameUpdateRequestPacket.GameUpdateRequestResponsePacket(game))
        }
    }

    data class MatchRequest(val account: Account, val requested: Side, val packet: GameCreationRequestPacket) {

        var met = false

    }

}
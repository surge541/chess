package me.surge.games

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.auth.AuthorisationHandler
import me.surge.common.auth.PublicAccountDetails
import me.surge.common.chess.ChessGame
import me.surge.common.chess.Side
import me.surge.common.managers.ThreadManager.loopingThread
import me.surge.common.packet.ClientGameUpdate
import me.surge.common.packet.GameCreationRequestPacket
import me.surge.common.packet.GameUpdateRequestPacket
import me.surge.server.Server
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random

object GameManager {

    private val games = mutableListOf<ChessGame>()
    private val matchmaking = CopyOnWriteArrayList<MatchRequest>()

    fun init() {
        loopingThread("matchmaking") {
            matchmaking.forEach { request ->
                val validSides = when (request.packet.requestedSide) {
                    Side.EITHER -> listOf(Side.EITHER, Side.BLACK, Side.WHITE)
                    Side.WHITE -> listOf(Side.EITHER, Side.BLACK)
                    Side.BLACK -> listOf(Side.EITHER, Side.WHITE)
                }

                val opponent = matchmaking.firstOrNull {
                    it.account != request.account && // not the current account
                            it.requested in validSides
                }

                if (opponent != null) {
                    Server.logger.info("Creating match between ${request.account.username} and ${opponent.account.username}")

                    val game = when (request.requested) {
                        Side.WHITE -> {
                            ChessGame(games.size, request.account, opponent.account)
                        }

                        Side.BLACK -> {
                            ChessGame(games.size, opponent.account, request.account)
                        }

                        Side.EITHER -> {
                            if (Random.nextInt(2) == 0) {
                                ChessGame(games.size, request.account, opponent.account)
                            } else {
                                ChessGame(games.size, opponent.account, request.account)
                            }
                        }
                    }

                    games.add(game)

                    AuthorisationHandler.updateGame(request.packet.accountDetails.id, game)
                    AuthorisationHandler.updateGame(opponent.packet.accountDetails.id, game)

                    request.packet.respond(GameCreationRequestPacket.GameCreationRequestResponsePacket(
                        AuthorisationHandler.fetchId(request.packet.accountDetails.id)!!.public
                    ))

                    opponent.packet.respond(GameCreationRequestPacket.GameCreationRequestResponsePacket(
                        AuthorisationHandler.fetchId(opponent.packet.accountDetails.id)!!.public
                    ))

                    matchmaking.remove(request)
                    matchmaking.remove(opponent)
                }
            }

            Thread.sleep(500L)
        }
    }

    @Listener
    fun gameCreationRequest(packet: GameCreationRequestPacket) {
        Server.logger.info("Creating random matchmake for ${packet.accountDetails.username}")

        // remove any existing matchmaking requests
        matchmaking.removeIf { it.account == packet.accountDetails }

        matchmaking.add(MatchRequest(packet.accountDetails, packet.requestedSide, packet))
    }

    @Listener
    fun clientGameUpdate(packet: ClientGameUpdate) {
        Server.logger.info("Client Game Update on game ${packet.gameId}")

        with(games.first { it.id == packet.gameId }) {
            update(packet.move!!)
        }
    }

    @Listener
    fun gameUpdateRequest(packet: GameUpdateRequestPacket) {
        val accountDetails = AuthorisationHandler.fetchId(packet.accountId)

        if (accountDetails != null) {
            packet.respond(GameUpdateRequestPacket.GameUpdateRequestResponsePacket(accountDetails.public))
        }
    }

    data class MatchRequest(val account: PublicAccountDetails, val requested: Side, val packet: GameCreationRequestPacket)

}
package me.surge.common.packet

import me.surge.common.chess.ChessGame
import org.json.JSONObject

class GameUpdateRequestPacket(json: JSONObject) : Packet("gupdatereq", json) {

    val gameId = json.getInt("gameId")

    constructor(gameId: Int) : this(JSONObject(mapOf(
        "gameId" to gameId
    )))

    class GameUpdateRequestResponsePacket(json: JSONObject) : Packet("gupdatereq-response", json) {

        val game = ChessGame.extract("game", json)

        constructor(game: ChessGame) : this(JSONObject(mapOf(
            "game" to ChessGame.embed(game)
        )))

    }

}
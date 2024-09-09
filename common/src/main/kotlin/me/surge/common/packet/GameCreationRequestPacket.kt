package me.surge.common.packet

import me.surge.common.auth.Account
import me.surge.common.chess.ChessGame
import me.surge.common.chess.Side
import org.json.JSONObject

class GameCreationRequestPacket(json: JSONObject) : Packet("gamecreationrequest", json) {

    val account = Account.extract("account", json)!!
    val requestedSide = json.getEnum(Side::class.java, "requestedSide")!!

    constructor(account: Account, requestedSide: Side): this(JSONObject(mapOf(
        "account" to Account.embed(account),
        "requestedSide" to requestedSide
    )))

    class GameCreationRequestResponsePacket(json: JSONObject) : Packet("gamecreationrequest-response", json) {

        val account = Account.extract("account", json)
        val game = ChessGame.extract("game", json)

        constructor(account: Account, game: ChessGame): this(JSONObject(mapOf(
            "account" to Account.embed(account),
            "game" to ChessGame.embed(game)
        )))

    }

}
package me.surge.common.packet

import me.surge.common.chess.Move
import org.json.JSONObject

class ClientGameUpdate(json: JSONObject) : Packet("cgameupt", json) {

    val gameId = json.getInt("gameId")
    val move = Move.extract("move", json)

    constructor(gameId: Int, move: Move) : this(JSONObject(mapOf(
        "gameId" to gameId,
        "move" to Move.embed(move)
    )))

}
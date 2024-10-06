package me.surge.common.auth

import me.surge.common.chess.ChessGame
import me.surge.common.chess.Side
import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class PublicAccountDetails(val id: Int, val username: String, val online: Boolean, val game: ChessGame?, val side: Side?) {

    override fun toString() = "$username [$id] (${if (online) "online" else "offline"})\n$side\n$game"

    companion object : IEmbeddable<PublicAccountDetails> {

        override fun extract(key: String?, json: JSONObject): PublicAccountDetails? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val id = obj.getInt("id")
            val username = obj.getString("username")
            val online = obj.getBoolean("online")

            val game = ChessGame.extract("game", obj)

            val side = obj.optEnum(Side::class.java, "side")

            return PublicAccountDetails(id, username, online, game, side)
        }

        override fun embed(obj: PublicAccountDetails): JSONObject = JSONObject()
            .put("id", obj.id)
            .put("username", obj.username)
            .put("online", obj.online)
            .put("game", if (obj.game == null) null else ChessGame.embed(obj.game))
            .put("side", obj.side)

    }

}

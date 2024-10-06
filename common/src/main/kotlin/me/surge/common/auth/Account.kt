package me.surge.common.auth

import me.surge.common.chess.ChessGame
import me.surge.common.chess.Side
import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class Account(val id: Int, val email: String, val username: String, val password: String) {

    var online = false

    var game: ChessGame? = null
    var side: Side? = null

    val public: PublicAccountDetails
        get() = PublicAccountDetails(id, username, online, game, side)

    companion object : IEmbeddable<Account> {

        override fun embed(obj: Account): JSONObject = JSONObject()
            .put("id", obj.id)
            .put("email", obj.email)
            .put("username", obj.username)
            .put("password", obj.password)
            .put("online", obj.online)
            .put("game", if (obj.game == null) null else ChessGame.embed(obj.game!!))
            .put("side", obj.side)

        override fun extract(key: String?, json: JSONObject): Account? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val id = obj.getInt("id")
            val email = obj.getString("email")
            val username = obj.getString("username")
            val password = obj.getString("password")
            val online = obj.getBoolean("online")
            val game = ChessGame.extract("game", json)
            val side = obj.optEnum(Side::class.java, "side")

            return Account(id, email, username, password).also {
                it.online = online
                it.game = game
                it.side = side
            }
        }

    }

}
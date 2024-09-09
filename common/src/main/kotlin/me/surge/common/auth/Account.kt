package me.surge.common.auth

import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class Account(val id: Int, val email: String, val username: String, val password: String) {

    var gameId = -1

    val public: PublicAccountDetails
        get() = PublicAccountDetails(id, username)

    companion object : IEmbeddable<Account> {

        override fun embed(obj: Account) = JSONObject()
            .put("id", obj.id)
            .put("email", obj.email)
            .put("username", obj.username)
            .put("password", obj.password)
            .put("gameId", obj.gameId)

        override fun extract(key: String?, json: JSONObject): Account? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val id = obj.getInt("id")
            val email = obj.getString("email")
            val username = obj.getString("username")
            val password = obj.getString("password")
            val gameId = obj.getInt("gameId")

            return Account(id, email, username, password).also { it.gameId = gameId }
        }

    }

}
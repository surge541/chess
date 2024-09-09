package me.surge.common.auth

import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class PublicAccountDetails(val id: Int, val username: String) {

    override fun toString() = "$username [$id]"

    companion object : IEmbeddable<PublicAccountDetails> {

        override fun extract(key: String?, json: JSONObject): PublicAccountDetails? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val id = obj.getInt("id")
            val username = obj.getString("username")

            return PublicAccountDetails(id, username)
        }

        override fun embed(obj: PublicAccountDetails): JSONObject = JSONObject()
            .put("id", obj.id)
            .put("username", obj.username)

    }

}

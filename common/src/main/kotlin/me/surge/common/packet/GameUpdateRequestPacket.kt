package me.surge.common.packet

import me.surge.common.auth.PublicAccountDetails
import org.json.JSONObject

class GameUpdateRequestPacket(json: JSONObject) : Packet("gupdatereq", json) {

    val accountId = json.getInt("accountId")

    constructor(accountId: Int) : this(JSONObject(mapOf(
        "accountId" to accountId
    )))

    class GameUpdateRequestResponsePacket(json: JSONObject) : Packet("gupdatereq-response", json) {

        val accountDetails = PublicAccountDetails.extract("account", json)

        constructor(accountDetails: PublicAccountDetails) : this(JSONObject(mapOf(
            "account" to PublicAccountDetails.embed(accountDetails)
        )))

    }

}
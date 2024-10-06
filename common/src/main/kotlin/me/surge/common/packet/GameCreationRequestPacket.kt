package me.surge.common.packet

import me.surge.common.auth.PublicAccountDetails
import me.surge.common.chess.Side
import org.json.JSONObject

class GameCreationRequestPacket(json: JSONObject) : Packet("gamecreationrequest", json) {

    val accountDetails = PublicAccountDetails.extract("accountDetails", json)!!
    val requestedSide = json.getEnum(Side::class.java, "requestedSide")!!

    constructor(account: PublicAccountDetails, requestedSide: Side): this(JSONObject(mapOf(
        "accountDetails" to PublicAccountDetails.embed(account),
        "requestedSide" to requestedSide
    )))

    class GameCreationRequestResponsePacket(json: JSONObject) : Packet("gamecreationrequest-response", json) {

        val accountDetails = PublicAccountDetails.extract("accountDetails", json)

        constructor(accountDetails: PublicAccountDetails): this(JSONObject(mapOf(
            "accountDetails" to PublicAccountDetails.embed(accountDetails),
        )))

    }

}